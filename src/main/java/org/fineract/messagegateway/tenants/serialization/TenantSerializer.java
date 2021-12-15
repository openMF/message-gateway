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
package org.fineract.messagegateway.tenants.serialization;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.fineract.messagegateway.exception.PlatformApiDataValidationException;
import org.fineract.messagegateway.helpers.ApiParameterError;
import org.fineract.messagegateway.helpers.DataValidatorBuilder;
import org.fineract.messagegateway.helpers.FromJsonHelper;
import org.fineract.messagegateway.tenants.constants.TenantConstants;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Component
public class TenantSerializer {

	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public TenantSerializer(FromJsonHelper fromJsonHelper) {
		this.fromJsonHelper = fromJsonHelper;
	}

	public Tenant validateCreateRequest(final String json) {
		final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
		this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json,
				TenantConstants.CREATE_REQUEST_PARAMETERS);

		final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
		final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
				.resource(TenantConstants.TENANTS_RESOURCE_NAME);
		final JsonElement element = this.fromJsonHelper.parse(json);


		final String tenantId = this.fromJsonHelper.extractStringNamed(TenantConstants.TENANT_ID, element);
		baseDataValidator.reset().parameter(TenantConstants.TENANT_ID)
				.value(tenantId).notBlank().notExceedingLengthOf(32);

		String tenantDescription = null;
		if(this.fromJsonHelper.parameterExists(TenantConstants.TENANT_DESCRIPTION, element)) {
			tenantDescription = this.fromJsonHelper.extractStringNamed(TenantConstants.TENANT_DESCRIPTION,
					element);
			baseDataValidator.reset().parameter(TenantConstants.TENANT_DESCRIPTION).value(tenantDescription)
					.notBlank().notExceedingLengthOf(500);
		}

		if (!dataValidationErrors.isEmpty()) {
			throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
					"Validation errors exist.", dataValidationErrors);
		}

		return new Tenant(tenantId, null, tenantDescription);
	}
}
