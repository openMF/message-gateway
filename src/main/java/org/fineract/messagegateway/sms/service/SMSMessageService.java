/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fineract.messagegateway.sms.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.providers.SMSProviderFactory;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class SMSMessageService {

	private static final Logger logger = LoggerFactory.getLogger(SMSMessageService.class);

	private final SmsOutboundMessageRepository smsOutboundMessageRepository ;

	private final SMSProviderFactory smsProviderFactory ;

	private final JdbcTemplate jdbcTemplate ;

	private ExecutorService executorService ;

	private ScheduledExecutorService scheduledExecutorService ;

	private final SecurityService securityService ;

	private final Duration pendingRetryInterval ;

	private final Duration pendingRetryWait ;


	@Autowired
	public SMSMessageService(final SmsOutboundMessageRepository smsOutboundMessageRepository,
							 final SMSProviderFactory smsProviderFactory,
							 final DataSource dataSource,
							 final SecurityService securityService,
							 @Value("${messagegateway.pending-retry-interval:1m}") final Duration pendingRetryInterval,
							 @Value("${messagegateway.pending-retry-wait:2m}") final Duration pendingRetryWait) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
		this.smsProviderFactory = smsProviderFactory ;
		this.jdbcTemplate = new JdbcTemplate(dataSource) ;
		this.securityService = securityService ;
		this.pendingRetryInterval = pendingRetryInterval ;
		this.pendingRetryWait = pendingRetryWait == null || pendingRetryWait.isZero() || pendingRetryWait.isNegative()
				? Duration.ofMinutes(2) : pendingRetryWait ;
	}

	@PostConstruct
	public void init() {
		logger.debug("Intializing OutboundMessages Service.....");
		executorService = Executors.newSingleThreadExecutor();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor() ;
		Duration interval = pendingRetryInterval;
		if (interval == null || interval.isZero() || interval.isNegative()) {
			logger.warn("Pending retry interval {} is not positive. Using 1 minute.", interval);
			interval = Duration.ofMinutes(1);
		}
		long intervalMillis = interval.toMillis();
		logger.info("Scheduling pending message retry every {}. Messages newer than {} are left for immediate sending.", interval, this.pendingRetryWait);
		scheduledExecutorService.scheduleWithFixedDelay(new BootupPendingMessagesTask(this.smsOutboundMessageRepository, this.smsProviderFactory),
				intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
	}

	@PreDestroy
	public void shutdownExecutors() {
		if (scheduledExecutorService != null) {
			scheduledExecutorService.shutdown();
		}
		if (executorService != null) {
			executorService.shutdown();
		}
	}

	public void sendShortMessage(final String tenantId, final String tenantAppKey, final Collection<OutboundMessages> messages) {
		logger.debug("Request Received to send messages.....");
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		for(OutboundMessages message: messages) {
			message.setTenant(tenant.getId());
			if(message.getSubmittedOnDate() == null) {
				message.setSubmittedOnDate(new Date());
			}
		}
		this.smsOutboundMessageRepository.saveAll(messages) ;
		this.executorService.execute(new MessageTask(tenant, this.smsOutboundMessageRepository, this.smsProviderFactory, messages));
	}
	public void sendShortMessageToProvider(final String tenantId, final String tenantAppKey, final Collection<OutboundMessages> messages, final String orchestrator) {
		logger.debug("Request Received to send messages.....");
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		for(OutboundMessages message: messages) {
			message.setTenant(tenant.getId());
		}
		this.executorService.execute(new MessageTask(tenant, this.smsOutboundMessageRepository, this.smsProviderFactory, messages,orchestrator));
	}

	public Collection<DeliveryStatusData> getDeliveryStatus(final String tenantId, final String tenantAppKey, final Collection<Long> internalIds) {
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		DeliveryStatusDataRowMapper mapper = new DeliveryStatusDataRowMapper() ;
		String internaIdString = internalIds.toString() ;
		internaIdString = internaIdString.replace("[", "(") ;
		internaIdString = internaIdString.replace("]", ")") ;
		String query = mapper.schema() + " where m.tenant_id=?"+" and m.internal_id in " +internaIdString;
		Collection<DeliveryStatusData> datas = this.jdbcTemplate.query(query, mapper, new Object[] {tenant.getId()}) ;
		return datas ;
	}

	public Collection<DeliveryStatusData>  getDeliveryCallbackStatus( String externalId) {
		DeliveryStatusDataRowMapper mapper = new DeliveryStatusDataRowMapper() ;
		String query = mapper.schema() +" where m.external_id=?";
		Collection<DeliveryStatusData> datas = this.jdbcTemplate.query(query, mapper, externalId) ;
		return datas ;
	}


	class DeliveryStatusDataRowMapper implements RowMapper<DeliveryStatusData> {

		private final StringBuilder buff = new StringBuilder() ;

		public DeliveryStatusDataRowMapper() {
			buff.append("select internal_id, external_id, delivered_on_date, delivery_status, delivery_error_message, sms_bridge_id, tenant_id from m_outbound_messages m") ;
		}

		public String schema() {
			return buff.toString() ;
		}

		@Override
		public DeliveryStatusData mapRow(ResultSet rs, int rowNum) throws SQLException {
			String internalId = rs.getString("internal_id") ;
			String externalId = rs.getString("external_id") ;
			Date deliveredOnDate = rs.getDate("delivered_on_date") ;
			Integer deliveryStatus = rs.getInt("delivery_status") ;
			String errorMessage = rs.getString("delivery_error_message") ;
			Long bridgeId = Long.valueOf(rs.getInt("sms_bridge_id"));
			Long tenantId = rs.getLong("tenant_id") ;
			DeliveryStatusData data = new DeliveryStatusData(internalId, externalId, deliveredOnDate, deliveryStatus, errorMessage, bridgeId,tenantId) ;
			return data;
		}
	}

	class MessageTask implements Runnable {

		final Collection<OutboundMessages> messages ;
		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		final Tenant tenant ;
		final String orchestrator;
		public MessageTask(final Tenant tenant, final SmsOutboundMessageRepository smsOutboundMessageRepository,
						   final SMSProviderFactory smsProviderFactory,
						   final Collection<OutboundMessages> messages) {
			this.tenant = tenant ;
			this.messages = messages ;
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
			this.orchestrator=null;
		}
		public MessageTask(final Tenant tenant, final SmsOutboundMessageRepository smsOutboundMessageRepository,
						   final SMSProviderFactory smsProviderFactory,
						   final Collection<OutboundMessages> messages, final String orchestrator) {
			this.tenant = tenant ;
			this.messages = messages ;
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
			this.orchestrator = orchestrator;
		}
		@Override
		public void run() {
			List<OutboundMessages> claimedMessages = new ArrayList<OutboundMessages>();
			for (OutboundMessages message : this.messages) {
				if (claimPendingMessage(message)) {
					claimedMessages.add(message);
				} else {
					logger.info("Skipping outbound message {} because another worker already claimed it", message.getId());
				}
			}
			if (claimedMessages.isEmpty()) {
				return;
			}
			try {
				this.smsProviderFactory.sendShortMessage(claimedMessages);
			} catch (RuntimeException ex) {
				logger.error("Sending messages failed", ex);
				for (OutboundMessages message : claimedMessages) {
					if (SmsMessageStatusType.PENDING.getValue().equals(message.getDeliveryStatus())) {
						message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
						message.setDeliveryErrorMessage(ex.getMessage());
					}
				}
			}
			logger.debug("Persisting delivery status. orchestrator={}", this.orchestrator);
			try {
				persistOutboundMessages(claimedMessages);
			} catch (RuntimeException ex) {
				logger.error("Could not persist delivery status", ex);
			}
		}
	}

	private boolean claimPendingMessage(final OutboundMessages message) {
		if (message.getId() == null) {
			return true;
		}
		Date claimedAt = new Date();
		Date staleBefore = new Date(claimedAt.getTime() - this.pendingRetryWait.toMillis());
		int claimed = this.smsOutboundMessageRepository.claimPendingMessage(message.getId(), claimedAt,
				SmsMessageStatusType.PENDING.getValue(), staleBefore);
		if (claimed == 1) {
			message.setClaimedAt(claimedAt);
			return true;
		}
		return false;
	}

	private void persistOutboundMessages(final Collection<OutboundMessages> messages) {
		try {
			this.smsOutboundMessageRepository.saveAll(messages);
		} catch (RuntimeException firstFailure) {
			logger.error("Saving outbound messages failed. Retrying without the provider response body.", firstFailure);
			for (OutboundMessages message : messages) {
				message.setResponse(null);
				String error = message.getDeliveryErrorMessage();
				if (error != null && error.length() > 500) {
					message.setDeliveryErrorMessage(error.substring(0, 500));
				}
			}
			this.smsOutboundMessageRepository.saveAll(messages);
		}
	}

	class BootupPendingMessagesTask implements Runnable {

		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		public BootupPendingMessagesTask(final SmsOutboundMessageRepository smsOutboundMessageRepository,
										 final SMSProviderFactory smsProviderFactory) {
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
		}

		@Override
		public void run() {
			logger.info("Sending Pending Messages.....");
			int processed = 0;
			Set<Long> attemptedIds = new HashSet<Long>();
			try {
				while (true) {
					PageRequest pageRequest = PageRequest.of(0, 200);
					Date createdBefore = new Date(System.currentTimeMillis() - pendingRetryWait.toMillis());
					logger.info("Reading Pending Messages older than {}.....", createdBefore);
					Page<OutboundMessages> messages = this.smsOutboundMessageRepository.findRetryablePendingMessages(SmsMessageStatusType.PENDING.getValue(), createdBefore, pageRequest);
					logger.info("Pending Messages size.....{}", messages.getTotalElements());
					if (!messages.hasContent()) {
						break;
					}
					int alreadyAttempted = 0;
					for (OutboundMessages message : messages.getContent()) {
						Long id = message.getId();
						if (id != null && !attemptedIds.add(id)) {
							alreadyAttempted++;
							continue;
						}
						if (!claimPendingMessage(message)) {
							logger.info("Skipping outbound message {} because another worker already claimed it", id);
							continue;
						}
						try {
							this.smsProviderFactory.sendShortMessage(Collections.singletonList(message));
						} catch (RuntimeException ex) {
							logger.error("Retry failed for outbound message {}", id, ex);
							if (SmsMessageStatusType.PENDING.getValue().equals(message.getDeliveryStatus())) {
								message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
								message.setDeliveryErrorMessage(ex.getMessage());
							}
						}
						try {
							persistOutboundMessages(Collections.singletonList(message));
						} catch (RuntimeException ex) {
							logger.error("Could not persist delivery status for outbound message {}", id, ex);
						}
						processed++;
						logger.info("Retried outbound message {} with delivery status {}", id, message.getDeliveryStatus());
					}
					if (alreadyAttempted == messages.getNumberOfElements()) {
						logger.error("Stopping pending retry. {} messages stayed pending after an attempt and could not be updated.", alreadyAttempted);
						break;
					}
				}
				logger.info("Finished pending message retry. Processed {}", processed);
			} catch (RuntimeException ex) {
				logger.error("Pending message retry stopped with an error. It will run again on the next interval.", ex);
			}
		}
	}
}
