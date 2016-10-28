package org.apache.messagegateway.sms.data;

import java.util.Date;

import org.apache.messagegateway.sms.util.SmsMessageStatusType;

public class DeliveryStatusData {

	private final Long id;
	private final String externalId;
	private final Date deliveredOnDate;
	private final Integer deliveryStatus;
	private final Boolean hasError;
	private final String errorMessage;

	public DeliveryStatusData(final Long id, final String externalId, final Date deliveredOnDate,
			final Integer deliveryStatus, final String errorMessage) {
		this.id = id;
		this.externalId = externalId;
		this.deliveredOnDate = deliveredOnDate;
		this.deliveryStatus = deliveryStatus;
		if (deliveryStatus.equals(SmsMessageStatusType.FAILED)) {
			this.hasError = Boolean.TRUE;
		} else {
			this.hasError = Boolean.FALSE;
		}
		this.errorMessage = errorMessage;
	}

	public Long getId() {
		return id;
	}

	public String getExternalId() {
		return externalId;
	}

	public Date getDeliveredOnDate() {
		return deliveredOnDate;
	}

	public Integer getDeliveryStatus() {
		return deliveryStatus;
	}

	public Boolean getHasError() {
		return hasError;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
