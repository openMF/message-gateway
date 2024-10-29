package org.fineract.messagegateway.sms.providers.impl.safaricom;

import org.apache.commons.lang.StringUtils;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.providers.Provider;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.fineract.messagegateway.tenants.domain.Tenant;
import org.fineract.messagegateway.tenants.repository.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.fineract.messagegateway.constants.MessageGatewayConstants.MESSAGE_TYPE_EMAIL;
import static org.fineract.messagegateway.constants.MessageGatewayConstants.MESSAGE_TYPE_SMS;
import static org.fineract.messagegateway.constants.MessageGatewayConstants.PROVIDER_AUTH_TOKEN;
import static org.fineract.messagegateway.constants.MessageGatewayConstants.PROVIDER_URL;

@Service(value = "safaricom")
public class SafaricomServiceProvider extends Provider {
    private static final Logger logger = LoggerFactory.getLogger(SafaricomServiceProvider.class);

    @Autowired
    private TenantRepository tenantRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendEmail(SMSBridge smsBridgeConfig, OutboundMessages message){
        logger.info("Sending Email Message- Safaricom");
        Tenant tenant = tenantRepository.findById(smsBridgeConfig.getTenantId()).orElseThrow(
                ()-> new RuntimeException("Tenant Not found"));
        Map<String, String> request = new HashMap<>();
        request.put("type", MESSAGE_TYPE_EMAIL);
        request.put("tenantID", tenant.getTenantId());
        request.put("to", message.getEmail());
        request.put("message", message.getMessage());

        connectAndSendToServer(smsBridgeConfig, message, request);
    }

    @Override
    public void sendMessage(SMSBridge smsBridgeConfig, OutboundMessages message){
        logger.info("Sending SMS Message - Safaricom");
        Tenant tenant = tenantRepository.findById(smsBridgeConfig.getTenantId()).orElseThrow(
                ()-> new RuntimeException("Tenant Not found"));
        Map<String, String> request = new HashMap<>();
        request.put("type", MESSAGE_TYPE_SMS);
        request.put("tenantID", tenant.getTenantId());
        request.put("to", message.getMobileNumber());
        request.put("message", message.getMessage());

        connectAndSendToServer(smsBridgeConfig, message, request);

        //move this from here
        if(StringUtils.isNotBlank(message.getEmail())){
            sendEmail(smsBridgeConfig, message);
        }
    }


    private void connectAndSendToServer(SMSBridge smsBridgeConfig, OutboundMessages message, Map<String, String> request) {

        String providerUrl = smsBridgeConfig.getConfigValue(PROVIDER_URL);
        String authorization = smsBridgeConfig.getConfigValue(PROVIDER_AUTH_TOKEN);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add(HttpHeaders.AUTHORIZATION,authorization);

        HttpEntity<?> entity = new HttpEntity<Object>(request);

        ResponseEntity<String> response = restTemplate.exchange(providerUrl, HttpMethod.POST, entity, new ParameterizedTypeReference<String>() {

        });
        if (response != null) {
            // String smsResponse = responseOne.getBody();
            if (!response.getStatusCode().equals(HttpStatus.CREATED)) {
                logger.debug("{}", response.getStatusCode().value());
                throw new RuntimeException("Request Failed,  Status" + response.getStatusCode());
            }
            message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
            message.setDeliveredOnDate(new Date());
            message.setResponse(response.getBody().toString());
        }else {
            throw new RuntimeException("Connection Failed : Response NULL");
        }
    }

    @Override
    public void updateStatusByMessageId(SMSBridge bridge, String externalId,String orchestrator) throws MessageGatewayException{

    }
    @Override
    public void publishZeebeVariable(OutboundMessages message){

    }
}
