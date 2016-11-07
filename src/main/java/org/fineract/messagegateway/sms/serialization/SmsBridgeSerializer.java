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
package org.fineract.messagegateway.sms.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.helpers.DataValidatorBuilder;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.sms.SmsConstants;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSBridgeConfig;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class SmsBridgeSerializer {

	private final FromJsonHelper fromApiJsonHelper;

	@Autowired
	public SmsBridgeSerializer(final FromJsonHelper fromApiJsonHelper) {
		this.fromApiJsonHelper = fromApiJsonHelper;
	}

	public SMSBridge validateCreate(final String json, Tenant tenant) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsConstants.supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		final JsonElement element = this.fromApiJsonHelper.parse(json);
		
		final String phoneNumber = this.fromApiJsonHelper.extractStringNamed(SmsConstants.phoneNo_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.phoneNo_paramname).value(phoneNumber).notBlank();
		
		final String providerName = this.fromApiJsonHelper.extractStringNamed(SmsConstants.providername_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.providername_paramname).value(providerName).notBlank();
		 
		final String countryCode = this.fromApiJsonHelper.extractStringNamed(SmsConstants.countrycode_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.countrycode_paramname).value(countryCode).notBlank();
		
		final String providerKey = this.fromApiJsonHelper.extractStringNamed(SmsConstants.providerkey_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.providerkey_paramname).value(providerKey).notBlank();
		
		final String providerDescription = this.fromApiJsonHelper.extractStringNamed(SmsConstants.providerdescription_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.providerdescription_paramname).value(providerDescription).notBlank();
		
		SMSBridge bridge = new SMSBridge(tenant.getId(), phoneNumber, providerName, providerKey, countryCode, providerDescription) ;
		
		JsonArray configParams = this.fromApiJsonHelper.extractJsonArrayNamed(SmsConstants.bridgeconfigurations_paramname, element);
		baseDataValidator.reset().parameter(SmsConstants.bridgeconfigurations_paramname).value(configParams).jsonArrayNotEmpty();
		if (configParams != null && configParams.size() > 0) {
			assembleBridgeConfigParams(bridge, configParams, baseDataValidator);
		}
		bridge.setCreatedDate(new Date());
		bridge.setSMSBridgeToBridgeConfigs(); 
		
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
		
		return bridge ;
	}

	public void validateUpdate(final String json, final SMSBridge bridge) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsConstants.supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		 final JsonElement element = this.fromApiJsonHelper.parse(json);
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.tenantId_paramname, element)) {
			 final Long tenantId = this.fromApiJsonHelper.extractLongNamed(SmsConstants.tenantId_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.tenantId_paramname).value(tenantId).notBlank();
			 bridge.setTenantId(tenantId);
		 }
		 
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.phoneNo_paramname, element)) {
			 final String phoneNumber = this.fromApiJsonHelper.extractStringNamed(SmsConstants.phoneNo_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.phoneNo_paramname).value(phoneNumber).notBlank();
			 bridge.setPhoneNo(phoneNumber);
		 }
		 
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.providername_paramname, element)) {
			 final String providerName = this.fromApiJsonHelper.extractStringNamed(SmsConstants.providername_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.providername_paramname).value(providerName).notBlank();
			 bridge.setProviderName(providerName);
		 }
		 
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.providerdescription_paramname, element)) {
			 final String providerDescription = this.fromApiJsonHelper.extractStringNamed(SmsConstants.providerdescription_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.providerdescription_paramname).value(providerDescription).notBlank();
			 bridge.setProviderDescription(providerDescription);
		 }
		 
		 if(this.fromApiJsonHelper.parameterExists(SmsConstants.countrycode_paramname, element)) {
			 final String countryCode = this.fromApiJsonHelper.extractStringNamed(SmsConstants.countrycode_paramname, element);
				baseDataValidator.reset().parameter(SmsConstants.countrycode_paramname).value(countryCode).notBlank();
				bridge.setCountryCode(countryCode);
				
		 }
		 
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.bridgeconfigurations_paramname, element)) {
			 JsonArray configParams = this.fromApiJsonHelper.extractJsonArrayNamed(SmsConstants.bridgeconfigurations_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.bridgeconfigurations_paramname).value(configParams).jsonArrayNotEmpty();
			 if(configParams != null && configParams.size() > 0) {
				 assembleBridgeConfigParams(bridge, configParams, baseDataValidator) ;	 
			 }
		 }
		 
		 bridge.setModifiedOnDate(new Date()) ;
		 if (!dataValidationErrors.isEmpty()) {
				throw new PlatformApiDataValidationException(dataValidationErrors);
		 }
	}
	
	private void assembleBridgeConfigParams(final SMSBridge bridge, final JsonArray configParams, final DataValidatorBuilder baseDataValidator) {
		List<SMSBridgeConfig> configs = new ArrayList<>() ;
		 for (int i = 0; i < configParams.size(); i++) {
             final JsonObject jsonObject = configParams.get(i).getAsJsonObject();
                 final String  configName = jsonObject.get(SmsConstants.configname_paramname).getAsString();
                 final String  configValue = jsonObject.get(SmsConstants.configvalue_paramname).getAsString();
                 baseDataValidator.reset().parameter(SmsConstants.configname_paramname).value(configName).notBlank();
                 baseDataValidator.reset().parameter(SmsConstants.configvalue_paramname).value(configValue).notBlank();
                 final SMSBridgeConfig config = new SMSBridgeConfig(configName, configValue) ;
                 configs.add(config) ;
         }
		 bridge.setSmsBridgeConfig(configs) ;
	}
}
