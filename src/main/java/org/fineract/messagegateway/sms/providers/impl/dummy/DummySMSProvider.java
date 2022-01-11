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
package org.fineract.messagegateway.sms.providers.impl.dummy;

import java.util.Date;

import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.springframework.stereotype.Service;

@Service(value="Dummy")
public class DummySMSProvider extends SMSProvider{

	@Override
	public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message) throws MessageGatewayException {
		if (message.getMessage().toUpperCase().contains("DELIVERED")) {
			message.setDeliveryStatus(SmsMessageStatusType.DELIVERED.getValue());
		} else if (message.getMessage().toUpperCase().contains("FAILED")) {
			message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
			message.setDeliveryErrorMessage("User requested FAILURE for testing");
		} else if (message.getMessage().toUpperCase().contains("PENDING")) {
			message.setDeliveryStatus(SmsMessageStatusType.PENDING.getValue());
		} else if (message.getMessage().toUpperCase().contains("WAITING")) {
			message.setDeliveryStatus(SmsMessageStatusType.WAITING_FOR_REPORT.getValue());
		} else {
			message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
			message.setDeliveredOnDate(new Date());
		}
	}

	@Override
	public void updateStatusByMessageId(SMSBridge bridge, String externalId) throws MessageGatewayException {

	}
}
