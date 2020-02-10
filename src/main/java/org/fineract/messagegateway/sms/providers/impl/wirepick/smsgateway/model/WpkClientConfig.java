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

package org.fineract.messagegateway.sms.providers.impl.wirepick.smsgateway.model;

public class WpkClientConfig {
	
	
	private String clientid ;
	private String pws ;
	private String affiliate;
	
	private String phone ;
	private String sms;
	private String senderid;
	private String tag;
	
	public WpkClientConfig()
	{
		
	}
	
	public WpkClientConfig(String clientid, String pws, String affiliate, String phone, String sms, String senderid,
			String tag) {
		this.clientid = clientid;
		this.pws = pws;
		this.affiliate = affiliate;
		this.phone = phone;
		this.sms = sms;
		this.senderid = senderid;
		this.tag = tag;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getPws() {
		return pws;
	}
	public void setPws(String pws) {
		this.pws = pws;
	}
	public String getAffiliate() {
		return affiliate;
	}
	public void setAffiliate(String affiliate) {
		this.affiliate = affiliate;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSms() {
		return sms;
	}
	public void setSms(String sms) {
		this.sms = sms;
	}
	public String getSenderid() {
		return senderid;
	}
	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
