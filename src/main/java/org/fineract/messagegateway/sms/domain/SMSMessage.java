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
package org.fineract.messagegateway.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.fineract.messagegateway.sms.util.SmsMessageStatusType;

@Entity
@Table(name = "m_outbound_messages")
public class SMSMessage extends AbstractPersistableCustom<Long> {

	@com.fasterxml.jackson.annotation.JsonIgnore
	@Column(name = "tenant_id", nullable = false)
	private Long tenantId;
	
	@Column(name = "external_id", nullable = true)
	private String externalId;

	@Column(name = "internal_id", nullable = false)
	private Long internalId;

	@Column(name = "submitted_on_date", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date submittedOnDate;

	@Column(name = "delivered_on_date", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date deliveredOnDate;

	@Column(name = "delivery_status", nullable = false)
	private Integer deliveryStatus = SmsMessageStatusType.PENDING.getValue();

	@Column(name = "delivery_error_message", nullable = true)
	private String deliveryErrorMessage;

	@Column(name = "source_address", nullable = true)
	private String sourceAddress;

	@Column(name = "mobile_number", nullable = false)
	private String mobileNumber;

	@Column(name = "message", nullable = false)
	private String message;

	@Column(name = "sms_bridge_id", nullable = false)
	private Long bridgeId;

	@Column(name = "response")
	private String response;

	protected SMSMessage() {
		
	}

	public SMSMessage(final String externalId, final Long internalId, final Long tenantId,
			final Date submittedOnDate, final Date deliveredOnDate,
			final SmsMessageStatusType deliveryStatus, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long bridgeId) {
		this.externalId = externalId;
		this.internalId = internalId;
		this.tenantId = tenantId;
		this.submittedOnDate = submittedOnDate;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus.getValue();
		this.deliveryErrorMessage = deliveryErrorMessage;
		this.sourceAddress = sourceAddress;
		this.mobileNumber = mobileNumber;
		this.message = message;
		this.bridgeId = bridgeId;
	}

	public static SMSMessage getPendingMessages(final String externalId, final Long internalId,
			final Long tenantId, final Date submittedOnDate,
			final Date deliveredOnDate, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long providerId) {

		return new SMSMessage(externalId, internalId, tenantId, submittedOnDate,
				deliveredOnDate, SmsMessageStatusType.PENDING, deliveryErrorMessage, sourceAddress, mobileNumber,
				message, providerId);
	}

	/**
	 * @return an instance of the SmsOutboundMessage class
	 **/
	public SMSMessage getInstance(final String externalId, final Long internalId, final Long tenantId,
			final Date submittedOnDate, final Date deliveredOnDate,
			final SmsMessageStatusType deliveryStatus, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long providerId) {

		return new SMSMessage(externalId, internalId, tenantId, submittedOnDate,
				deliveredOnDate, deliveryStatus, deliveryErrorMessage, sourceAddress, mobileNumber, message,
				providerId);
	}

	public Long getInternalId() {
		return internalId;
	}

	public void setExternalId(final String externalId) {
		this.externalId = externalId ;
	}
	
	public String getExternalId() {
		return this.externalId ;
	}
	
	public void setInternalId(Long internalId) {
		this.internalId = internalId;
	}

	public Long getTenantId() {
		return tenantId;
	}

	public void setTenant(Long tenantId) {
		this.tenantId = tenantId;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getBridgeId() {
		return bridgeId;
	}

	public void setProviderId(Long providerId) {
		this.bridgeId = providerId;
	}

	public void setSubmittedOnDate(final Date submittedDate) {
		this.submittedOnDate = submittedDate ;
	}
	
	public void setDeliveryErrorMessage(final String deliveryErrorMessage) {
		this.deliveryErrorMessage = deliveryErrorMessage ;
	}
	public String getDeliveryErrorMessage(){
		return this.deliveryErrorMessage;
	}
	
	public void setDeliveryStatus(final Integer status) {
		this.deliveryStatus = status ;
	}
	
	public void setDeliveredOnDate(final Date deliveredOnDate) {
		this.deliveredOnDate = deliveredOnDate ;
	}
	
	public Integer getDeliveryStatus() {
		return this.deliveryStatus ;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	@Override
    public String toString() {
        return "SmsOutboundMessage [externalId=" + externalId + ", internalId=" + internalId
                + ",TenantIdentifier=" + tenantId + ", submittedOnDate=" + submittedOnDate + ", deliveredOnDate="
                + deliveredOnDate + ", deliveryStatus=" + deliveryStatus + ", deliveryErrorMessage="
                + deliveryErrorMessage + ", sourceAddress=" + sourceAddress + ", mobileNumber=" + mobileNumber
                + ", message=" + message + "]";
    }
}
