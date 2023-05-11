/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.fineract.messagegateway.sms.providers.impl.twilio;

import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(TwilioApiResource.class);
	
	private final SmsOutboundMessageRepository smsOutboundMessageRepository ;
	
	@Autowired
	public TwilioApiResource(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
		this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
	}
	
	@RequestMapping(value = "/report/{messageId}", method = RequestMethod.POST, consumes = {"application/x-www-form-urlencoded"}, produces = {"application/x-www-form-urlencoded"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @ModelAttribute final TwilioReponseData payload) {
    	OutboundMessages message = this.smsOutboundMessageRepository.getById(messageId) ;
    	if(message != null) {
    		logger.info("Status Callback received from Twilio for "+messageId+" with status:"+payload.getMessageStatus());
    		message.setDeliveryStatus(TwilioStatus.smsStatus(payload.getMessageStatus()).getValue());
    		this.smsOutboundMessageRepository.save(message) ;
    	}else {
    		logger.info("Message with Message id "+messageId+" Not found");
    	}
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
