package org.fineract.messagegateway.sms.providers.impl.safaricom;

import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.providers.Provider;

public class SafaricomServiceProvider extends Provider {
    @Override
    public void sendEmail(SMSBridge smsBridgeConfig, OutboundMessages message){

    }
    @Override
    public void sendMessage(SMSBridge smsBridgeConfig, OutboundMessages message){

    }
    @Override
    public void updateStatusByMessageId(SMSBridge bridge, String externalId,String orchestrator) throws MessageGatewayException{

    }
    @Override
    public void publishZeebeVariable(OutboundMessages message){

    }
}
