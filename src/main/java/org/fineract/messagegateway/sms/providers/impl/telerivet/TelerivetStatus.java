/*
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


import org.fineract.messagegateway.sms.util.SmsMessageStatusType;


public class TelerivetStatus {
    public static SmsMessageStatusType smsStatus(final String telerivetStatus) {
        SmsMessageStatusType smsStatus = SmsMessageStatusType.PENDING;
        switch(telerivetStatus) {
            case "queued":
            case "failed_queued":
                smsStatus = SmsMessageStatusType.WAITING_FOR_REPORT ;
            case "sent":
                smsStatus = SmsMessageStatusType.SENT ;
                break ;
            case "delivered":
                smsStatus = SmsMessageStatusType.DELIVERED;
                break ;
            case "not_delivered":
            case "failed":
                smsStatus = SmsMessageStatusType.FAILED ;
                break ;
        }
        return smsStatus ;
    }

    public static SmsMessageStatusType smsStatus(final Integer telerivetStatus) {
        SmsMessageStatusType smsStatus = SmsMessageStatusType.PENDING;
        switch(telerivetStatus) {
            case 0:
            case 1:
                smsStatus = SmsMessageStatusType.WAITING_FOR_REPORT ;
            case 2:
                smsStatus = SmsMessageStatusType.SENT ;
                break ;
            case 3:
                smsStatus = SmsMessageStatusType.DELIVERED;
                break ;
            case 4:
            case 5:
                smsStatus = SmsMessageStatusType.FAILED;
                break ;
        }
        return smsStatus ;
    }


}
