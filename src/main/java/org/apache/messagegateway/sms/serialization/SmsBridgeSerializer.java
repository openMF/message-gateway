package org.apache.messagegateway.sms.serialization;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.messagegateway.helpers.ApiParameterError;
import org.apache.messagegateway.helpers.DataValidatorBuilder;
import org.apache.messagegateway.helpers.FromJsonHelper;
import org.apache.messagegateway.helpers.PlatformApiDataValidationException;
import org.apache.messagegateway.sms.SmsConstants;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSBridgeConfig;
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

	public void validateCreate(final SMSBridge bridge) {
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		final String tenantId = bridge.getTenantId();
		baseDataValidator.reset().parameter(SmsConstants.tenantId_paramname).value(tenantId).notBlank();
		final String phoneNo = bridge.getPhoneNo();
		baseDataValidator.reset().parameter(SmsConstants.phoneNo_paramname).value(phoneNo).notBlank();
		final String providerName = bridge.getProviderName();
		baseDataValidator.reset().parameter(SmsConstants.providername_paramname).value(providerName).notBlank();
		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException(dataValidationErrors);
		}
	}

	public void validateUpdate(final String json, final SMSBridge bridge) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, SmsConstants.supportedParameters);
		
		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource("smsBridge");
		 final JsonElement element = this.fromApiJsonHelper.parse(json);
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.tenantId_paramname, element)) {
			 final String tenantId = this.fromApiJsonHelper.extractStringNamed(SmsConstants.tenantId_paramname, element);
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
		 
		 if (this.fromApiJsonHelper.parameterExists(SmsConstants.bridgeconfigurations_paramname, element)) {
			 JsonArray configParams = this.fromApiJsonHelper.extractJsonArrayNamed(SmsConstants.bridgeconfigurations_paramname, element);
			 baseDataValidator.reset().parameter(SmsConstants.bridgeconfigurations_paramname).value(configParams).arrayNotEmpty();
			 assembleBridgeConfigParams(bridge, configParams, baseDataValidator) ;
		 }
		 
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
