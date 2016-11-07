package org.apache.messagegateway.tenants.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.messagegateway.sms.domain.AbstractPersistableCustom;

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
