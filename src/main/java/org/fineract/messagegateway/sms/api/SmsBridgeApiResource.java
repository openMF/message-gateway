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
package org.fineract.messagegateway.sms.api;

import java.util.Collection;

import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.exception.UnsupportedParameterException;
import org.fineract.messagegateway.helpers.ApiGlobalErrorResponse;
import org.fineract.messagegateway.helpers.PlatformApiDataValidationExceptionMapper;
import org.fineract.messagegateway.helpers.PlatformResourceNotFoundExceptionMapper;
import org.fineract.messagegateway.helpers.UnsupportedParameterExceptionMapper;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.fineract.messagegateway.sms.service.SMSBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smsbridges")
public class SmsBridgeApiResource {

	private final SMSBridgeService smsBridgeService ;
	
	@Autowired
    public SmsBridgeApiResource(final SMSBridgeService smsBridgeService) {
		this.smsBridgeService = smsBridgeService ;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> createSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
    		@RequestBody final String smsBridgeJson) {
    		 Long bridgeId = this.smsBridgeService.createSmsBridgeConfig(tenantId, appKey, smsBridgeJson) ;
    		 return new ResponseEntity<>(bridgeId, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>updateSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
    		@PathVariable("bridgeId") final Long bridgeId, @RequestBody final String smsBridge) {
    	this.smsBridgeService.updateSmsBridge(tenantId, tenantAppKey, bridgeId, smsBridge);
        return new ResponseEntity<>(bridgeId, HttpStatus.OK);
    }
    
    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.DELETE, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>deleteSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String tenantAppKey,
    		@PathVariable("bridgeId") final Long bridgeId) {
		this.smsBridgeService.deleteSmsBridge(tenantId, tenantAppKey, bridgeId);
        return new ResponseEntity<>(bridgeId, HttpStatus.OK);
    }
    
    @RequestMapping(method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<SMSBridge>> getAllSMSBridgeConfigs(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey) {
        Collection<SMSBridge> bridges = this.smsBridgeService.retrieveProviderDetails(tenantId, appKey) ;
        return new ResponseEntity<>(bridges, HttpStatus.OK);
    }

    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<SMSBridge> getSMSBridgeConfig(@RequestHeader(MessageGatewayConstants.TENANT_IDENTIFIER_HEADER) final String tenantId,
    		@RequestHeader(MessageGatewayConstants.TENANT_APPKEY_HEADER) final String appKey,
    		@PathVariable("bridgeId") final Long bridgeId) {
        SMSBridge bridge = this.smsBridgeService.retrieveSmsBridge(tenantId, appKey, bridgeId);
		return new ResponseEntity<>(bridge, HttpStatus.OK);
    }
    
    @ExceptionHandler({PlatformApiDataValidationException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiDataValidationException(PlatformApiDataValidationException e) {
    	return PlatformApiDataValidationExceptionMapper.toResponse(e) ;
    }
    
    @ExceptionHandler({UnsupportedParameterException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleUnsupportedParameterException(UnsupportedParameterException e) {
    	return UnsupportedParameterExceptionMapper.toResponse(e) ;
    }
    
    @ExceptionHandler({SMSBridgeNotFoundException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleSMSBridgeNotFoundException(SMSBridgeNotFoundException e) {
    	return PlatformResourceNotFoundExceptionMapper.toResponse(e) ;
    }
}
