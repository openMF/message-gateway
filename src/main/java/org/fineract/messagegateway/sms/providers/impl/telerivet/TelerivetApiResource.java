
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
package org.fineract.messagegateway.sms.providers.impl.telerivet;

import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/telerivet")
public class TelerivetApiResource {

    private static final Logger logger = LoggerFactory.getLogger(TelerivetApiResource.class);

    private final SmsOutboundMessageRepository smsOutboundMessageRepository ;

    @Autowired
    public TelerivetApiResource(final SmsOutboundMessageRepository smsOutboundMessageRepository) {
        this.smsOutboundMessageRepository = smsOutboundMessageRepository ;
    }

    @RequestMapping(value = "/report/{messageId}", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Void> updateDeliveryStatus(@PathVariable("messageId") final Long messageId, @RequestBody final TelerivetResponseData report) {
        SMSMessage message = this.smsOutboundMessageRepository.findById(messageId).get() ;
        if(message != null) {
            logger.debug("Status Callback received from Telerivet for "+messageId +" with status:" + message.getDeliveryStatus());
            message.setDeliveryStatus(TelerivetStatus.smsStatus(report.getMessageStatus()).getValue());
            this.smsOutboundMessageRepository.save(message) ;
        }else {
            logger.info("Message with Message id "+messageId+" Not found");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}