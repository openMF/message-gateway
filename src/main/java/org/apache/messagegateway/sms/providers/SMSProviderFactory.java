package org.apache.messagegateway.sms.providers;

import java.util.Collection;

import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.exception.ProviderNotDefinedException;
import org.apache.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.apache.messagegateway.sms.repository.SMSBridgeRepository;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.apache.messagegateway.tenants.domain.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SMSProviderFactory implements ApplicationContextAware {

	 private static final Logger logger = LoggerFactory.getLogger(SMSProviderFactory.class);
	 
	private ApplicationContext applicationContext;

	private final SMSBridgeRepository smsBridgeRepository;

	@Autowired
	public SMSProviderFactory(final SMSBridgeRepository smsBridgeRepository) {
		this.smsBridgeRepository = smsBridgeRepository;
	}

	public SMSProvider getSMSProvider(final SMSMessage message) throws SMSBridgeNotFoundException, ProviderNotDefinedException {
		SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
				message.getTenantId());
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(message.getBridgeId());
		}
		SMSProvider provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
		if(provider == null) throw new ProviderNotDefinedException() ;
		return provider ;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void sendShortMessage(final SMSMessage message) {
		SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
				message.getTenantId());
		SMSProvider provider = null;
		try {
			if (bridge == null) {
				throw new SMSBridgeNotFoundException(message.getBridgeId());
			}
			provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
			if (provider == null) throw new ProviderNotDefinedException();
			provider.sendMessage(bridge, message);
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
		} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException | BeansException e) {
			logger.error(e.getMessage());
			message.setDeliveryErrorMessage(e.getMessage());
			message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
		}
	}
	
	public void sendShortMessage(final Collection<SMSMessage> messages) {
		for(SMSMessage message: messages) {
			SMSBridge bridge = this.smsBridgeRepository.findByIdAndTenantId(message.getBridgeId(),
					message.getTenantId());
			SMSProvider provider = null;
			try {
				if (bridge == null) {
					throw new SMSBridgeNotFoundException(message.getBridgeId());
				}
				provider = (SMSProvider) this.applicationContext.getBean(bridge.getProviderKey()) ;
				if (provider == null)
					throw new ProviderNotDefinedException();
				provider.sendMessage(bridge, message);
			} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException | BeansException e) {
				logger.error(e.getMessage());
				message.setDeliveryErrorMessage(e.getMessage());
				message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
			}
		}
	}
}
