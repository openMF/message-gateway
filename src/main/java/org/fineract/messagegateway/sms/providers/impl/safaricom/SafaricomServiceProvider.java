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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.net.URI;
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
    public static final String TOKEN = "TOKEN";
    public static final String EXPIRES_AT = "EXPIRES_AT";

    @Autowired
    private TenantRepository tenantRepository;
    private final RestTemplate restTemplate = createRestTemplate();
    private final MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(60_000);
        return new RestTemplate(requestFactory);
    }



    private static final Map<String, Object> statcTokenMap = new HashMap<>();


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

        if(StringUtils.isNotBlank(message.getEmail())){
            try {
                sendEmail(smsBridgeConfig, message);
            } catch (RuntimeException ex) {
                logger.error("SMS {} was accepted by Safaricom. Follow-up email failed", message.getId(), ex);
            }
        }
        message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
    }


    private void connectAndSendToServer(SMSBridge smsBridgeConfig, OutboundMessages message, Map<String, String> request) {
        String providerUrl = smsBridgeConfig.getConfigValue(PROVIDER_URL);
        logger.info("Posting Safaricom {} for message {} to {}", request.get("type"), message.getId(), providerUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(getAuthToken(smsBridgeConfig));

        try {
            restTemplate.execute(providerUrl, HttpMethod.POST, clientRequest -> {
                clientRequest.getHeaders().putAll(headers);
                jsonConverter.write(request, MediaType.APPLICATION_JSON, clientRequest);
            }, clientResponse -> {
                applyProviderResponse(message, clientResponse);
                return null;
            });
        } catch (RuntimeException ex) {
            if (!SmsMessageStatusType.SENT.getValue().equals(message.getDeliveryStatus())) {
                message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
                if (message.getDeliveryErrorMessage() == null) {
                    message.setDeliveryErrorMessage(ex.getMessage());
                }
                throw ex;
            }
            logger.warn("Safaricom accepted message {} but closing the response failed", message.getId(), ex);
        }
    }

    private void applyProviderResponse(OutboundMessages message, ClientHttpResponse clientResponse) throws IOException {
        int status = clientResponse.getRawStatusCode();
        String responseBody = readBody(clientResponse);
        logger.info("Safaricom response for message {} status {} body {}", message.getId(), status, abbreviate(responseBody));
        if (status == HttpStatus.OK.value() || status == HttpStatus.CREATED.value() || status == HttpStatus.ACCEPTED.value()) {
            message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
            message.setDeliveredOnDate(new Date());
            message.setResponse(responseBody);
            return;
        }
        message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
        message.setDeliveryErrorMessage("Request Failed, Status " + status);
        throw new IllegalStateException("Request Failed, Status " + status);
    }

    private static String readBody(ClientHttpResponse clientResponse) throws IOException {
        InputStream body = clientResponse.getBody();
        if (body == null) {
            return null;
        }
        return StreamUtils.copyToString(body, StandardCharsets.UTF_8);
    }

    private static String abbreviate(String value) {
        if (value == null || value.length() <= 500) {
            return value;
        }
        return value.substring(0, 500);
    }

    @Override
    public void updateStatusByMessageId(SMSBridge bridge, String externalId,String orchestrator) throws MessageGatewayException{

    }
    @Override
    public void publishZeebeVariable(OutboundMessages message){

    }


    private String getAuthToken(SMSBridge smsBridgeConfig) {
        logger.info("Fetching Access TOKEN");
        String authApiEnabled = smsBridgeConfig.getConfigValue("auth_api_enabled");
        if(!"true".equals(authApiEnabled)){
            logger.info("Fetching Access TOKEN: authApiEnabled {}, setting static token", authApiEnabled);
            return smsBridgeConfig.getConfigValue(PROVIDER_AUTH_TOKEN);
        }
        String authUserName = smsBridgeConfig.getConfigValue("auth_api_userName");
        String authPassword = smsBridgeConfig.getConfigValue("auth_api_password");
        String authScope = smsBridgeConfig.getConfigValue("auth_api_scope");
        String authApiUrl = smsBridgeConfig.getConfigValue("auth_api_url");

        if(statcTokenMap.get(TOKEN)!=null && System.currentTimeMillis() < (Long) statcTokenMap.get(EXPIRES_AT)){
            logger.info("Fetching Access TOKEN:  setting previous token");
            return (String) statcTokenMap.get(TOKEN);
        }

        logger.info("Fetching Access TOKEN:  Fetching new token");
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(authUserName, authPassword);

        URI uri = UriComponentsBuilder.fromHttpUrl(authApiUrl)
                .queryParam("scope", authScope)
                .build()
                .toUri();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> requestEntity =
                new HttpEntity<>(formData, headers);
        ResponseEntity<OAuthTokenResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                requestEntity,
                OAuthTokenResponse.class
        );

        OAuthTokenResponse token = responseEntity.getBody();
        statcTokenMap.put(TOKEN, token.getAccessToken());
        statcTokenMap.put(EXPIRES_AT, System.currentTimeMillis() + (token.getExpiresIn()*1000));
        logger.info("Fetched Access TOKEN. Token expires in {}s", token.getExpiresIn());
        return (String) statcTokenMap.get(TOKEN);
    }

}
