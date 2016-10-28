package org.apache.messagegateway.sms;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.messagegateway.constants.MessageGatewayConstants;

public interface SmsConstants extends MessageGatewayConstants{

	String tenantId_paramname = "tenantId";

	String phoneNo_paramname = "phoneNo" ;

	String providerappkey_paramname = "providerAppKey" ;

	String providername_paramname = "providerName" ;

	String providerdescription_paramname = "providerDescription" ;
	
	String bridgeconfigurations_paramname = "bridgeConfigurations" ;

	String configname_paramname = "configName" ;
	
	String configvalue_paramname = "configValue" ;
	
	Set<String> supportedParameters = new HashSet<>(Arrays.asList(tenantId_paramname, phoneNo_paramname,
			providerappkey_paramname, providername_paramname, providerdescription_paramname, bridgeconfigurations_paramname, configvalue_paramname));
}
