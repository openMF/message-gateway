package org.apache.messagegateway.sms.providers;

import java.util.Collection;

import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.exception.ProviderNotDefinedException;
import org.apache.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.apache.messagegateway.sms.providers.impl.infobip.InfoBipMessageProvider;
import org.apache.messagegateway.sms.providers.impl.twilio.TwilioMessageProvider;
import org.apache.messagegateway.sms.repository.SMSBridgeRepository;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SMSProviderFactory implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private final SMSBridgeRepository providerDetailsRepository;

	@Autowired
	public SMSProviderFactory(final SMSBridgeRepository providerDetailsRepository) {
		this.providerDetailsRepository = providerDetailsRepository;
	}

	public SMSProvider getSMSProvider(final SMSMessage message) throws SMSBridgeNotFoundException, ProviderNotDefinedException {
		SMSBridge bridge = this.providerDetailsRepository.findByIdAndTenantId(message.getProviderId(),
				message.getTenantId());
		if (bridge == null) {
			throw new SMSBridgeNotFoundException(message.getTenantId(), message.getProviderId());
		}
		if (bridge.getProviderName().contains("Twilio")) {
			return this.applicationContext.getBean(TwilioMessageProvider.class);
		} else if (bridge.getProviderName().contains("Twilio")) {
			return this.applicationContext.getBean(InfoBipMessageProvider.class);
		}
		throw new ProviderNotDefinedException() ;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public void sendShortMessage(final SMSMessage message) {
		SMSBridge bridge = this.providerDetailsRepository.findByIdAndTenantId(message.getProviderId(),
				message.getTenantId());
		SMSProvider provider = null;
		try {
			if (bridge == null) {
				throw new SMSBridgeNotFoundException(message.getTenantId(), message.getProviderId());
			}
			if (bridge.getProviderName().contains("Twilio")) {
				provider = this.applicationContext.getBean(TwilioMessageProvider.class);
			} else if (bridge.getProviderName().contains("InfoBip")) {
				provider = this.applicationContext.getBean(InfoBipMessageProvider.class);
			}
			if (provider == null)
				throw new ProviderNotDefinedException();
			provider.sendMessage(bridge, message);
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
		} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException e) {
			message.setDeliveryErrorMessage(e.getMessage());
			message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
		}
	}
	
	public void sendShortMessage(final Collection<SMSMessage> messages) {
		
		for(SMSMessage message: messages) {
			SMSBridge bridge = this.providerDetailsRepository.findByIdAndTenantId(message.getProviderId(),
					message.getTenantId());
			SMSProvider provider = null;
			try {
				if (bridge == null) {
					throw new SMSBridgeNotFoundException(message.getTenantId(), message.getProviderId());
				}
				if (bridge.getProviderName().contains("Twilio")) {
					provider = this.applicationContext.getBean(TwilioMessageProvider.class);
				} else if (bridge.getProviderName().contains("InfoBip")) {
					provider = this.applicationContext.getBean(InfoBipMessageProvider.class);
				}
				if (provider == null)
					throw new ProviderNotDefinedException();
				provider.sendMessage(bridge, message);
				message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
			} catch (SMSBridgeNotFoundException | MessageGatewayException | ProviderNotDefinedException e) {
				message.setDeliveryErrorMessage(e.getMessage());
				message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
			}
		}
	}
}
