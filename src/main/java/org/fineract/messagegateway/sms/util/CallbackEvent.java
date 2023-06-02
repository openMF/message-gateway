package org.fineract.messagegateway.sms.util;

import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

public class CallbackEvent extends ApplicationEvent {
    private static final Logger logger = LoggerFactory.getLogger(CallbackEvent.class);

    @Autowired
    private SmsOutboundMessageRepository smsOutboundMessageRepository ;
    private String messageId;
    private OutboundMessages message;



    //Constructor's first parameter must be source
    public CallbackEvent(Object source, SmsOutboundMessageRepository smsOutboundMessageRepository, String messageId)
    {
        super(source);
        this.smsOutboundMessageRepository = smsOutboundMessageRepository;
        this.messageId = messageId;
    }
    public OutboundMessages getMessage() {
        logger.info("Called get message from callback event listener");
        logger.info("Message id is:" + messageId);
        message = this.smsOutboundMessageRepository.findByExternalId(messageId);
        logger.info("Response is:" + message);
        return message;
    }
}
