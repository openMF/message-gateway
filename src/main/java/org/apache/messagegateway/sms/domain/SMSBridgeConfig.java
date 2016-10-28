package org.apache.messagegateway.sms.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name="m_provider_configuration")
public class SMSBridgeConfig extends AbstractPersistableCustom<Long>{

	@ManyToOne(optional = false)
    @JoinColumn(name = "provider_id", referencedColumnName = "id", nullable = false)
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
