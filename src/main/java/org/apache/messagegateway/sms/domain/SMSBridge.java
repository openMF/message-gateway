package org.apache.messagegateway.sms.domain;

import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="m_sms_bridge")
public class SMSBridge extends AbstractPersistableCustom<Long> {

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;

	@Column(name = "phone_no", nullable = false)
	private String phoneNo;

	@Column(name = "provider_key", nullable = false)
	private String providerAppKey; //In future we can use for some kind of authentication

	@Column(name = "provider_name", nullable = false)
	private String providerName;

	@Column(name = "description", nullable = false)
	private String providerDescription;
	
	@Column(name = "created_on", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOnDate;
	
	@Column(name = "last_modified_on", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date modifiedOnDate;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bridge", orphanRemoval = true, fetch = FetchType.EAGER)
	public Collection<SMSBridgeConfig> bridgeConfigurations;

	public SMSBridge() {
		
	}
	
	public SMSBridge(final String tenantId) {
		this.tenantId = tenantId ;
	}
	
	public SMSBridge(final Long id, final String tenantId, final String phoneNo, final String providerAppKey, final String providerName, final String providerDescription) {
		this.id = id ;
		this.tenantId = tenantId ;
		this.phoneNo = phoneNo ;
		this.providerAppKey = providerAppKey ;
		this.providerName = providerName ;
		this.providerDescription = providerDescription ;
	}
	
	public SMSBridge(final String tenantId, final String phoneNo, final String providerAppKey, final String providerName, final String providerDescription) {
		this.tenantId = tenantId ;
		this.phoneNo = phoneNo ;
		this.providerAppKey = providerAppKey ;
		this.providerName = providerName ;
		this.providerDescription = providerDescription ;
	}
	
	public void setTenantId(final String tenantId) {
		this.tenantId = tenantId ;
	}
	
	public String getTenantId() {
		return tenantId;
	}

	public void setPhoneNo(final String phoneNo) {
		this.phoneNo = phoneNo ;
	}
	
	public String getPhoneNo() {
		return phoneNo;
	}

	public String getProviderAppKey() {
		return providerAppKey;
	}

	public void setProviderName(final String providerName) {
		this.providerName = providerName ;
	}
	
	public String getProviderName() {
		return providerName;
	}
	
	public void setProviderDescription(final String providerDescription) {
		this.providerDescription = providerDescription ;
	}
	
	public String getProviderDescription() {
		return this.providerDescription ;
	}
	
	public void setProviderAppKey(final String appkey) {
		this.providerAppKey = appkey ;
	}
	
	public Collection<SMSBridgeConfig> getBridgeConfigurations() {
		return this.bridgeConfigurations ;
	}
	
	public String getConfigValue(final String configParamName) {
		if(this.bridgeConfigurations == null || this.bridgeConfigurations.size() == 0) return null ;
		String configValue = null ;
		for(SMSBridgeConfig config: this.bridgeConfigurations) {
			if(config.getConfigName().equals(configParamName)) {
				configValue = config.getConfigValue() ;
			}
		}
		return configValue ;
	}
	
	public void setCreatedDate(final Date createdOnDate) {
		this.createdOnDate = createdOnDate ;
	}
	
	public void setSMSBridgeToBridgeConfigs() {
		if(this.bridgeConfigurations != null) {
			for(SMSBridgeConfig config: this.bridgeConfigurations) {
				config.setSMSBridge(this); 
			}
		}
	}

	public void setSmsBridgeConfig(final Collection<SMSBridgeConfig> bridgeConfigurations) {
		this.bridgeConfigurations.clear(); 
		this.bridgeConfigurations = bridgeConfigurations ;
	}
	public String generateApiKey() {
		StringBuffer buff = new StringBuffer() ;
		buff.append(tenantId) ;
		buff.append(":") ;
		buff.append(phoneNo) ;
		buff.append(":") ;
		buff.append(providerName) ;
		buff.append(":") ;
		SMSBridgeConfig config = this.bridgeConfigurations.iterator().next() ;
		buff.append(config.getConfigName()) ;
		buff.append(":") ;
		buff.append(config.getConfigValue()) ;
		return buff.toString();
	}
}
