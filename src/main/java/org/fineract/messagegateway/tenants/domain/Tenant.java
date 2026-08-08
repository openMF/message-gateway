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
package org.fineract.messagegateway.tenants.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.fineract.messagegateway.sms.domain.AbstractPersistableCustom;

@Entity
@Table(name="m_tenants")
public class Tenant extends AbstractPersistableCustom<Long> {

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;
	
	@Column(name = "tenant_app_key", nullable = false)
	private String tenantAppKey ;
	
	@Column(name = "description", nullable = true)
	private String description ;
	
	protected Tenant() { }
	
	public Tenant(final String tenantId, final String tenantAppKey, final String description) {
		this.tenantId = tenantId ;
		this.tenantAppKey = tenantAppKey ;
		this.description = description ;
	}
		
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getTenantAppKey() {
		return tenantAppKey;
	}

	public void setTenantAppKey(String tenantAppKey) {
		this.tenantAppKey = tenantAppKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
