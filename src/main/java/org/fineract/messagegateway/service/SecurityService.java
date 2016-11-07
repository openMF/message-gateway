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
package org.fineract.messagegateway.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import org.fineract.messagegateway.exception.UnexpectedException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.fineract.messagegateway.tenants.repository.TenantRepository;
import org.fineract.messagegateway.tenants.service.TenantsService;
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
    		org.fineract.messagegateway.exception.SecurityException.tenantAlreadyExisits(tenantId) ;
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
