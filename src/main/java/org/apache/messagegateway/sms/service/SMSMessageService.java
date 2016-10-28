package org.apache.messagegateway.sms.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.messagegateway.sms.data.DeliveryStatusData;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.providers.SMSProviderFactory;
import org.apache.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SMSMessageService {

	private final SmsOutboundMessageRepository smsOutboundMessageRepository ;
	
	private final SMSProviderFactory smsProviderFactory ;
	
	@Autowired
	public SMSMessageService(final SmsOutboundMessageRepository smsOutboundMessageRepository,
			final SMSProviderFactory smsProviderFactory) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
		this.smsProviderFactory = smsProviderFactory ;
	}
	
	public void sendShortMessage(final Collection<SMSMessage> messages) {
		Date date = new Date() ;
		for(SMSMessage message: messages) {
			message.setSubmittedOnDate(date);
		}
		this.smsOutboundMessageRepository.save(messages) ;
		this.sendMessage(messages);
	}
	
	public Collection<DeliveryStatusData> getDeliveryStatus(Collection<Long> internalIds) {
		List<DeliveryStatusData> datas = new ArrayList<>() ;
		DeliveryStatusData data = new DeliveryStatusData(new Long(55L), "Aasfasf", new Date(), SmsMessageStatusType.FAILED.getValue(), "This is test credentials") ;
		datas.add(data) ;
		return datas ;
	}
	private void sendMessage(final Collection<SMSMessage> message) {
		this.smsProviderFactory.sendShortMessage(message);
	}
	
	
}
