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
package org.fineract.messagegateway.sms.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fineract.messagegateway.constants.MessageGatewayConstants;


public interface SmsConstants extends MessageGatewayConstants{

	String tenantId_paramname = "tenantId";

	String phoneNo_paramname = "phoneNo" ;

	String providerappkey_paramname = "providerAppKey" ;

	String providername_paramname = "providerName" ;

	String providerkey_paramname = "providerKey" ;
	
	String countrycode_paramname = "countryCode" ;
	
	String providerdescription_paramname = "providerDescription" ;
	
	String bridgeconfigurations_paramname = "bridgeConfigurations" ;

	String configname_paramname = "configName" ;
	
	String configvalue_paramname = "configValue" ;
	
	Set<String> supportedParameters = new HashSet<>(Arrays.asList(phoneNo_paramname,
			providername_paramname, providerkey_paramname, countrycode_paramname, providerdescription_paramname, bridgeconfigurations_paramname, configvalue_paramname));
}
