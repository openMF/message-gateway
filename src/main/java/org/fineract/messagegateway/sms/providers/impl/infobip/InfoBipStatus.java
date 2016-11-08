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
package org.fineract.messagegateway.sms.providers.impl.infobip;


import org.fineract.messagegateway.sms.util.SmsMessageStatusType;


public class InfoBipStatus {

	public static SmsMessageStatusType smsStatus(final String twilioStatus) {
		SmsMessageStatusType smsStatus = SmsMessageStatusType.PENDING;
		switch(twilioStatus) {
		case "PENDING":
		case "ACCEPTED":
			smsStatus = SmsMessageStatusType.WAITING_FOR_REPORT ;
			break ;
		case "sent" :
			smsStatus = SmsMessageStatusType.SENT ;
				break ;
		case "DELIVERED":
			smsStatus = SmsMessageStatusType.DELIVERED;
			break ;
		case "UNDELIVERABLE":
		case "EXPIRED" :
		case "REJECTED":
			smsStatus = SmsMessageStatusType.FAILED ;
			break ;
		}
		return smsStatus ;
	}
	
	public static SmsMessageStatusType smsStatus(final Integer infoBipStatus) {
		SmsMessageStatusType smsStatus = SmsMessageStatusType.PENDING;
		switch(infoBipStatus) {
		case 0:
			smsStatus = SmsMessageStatusType.WAITING_FOR_REPORT ;
		case 1:
			smsStatus = SmsMessageStatusType.SENT ;
			break ;
		case 2:
		case 4:
		case 5:
			smsStatus = SmsMessageStatusType.FAILED;
			break ;
			
		case 3:
			smsStatus = SmsMessageStatusType.DELIVERED;
			break ;
		}
		return smsStatus ;
	}
}
