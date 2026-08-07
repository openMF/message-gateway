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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="m_sms_bridge_configuration")
public class SMSBridgeConfig extends AbstractPersistableCustom<Long>{

	@ManyToOne(optional = false)
    @JoinColumn(name = "sms_bridge_id", referencedColumnName = "id", nullable = false)
    private SMSBridge bridge;
	
	@Column(name = "config_name", nullable = false)
	private String configName ;
	
	@Column(name = "config_value", nullable = false)
	private String configValue ;
	
	protected SMSBridgeConfig() {
		
	}
	
	public SMSBridgeConfig(final String configName, final String configValue) {
		this.configName = configName ;
		this.configValue = configValue ;
	}
	
	public String getConfigName() {
		return this.configName ;
	}
	
	public String getConfigValue() {
		return this.configValue ;
	}
	
	public void setSMSBridge(final SMSBridge bridge) {
		this.bridge = bridge ;
	}
}
