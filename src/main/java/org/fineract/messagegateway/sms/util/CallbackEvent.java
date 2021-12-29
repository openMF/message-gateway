package org.fineract.messagegateway.sms.util;

import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.springframework.context.ApplicationEvent;

public class CallbackEvent extends ApplicationEvent
{

    private String eventType;
    private SmsOutboundMessageRepository smsOutboundMessageRepository ;
    private String messageId;

    //Constructor's first parameter must be source
    public CallbackEvent(Object source, String eventType, String messageId)
    {
        //Calling this super class constructor is necessary
        super(source);
        this.eventType = eventType;
        this.messageId = messageId;
    }

    public String getEventType() {
        return eventType;
    }


    public SMSMessage getMessage() {

        SMSMessage message = this.smsOutboundMessageRepository.findByExternalId(messageId);
        return message;
    }
}
