package org.apache.messagegateway.sms.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.messagegateway.sms.util.SmsMessageStatusType;

@Entity
@Table(name = "m_outbound_messages")
public class SMSMessage extends AbstractPersistableCustom<Long> {

	@Column(name = "external_id", nullable = true)
	private String externalId;

	@Column(name = "internal_id", nullable = false)
	private Long internalId;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

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

	@Column(name = "provider_id", nullable = false)
	private Long providerId;

	protected SMSMessage() {
		
	}

	private SMSMessage(final String externalId, final Long internalId, final String tenantId,
			final Date submittedOnDate, final Date deliveredOnDate,
			final SmsMessageStatusType deliveryStatus, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long providerId) {
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
		this.providerId = providerId;
	}

	public static SMSMessage getPendingMessages(final String externalId, final Long internalId,
			final String mifosTenantIdentifier, final Date submittedOnDate,
			final Date deliveredOnDate, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long providerId) {

		return new SMSMessage(externalId, internalId, mifosTenantIdentifier, submittedOnDate,
				deliveredOnDate, SmsMessageStatusType.PENDING, deliveryErrorMessage, sourceAddress, mobileNumber,
				message, providerId);
	}

	/**
	 * @return an instance of the SmsOutboundMessage class
	 **/
	public SMSMessage getInstance(final String externalId, final Long internalId, final String mifosTenantIdentifier,
			final Date submittedOnDate, final Date deliveredOnDate,
			final SmsMessageStatusType deliveryStatus, final String deliveryErrorMessage, final String sourceAddress,
			final String mobileNumber, final String message, final Long providerId) {

		return new SMSMessage(externalId, internalId, mifosTenantIdentifier, submittedOnDate,
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

	public String getTenantId() {
		return tenantId;
	}

	public void setMifosTenantIdentifier(String tenantId) {
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

	public Long getProviderId() {
		return providerId;
	}

	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}

	public void setSubmittedOnDate(final Date submittedDate) {
		this.submittedOnDate = submittedDate ;
	}
	
	public void setDeliveryErrorMessage(final String deliveryErrorMessage) {
		this.deliveryErrorMessage = deliveryErrorMessage ;
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
	
	@Override
    public String toString() {
        return "SmsOutboundMessage [externalId=" + externalId + ", internalId=" + internalId
                + ",TenantIdentifier=" + tenantId + ", submittedOnDate=" + submittedOnDate + ", deliveredOnDate="
                + deliveredOnDate + ", deliveryStatus=" + deliveryStatus + ", deliveryErrorMessage="
                + deliveryErrorMessage + ", sourceAddress=" + sourceAddress + ", mobileNumber=" + mobileNumber
                + ", message=" + message + "]";
    }
}
