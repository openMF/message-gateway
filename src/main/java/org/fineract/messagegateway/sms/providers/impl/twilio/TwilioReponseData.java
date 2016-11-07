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
package org.fineract.messagegateway.sms.providers.impl.twilio;

public class TwilioReponseData {

	private String SmsSid;
	private String SmsStatus;
	private String MessageStatus;
	private String To;
	private String MessageSid;
	private String AccountSid;
	private String From;
	private String ApiVersion;

	public String getSmsSid() {
		return SmsSid;
	}

	public void setSmsSid(String smsSid) {
		SmsSid = smsSid;
	}

	public String getSmsStatus() {
		return SmsStatus;
	}

	public void setSmsStatus(String smsStatus) {
		SmsStatus = smsStatus;
	}

	public String getMessageStatus() {
		return MessageStatus;
	}

	public void setMessageStatus(String messageStatus) {
		MessageStatus = messageStatus;
	}

	public String getTo() {
		return To;
	}

	public void setTo(String to) {
		To = to;
	}

	public String getMessageSid() {
		return MessageSid;
	}

	public void setMessageSid(String messageSid) {
		MessageSid = messageSid;
	}

	public String getAccountSid() {
		return AccountSid;
	}

	public void setAccountSid(String accountSid) {
		AccountSid = accountSid;
	}

	public String getFrom() {
		return From;
	}

	public void setFrom(String from) {
		From = from;
	}

	public String getApiVersion() {
		return ApiVersion;
	}

	public void setApiVersion(String apiVersion) {
		ApiVersion = apiVersion;
	}

}
