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
package org.fineract.messagegateway.sms.util;

import org.fineract.messagegateway.sms.data.EnumOptionData;

/** 
 * SMS message enumerations 
 **/
public class SmsMessageEnumerations {
	
	/** 
	 * SMS message delivery status enumeration 
	 * 
	 * @return EnumOptionData object
	 **/
	public static EnumOptionData status(final Integer statusId) {
        return status(SmsMessageStatusType.fromInt(statusId));
    }

	/** 
	 * SMS message delivery status enumeration 
	 * 
	 * @return EnumOptionData object
	 **/
    public static EnumOptionData status(final SmsMessageStatusType status) {
        EnumOptionData optionData = new EnumOptionData(SmsMessageStatusType.INVALID.getValue().longValue(),
                SmsMessageStatusType.INVALID.getCode(), "Invalid");
        
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData(SmsMessageStatusType.INVALID.getValue().longValue(),
                        SmsMessageStatusType.INVALID.getCode(), "Invalid");
            break;
            case PENDING:
                optionData = new EnumOptionData(SmsMessageStatusType.PENDING.getValue().longValue(),
                        SmsMessageStatusType.PENDING.getCode(), "Pending");
            break;
            case SENT:
                optionData = new EnumOptionData(SmsMessageStatusType.SENT.getValue().longValue(), SmsMessageStatusType.SENT.getCode(),
                        "Sent");
            break;
            case DELIVERED:
                optionData = new EnumOptionData(SmsMessageStatusType.DELIVERED.getValue().longValue(),
                        SmsMessageStatusType.DELIVERED.getCode(), "Delivered");
            break;
            case FAILED:
                optionData = new EnumOptionData(SmsMessageStatusType.FAILED.getValue().longValue(), SmsMessageStatusType.FAILED.getCode(),
                        "Failed");
            break;

        }

        return optionData;
    }
}
