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

/** 
 * SMS message delivery status predefined enum constants
 **/
public enum SmsMessageStatusType {
	INVALID(0, "smsMessageStatusType.invalid"), // unknown status type
    PENDING(100, "smsMessageStatusType.pending"), // message received
    WAITING_FOR_REPORT(150, "smsMessageStatusType.pending"),
    SENT(200, "smsMessageStatusType.sent"), // message sent to the SMS gateway
    DELIVERED(300, "smsMessageStatusType.delivered"), // SMS gateway's attempt to deliver message to recipient's phone was successful
    FAILED(400, "smsMessageStatusType.failed"); // SMS gateway's attempt to deliver message to recipient's phone failed
	
	private final Integer value;
    private final String code;

    /** 
     * get enum constant by value
     * 
     * @param statusValue the value of the enum constant
     * @return enum constant
     **/
    public static SmsMessageStatusType fromInt(final Integer statusValue) {

        SmsMessageStatusType enumeration = SmsMessageStatusType.INVALID;
        
        switch (statusValue) {
            case 100:
                enumeration = SmsMessageStatusType.PENDING;
            break;
            
            case 150:
                enumeration = SmsMessageStatusType.WAITING_FOR_REPORT;
            break;
            
            case 200:
                enumeration = SmsMessageStatusType.SENT;
            break;
            case 300:
                enumeration = SmsMessageStatusType.DELIVERED;
            break;
            case 400:
                enumeration = SmsMessageStatusType.FAILED;
            break;
        }
        
        return enumeration;
    }

    /** 
     * SmsMessageStatusType constructor  
     **/
    private SmsMessageStatusType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    /** 
     * @return enum constant value 
     **/
    public Integer getValue() {
        return this.value;
    }

    /** 
     * @return enum constant 
     **/
    public String getCode() {
        return this.code;
    }
}
