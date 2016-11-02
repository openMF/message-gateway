package org.apache.messagegateway.sms.providers.impl.twilio;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.messagegateway.sms.service.SMSMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioApiResource {

	private final SMSMessageService smsMessageService ;
	
	@Autowired
	public TwilioApiResource(final SMSMessageService smsMessageService) {
		this.smsMessageService = smsMessageService ;
	}
	
	@RequestMapping(value = "/report/{messageId}", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @RequestBody final String payload) {
    	System.out.println("SmsApiResource.updateDeliveryStatus()############"+payload);
    	smsMessageService.updateDeliverStatusFromServer(messageId, parseResponse(payload)) ;
       return new ResponseEntity<>(HttpStatus.OK);
    }
	
	//Need to change it to TwilioReponseData
	private Map<String, String> parseResponse(final String payLoad) {
		Map<String, String> response = new HashMap<>() ;
		StringTokenizer tokenizer = new StringTokenizer(payLoad, "&") ;
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken() ;
			StringTokenizer tokenizer1 = new StringTokenizer(token, "=") ;
			String paramName = tokenizer1.nextToken() ;
			String paramValue = tokenizer1.nextToken() ;
			response.put(paramName, paramValue) ;
		}
		return response ;
	}
}
