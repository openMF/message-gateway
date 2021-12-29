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
package org.fineract.messagegateway.sms.data;

import java.util.Date;

import org.fineract.messagegateway.sms.util.SmsMessageStatusType;


public class DeliveryStatusData {

	private final String id; //stands for internal id
	private final String externalId;
	private final Date deliveredOnDate;
	private final Integer deliveryStatus;
	private final Boolean hasError;
	private final String errorMessage;

	public DeliveryStatusData(final String id, final String externalId, final Date deliveredOnDate,
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

	public String getId() {
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
