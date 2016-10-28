package org.apache.messagegateway.sms.providers.impl.twilio;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.messagegateway.exception.MessageGatewayException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.providers.SMSProvider;
import org.apache.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.twilio.exception.ApiException;
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

@Service(value="TwilioSMS")
public class TwilioMessageProvider implements SMSProvider {

    private static final Logger logger = LoggerFactory.getLogger(TwilioMessageProvider.class);

    private HashMap<String, ArrayList<TwilioRestClient>> restClients = new HashMap<>() ; //tenantId, twilio clients
    
    private String hostAddress = null;
    
    TwilioMessageProvider() {
        super();
    }

    @Override
    public void sendMessage(final SMSBridge smsBridgeConfig, final SMSMessage message)
        throws MessageGatewayException {
    	if(hostAddress == null) {
    		InetAddress IP;
    		try {
    			IP = InetAddress.getLocalHost();
    			 hostAddress = IP.getHostAddress() ;
    		} catch (UnknownHostException e) {
    			e.printStackTrace();
    		}	
    	}
    	//Based on message id, register call back. so that we get notification from Twilio about message status
    	String statusCallback = "http://"+hostAddress+":9191/sms/"+message.getId() ;
    	
        final List<NameValuePair> messageParams = new ArrayList<NameValuePair>();
        messageParams.add(new BasicNameValuePair("From", smsBridgeConfig.getPhoneNo()));
        messageParams.add(new BasicNameValuePair("To", "+" + message.getMobileNumber()));
        messageParams.add(new BasicNameValuePair("Body", message.getMessage()));
        messageParams.add(new BasicNameValuePair("action", statusCallback)) ;
        final TwilioRestClient twilioRestClient = this.getRestClient(smsBridgeConfig);
        logger.info("Sending SMS to " + message.getMobileNumber() + " ...");
        MessageCreator creator = new MessageCreator(new PhoneNumber(message.getMobileNumber()), new PhoneNumber(smsBridgeConfig.getPhoneNo()) , message.getMessage() ) ;
        creator.setStatusCallback(statusCallback) ;
        try {
        	Message twilioMessage = creator.create(twilioRestClient) ;
        	message.setExternalId(twilioMessage.getSid());
        	message.setDeliveryStatus(TwilioStatus.smsStatus(twilioMessage.getStatus()).getValue()) ;
        }catch (ApiException e) {
        	message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
        	message.setDeliveryErrorMessage(e.getMessage());
        }
    }
    
    private TwilioRestClient getRestClient(final SMSBridge smsBridge) {
    	TwilioRestClient client = null ;
    	ArrayList<TwilioRestClient> tenantsClients = this.restClients.get(smsBridge.getTenantId()) ;
    	if(tenantsClients == null) {
    		tenantsClients = new ArrayList<>() ; 
    		client = this.get(smsBridge) ;
    		tenantsClients.add(client) ;
    		this.restClients.put(smsBridge.getTenantId(), tenantsClients) ;
    	}else {
    		for(TwilioRestClient client1: tenantsClients) {
    			if(client1.getAccountSid().equals(smsBridge.getConfigValue(TwilioMessageConstants.PROVIDER_ACCOUNT_ID))) {
    				client = client1 ;
    				break ;
    			}
    		}
    		if(client == null) {
    			client = this.get(smsBridge) ;
        		tenantsClients.add(client) ;
    		}
    	}
    	return client ;
    }
    
    TwilioRestClient get(final SMSBridge smsBridgeConfig) {
    	String providerAccountId = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_ACCOUNT_ID) ;
    	String providerAuthToken = smsBridgeConfig.getConfigValue(TwilioMessageConstants.PROVIDER_AUTH_TOKEN) ;
        final TwilioRestClient client = new TwilioRestClient.Builder(providerAccountId, providerAuthToken).build();
        return client;
    }
}
