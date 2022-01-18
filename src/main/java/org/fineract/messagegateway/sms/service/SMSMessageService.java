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
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.fineract.messagegateway.service.SecurityService;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProviderFactory;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


	@Autowired
	public SMSMessageService(final SmsOutboundMessageRepository smsOutboundMessageRepository,
							 final SMSProviderFactory smsProviderFactory,
							 final DataSource dataSource,
							 final SecurityService securityService) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
		this.smsProviderFactory = smsProviderFactory ;
		this.jdbcTemplate = new JdbcTemplate(dataSource) ;
		this.securityService = securityService ;
	}

	@PostConstruct
	public void init() {
		logger.debug("Intializing SMSMessage Service.....");
		executorService = Executors.newSingleThreadExecutor();
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor() ;
		scheduledExecutorService.schedule(new BootupPendingMessagesTask(this.smsOutboundMessageRepository, this.smsProviderFactory) , 1, TimeUnit.MINUTES) ;
		//When do I have to shutdown  scheduledExecutorService ? :-( as it is no use after triggering BootupPendingMessagesTask
		//Shutdown scheduledExecutorService on application close event
	}

	public void sendShortMessage(final String tenantId, final String tenantAppKey, final Collection<SMSMessage> messages) {
		logger.debug("Request Received to send messages.....");
		Tenant tenant = this.securityService.authenticate(tenantId, tenantAppKey) ;
		for(SMSMessage message: messages) {
			message.setTenant(tenant.getId());
		}
		this.smsOutboundMessageRepository.saveAll(messages) ;
		this.executorService.execute(new MessageTask(tenant, this.smsOutboundMessageRepository, this.smsProviderFactory, messages));
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

		final Collection<SMSMessage> messages ;
		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		final Tenant tenant ;

		public MessageTask(final Tenant tenant, final SmsOutboundMessageRepository smsOutboundMessageRepository,
						   final SMSProviderFactory smsProviderFactory,
						   final Collection<SMSMessage> messages) {
			this.tenant = tenant ;
			this.messages = messages ;
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
		}

		@Override
		public void run() {
			this.smsProviderFactory.sendShortMessage(messages);
			this.smsOutboundMessageRepository.saveAll(messages) ;
		}
	}

	class BootupPendingMessagesTask implements Callable<Integer> {

		final SmsOutboundMessageRepository smsOutboundMessageRepository ;
		final SMSProviderFactory smsProviderFactory ;
		public BootupPendingMessagesTask(final SmsOutboundMessageRepository smsOutboundMessageRepository,
										 final SMSProviderFactory smsProviderFactory) {
			this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
			this.smsProviderFactory = smsProviderFactory ;
		}

		@Override
		public Integer call() throws Exception {
			logger.info("Sending Pending Messages on bootup.....");
			Integer page = 0;
			Integer initialSize = 200;
			Integer totalPageSize = 0;
			do {
				PageRequest pageRequest = PageRequest.of(page, initialSize);
				logger.info("Reading Pending Messages on bootup.....");
				Page<SMSMessage> messages = this.smsOutboundMessageRepository.findByDeliveryStatus(SmsMessageStatusType.PENDING.getValue(), pageRequest) ;
				logger.info("Pending Messages size.....{}", messages.getTotalElements());
				page++;
				totalPageSize = messages.getTotalPages();
				this.smsProviderFactory.sendShortMessage(messages.getContent());
				this.smsOutboundMessageRepository.saveAll(messages);
			}while (page < totalPageSize);
			return totalPageSize;
		}
	}
}
