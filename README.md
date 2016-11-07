# message-gateway

Steps to configure Message-Gateway

## Create tenant with http://host:9191/tenants/
	method: POST
	Request Body:
	
	{
		"tenantId" : "Tenant Id",
		"description" : "Some Description"
	}
	
 This API will return an unique tenant app key. This key should not be shared with others. 
 
## Create Twilio SMS Bridge with http://host:9191/smsbridges
 	method: POST
 	HEADERS:
 		Fineract-Platform-TenantId:tenantId
 		Fineract-Tenant-App-Key:generatedAppkeywhilecreatingtenant
 	Request Body:
 	{
		"phoneNo": "+xxxxxxxxxxx",
		"providerName": "Twilio Provider",
		"providerDescription": "SMS Provider for promotional messages",
		"providerKey":"Twilio",
		"bridgeConfigurations": [
			{
				"configName":"Provider_Account_Id",
				"configValue":"xxxxx"
			},
			{
			"configName":"Provider_Auth_Token",
			"configValue":"xxxxx"
			}
		]
	}
	
This API will return Bridge Identifier by which you can use while sending SMS
	
## Send SMS by using one of the provider with http://localhost:9191/sms
	Method: POST
	HEADERS:
 		Fineract-Platform-TenantId:tenantId
 		Fineract-Tenant-App-Key:generatedAppkeywhilecreatingtenant
 	Request Body:
 		
		[{
			"internalId":"55",
			"mobileNumber":"+91xxxxxxxxxx",
			"message":"Hello from Fineract",
			"providerId":"2"
		}]

	
