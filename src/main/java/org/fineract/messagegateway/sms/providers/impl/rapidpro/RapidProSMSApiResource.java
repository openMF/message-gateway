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
package org.fineract.messagegateway.sms.providers.impl.rapidpro;

import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rapidpro")
public class RapidProSMSApiResource {

  private static final Logger logger = LoggerFactory.getLogger(
    RapidProSMSApiResource.class
  );

  private final SmsOutboundMessageRepository smsOutboundMessageRepository;

  @Autowired
  public RapidProSMSApiResource(
    final SmsOutboundMessageRepository smsOutboundMessageRepository
  ) {
    this.smsOutboundMessageRepository = smsOutboundMessageRepository;
  }

  @RequestMapping(
    value = "/report/{messageId}",
    method = RequestMethod.GET,
    consumes = { "application/json" },
    produces = { "application/json" }
  )
  public ResponseEntity<Void> updateDeliveryStatus(
    @PathVariable("messageId") final Long messageId
  ) {
    SMSMessage message = this.smsOutboundMessageRepository.findById(messageId).get();
    if (message != null) {
      logger.info(
        "Status Callback received from RapidProSMS for " +
        messageId
      );
      this.smsOutboundMessageRepository.save(message);
    } else {
      logger.info("Message with Message id " + messageId + " Not found");
    }
    return new ResponseEntity<>(HttpStatus.OK);
  }

  public static SmsMessageStatusType smsStatus(final String messageStatus) {
    SmsMessageStatusType smsStatus = SmsMessageStatusType.PENDING;
    switch (messageStatus) {
      case "ESME_ROK":
        smsStatus = SmsMessageStatusType.DELIVERED;
        break;
      default:
        smsStatus = SmsMessageStatusType.FAILED;
        break;
    }
    return smsStatus;
  }
}
