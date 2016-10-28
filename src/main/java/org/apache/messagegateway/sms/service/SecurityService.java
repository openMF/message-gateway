/**
 * Copyright 2014 Markus Geiss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.messagegateway.sms.service;

import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.repository.SMSBridgeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class SecurityService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityService.class);

    @Autowired
    private SMSBridgeRepository smsBridgeRepository;

    public SecurityService() {
        super();
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
}
