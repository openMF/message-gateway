package org.apache.messagegateway.sms.providers.impl.twilio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwilioApiResource {

	@RequestMapping(value = "/report/{messageId}", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @ModelAttribute  final TwilioReponseData payload) {
    	System.out.println("SmsApiResource.updateDeliveryStatus()############"+payload);
       return new ResponseEntity<>(HttpStatus.OK);
    }
}
