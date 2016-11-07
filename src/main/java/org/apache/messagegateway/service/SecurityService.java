package org.apache.messagegateway.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.apache.messagegateway.exception.SecurityException;
import org.apache.messagegateway.exception.UnexpectedException;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.tenants.domain.Tenant;
import org.apache.messagegateway.tenants.exception.TenantNotFoundException;
import org.apache.messagegateway.tenants.repository.TenantRepository;
import org.apache.messagegateway.tenants.service.TenantsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private TenantRepository tenantRepository ;
    
    @Autowired
    private TenantsService tenantService ;
    
    public SecurityService() {
        super();
    }
    
    public Tenant authenticate(final String tenantId, final String tenantAppKey) {
    	Tenant tenant = this.tenantService.findTenantByTenantIdAndTenantAppKey(tenantId, tenantAppKey) ;
    	return tenant ;
    }

    /*public void verifyApiKey(final String apiKey, final String tenantId) {
        final SMSBridgeConfig smsBridgeConfigList = this.smsBridgeRepository.findByApiKey(apiKey);
        if (smsBridgeConfigList == null || !smsBridgeConfigList.getTenantId().equals(tenantId)) {
            throw new InvalidApiKeyException(apiKey);
        }
    }*/

    public String generateApiKey(final SMSBridge smsBridge) {
        try {
            final String source = smsBridge.generateApiKey() ;
            return DigestUtils.md5DigestAsHex(source.getBytes("UTF-8"));
        } catch (Exception ex) {
            logger.error("Could not create API key, reason,", ex);
            throw new IllegalArgumentException(ex);
        }
    }
    
    public String generateApiKey(final String tenantId) {
    	Tenant tenant = this.tenantRepository.findByTenantId(tenantId) ;
    	if(tenant != null) {
    		SecurityException.tenantAlreadyExisits(tenantId) ;
    	}
    	
        final String randomKey = UUID.randomUUID().toString();
        try {
			final String restApiKey = URLEncoder.encode(randomKey, "UTF-8");
			return restApiKey;
		} catch (final UnsupportedEncodingException e) {
			logger.error("API Key generation error..., reason,", e);
			throw new UnexpectedException();
		}
	}
}
