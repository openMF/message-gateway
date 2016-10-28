package org.apache.messagegateway.sms.api;

import java.util.Collection;
import java.util.List;

import org.apache.messagegateway.sms.data.DeliveryStatusData;
import org.apache.messagegateway.sms.domain.SMSMessage;
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
@RequestMapping("/sms")
public class SmsApiResource {

	//This class sends TRANSACTIONAL & PROMOTIONAL SMS
	private SMSMessageService smsMessageService ;
	
	@Autowired
    public SmsApiResource(final SMSMessageService smsMessageService) {
		this.smsMessageService = smsMessageService ;
    }

    @RequestMapping(value = "/{tenantId}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> sendShortMessage(@RequestBody final List<SMSMessage> payload) {
    	this.smsMessageService.sendShortMessage(payload);
       return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<DeliveryStatusData>> getDeliveryStatus(@PathVariable("tenantId") final String tenantId, @RequestBody final Collection<Long> messageIds) {
    	Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryStatus(messageIds) ;
    	return new ResponseEntity<>(deliveryStatus, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{messageId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @RequestBody final String payload) {
    	System.out.println("SmsApiResource.getDeliveryStatus()");
       return new ResponseEntity<>(HttpStatus.OK);
    }
}
