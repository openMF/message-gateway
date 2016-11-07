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

import java.util.Date;

import org.fineract.messagegateway.sms.util.SmsMessageStatusType;


/** 
 * Immutable data object representing a sms gateway message delivery report 
 **/
public class SmsGatewayDeliveryReport {
    
    /** 
     * the sms gateway message identifier  
     **/
    private String externalId;
    
    /** 
     * Date/time when message was submitted from mifos to the sms gateway system. (format: yyyy/mm/dd hh:mm:ss) 
     **/
    private Date sentDate;
    
    /** 
     * Date/time when SMSC notified the sms gateway system of the delivery report (format: yyyy/mm/dd hh:mm:ss) 
     **/
    private Date doneDate;
    
    /** 
     * the status of the message delivery 
     **/
    private SmsMessageStatusType status;
    
    /** 
     * SmsGatewayDeliveryReport constructor
     * 
     **/
    public SmsGatewayDeliveryReport(String externalId, Date sentDate, Date doneDate, SmsMessageStatusType status) {
        this.externalId = externalId;
        this.sentDate = sentDate;
        this.doneDate = doneDate;
        this.status = status;
    }
    
    /**
     * @return the external id
     **/
    public String getExternalId() {
        return externalId;
    }
    
    /**
     * @return the sent date
     **/
    public Date getSentDate() {
        return sentDate;
    }
    
    /**
     * @return the sent date
     **/
    public Date getDoneDate() {
        return doneDate;
    }
    
    /**
     * @return the status
     **/
    public SmsMessageStatusType getStatus() {
        return status;
    }
    
    @Override
    /** 
     * @return String representation of the SmsGatewayDeliveryReport class
     **/
    public String toString() {
        return "SmsGatewayDeliveryReport [externalId=" + externalId + ", sentDate=" + sentDate + ", doneDate=" + doneDate + ", status=" + status + "]";
    }
}
