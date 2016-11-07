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
package org.fineract.messagegateway.sms.providers.impl.infobip;

/** 
 * SMPP server simulator predefined enum constants (connection credentials)
 **/
public enum SMPPServerSimulatorConfiguration {
	SMS_GATEWAY_SYSTEM_ID("test", "SMPPServerSimulatorConfiguration.smsGatewaySystemId"),
	SMS_GATEWAY_HOSTNAME("localhost", "SMPPServerSimulatorConfiguration.smsGatewayHostname"),
	SMS_GATEWAY_PORT(8056, "SMPPServerSimulatorConfiguration.smsGatewayPort"),
	SMS_GATEWAY_PASSWORD("test", "SMPPServerSimulatorConfiguration.smsGatewayPassword");

	private final Object value;
    private final String code;
    
    /** 
     * SMPPServerSimulatorConfiguration constructor  
     **/
    private SMPPServerSimulatorConfiguration(final Object value, final String code) {
        this.value = value;
        this.code = code;
    }

    /** 
     * @return enum constant value 
     **/
    public Object getValue() {
        return this.value;
    }

    /** 
     * @return enum constant 
     **/
    public String getCode() {
        return this.code;
    }
}
