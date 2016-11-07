package org.apache.messagegateway.sms.providers.impl.dummy;

import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.providers.SMSProvider;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service(value="Dummy")
public class DummySMSProvider implements SMSProvider{

        @Override public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message) throws MessageGatewayException {
                if(message.getMessage().toUpperCase().contains("DELIVERED")){
                        message.setDeliveryStatus(SmsMessageStatusType.DELIVERED.getValue());
                } else if(message.getMessage().toUpperCase().contains("FAILED")){
                        message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
                        message.setDeliveryErrorMessage("User requested FAILURE for testing");
                } else if(message.getMessage().toUpperCase().contains("PENDING")){
                        message.setDeliveryStatus(SmsMessageStatusType.PENDING.getValue());
                } else if(message.getMessage().toUpperCase().contains("WAITING")){
                        message.setDeliveryStatus(SmsMessageStatusType.WAITING_FOR_REPORT.getValue());
                } else {
                        message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
                        message.setDeliveredOnDate(new Date());
                }
        }
}
