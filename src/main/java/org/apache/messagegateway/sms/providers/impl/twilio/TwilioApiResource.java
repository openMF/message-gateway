package org.apache.messagegateway.sms.providers.impl.twilio;

import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/twilio")
public class TwilioApiResource {

	private final SmsOutboundMessageRepository smsOutboundMessageRepository ;
	
	@Autowired
	public TwilioApiResource(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
	}
	
	@RequestMapping(value = "/report/{messageId}", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @ModelAttribute final TwilioReponseData payload) {
    	SMSMessage message = this.smsOutboundMessageRepository.findOne(messageId) ;
    	if(message != null) {
    		message.setDeliveryStatus(TwilioStatus.smsStatus(payload.getMessageStatus()).getValue());
    		this.smsOutboundMessageRepository.save(message) ;
    	}else {
    		//log here
    	}
       return new ResponseEntity<>(HttpStatus.OK);
    }
}
