/*
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
package org.fineract.messagegateway.sms.providers.impl.telerivet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.camunda.zeebe.client.ZeebeClient;
import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.providers.Provider;
import org.fineract.messagegateway.sms.repository.SmsOutboundMessageRepository;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.telerivet.*;

import static org.fineract.messagegateway.zeebe.ZeebeVariables.*;

@Service(value = "telerivet")
@Component
public class TelerivetMessageProvider extends Provider {

    @Value("${providerSource.fromyml}")
    private String ymlCheck;

    @Value("${providerKeys.telerivetApiKey}")
    private String apiKey;

    @Value("${providerKeys.telerivetProjectId}")
    private String projectId;

    private static final Logger logger = LoggerFactory.getLogger(TelerivetMessageProvider.class);

    private final String callBackUrl;

    @Autowired
    private SmsOutboundMessageRepository smsOutboundMessageRepository;

    @Autowired
    private ZeebeClient zeebeClient;


    @Autowired
    public TelerivetMessageProvider(final HostConfig hostConfig) {
        callBackUrl = String.format("%s://%s/telerivet/report/", hostConfig.getProtocol(), hostConfig.getHostName());
        logger.info("Registering call back to Telerivet: {}", callBackUrl);
    }

    @Override
    public void sendMessage(SMSBridge smsBridgeConfig, OutboundMessages message) throws MessageGatewayException {
        String providerAPIKey = null;
        String providerProjectId = null;
        if(ymlCheck.equals("enabled")){
           logger.info("Yml values are enables so attaching provider related values from yml");
           providerAPIKey = apiKey;
           providerProjectId = projectId;
        }
        else{
          providerAPIKey = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_API_KEY);
          providerProjectId = smsBridgeConfig.getConfigValue(MessageGatewayConstants.PROVIDER_PROJECT_ID);
        }

        String statusURL = callBackUrl;
        String mobileNumber = message.getMobileNumber();
        String messageToSend = message.getMessage();
        try {
            TelerivetAPI tr = new TelerivetAPI(providerAPIKey);
            Project project = tr.initProjectById(providerProjectId);
            logger.info("Sending SMS to {} ...",mobileNumber);
            Message sent_msg = project.sendMessage(Util.options(
                    "content", messageToSend,
                    "to_number", mobileNumber,
                    "status-url", statusURL));
            message.setDeliveryStatus(TelerivetStatus.smsStatus(sent_msg.getStatus()).getValue());
            message.setExternalId(sent_msg.getId());

            logger.debug("Publishing internal id {} and external id {} to the zeebe workflow:",message.getInternalId(),message.getExternalId());

            publishZeebeVariable(message);

            logger.info(sent_msg.getId());
            logger.info("TelerivetMessageProvider.sendMessage(): {}",  TelerivetStatus.smsStatus(sent_msg.getStatus()));

            if (message.getDeliveryStatus().equals(SmsMessageStatusType.FAILED.getValue())) {
                message.setDeliveryErrorMessage(sent_msg.getErrorMessage());
                logger.error("Sending SMS to : {}  failed with reason: {}", message.getMobileNumber(), sent_msg.getErrorMessage());
            }
        } catch (IOException e) {
            logger.error("ApiException while sending message to: {} with reason: {}" , message.getMobileNumber() , e.getMessage());
            message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
            message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
            message.setDeliveryStatus(SmsMessageStatusType.FAILED.getValue());
            message.setDeliveryErrorMessage(e.getMessage());
        }
    }

    @Override
    public void updateStatusByMessageId(SMSBridge bridge, String externalId,String orchestrator) throws MessageGatewayException {
        Message msg = null;
        try {
            logger.info("Fetching message status by id");
            String providerAPIKey = null;
            String providerProjectId = null;
            if(ymlCheck.equals("enabled")){
                logger.info("Yml values are enables so attaching provider related values from yml");
                providerAPIKey = apiKey;
                providerProjectId = projectId;
            }
            else{
                providerAPIKey = bridge.getConfigValue(MessageGatewayConstants.PROVIDER_API_KEY);
                providerProjectId = bridge.getConfigValue(MessageGatewayConstants.PROVIDER_PROJECT_ID);
            }

            OutboundMessages message = this.smsOutboundMessageRepository.findByExternalId(externalId);
            TelerivetAPI tr = new TelerivetAPI(providerAPIKey);
            Project project = tr.initProjectById(providerProjectId);
            msg = project.getMessageById(externalId);
            message.setDeliveryStatus(TelerivetStatus.smsStatus(msg.getStatus()).getValue());
            message.setDeliveryErrorMessage(msg.getErrorMessage());
            if(orchestrator == null){
                this.smsOutboundMessageRepository.save(message) ;
            }else{
                if(message.getDeliveryStatus() == 300 || message.getDeliveryStatus() == 400) {
                    publishZeebeVariable(message);
                }
            }
            logger.debug("Value updated");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publishZeebeVariable(OutboundMessages message){
        logger.info("---------------- Publishing zeebe variable ----------------");
        Map<String,Object> map=new HashMap<String,Object>();
        map.put(MESSAGE_EXTERNAL_ID,message.getExternalId());
        map.put(MESSAGE_INTERNAL_ID,message.getInternalId());
        map.put(TENANT_ID,message.getTenantId());
        map.put(BRIDGE_ID,message.getBridgeId());
        map.put(DELIVERY_STATUS_CODE,message.getDeliveryStatus());

        map.put(TRANSACTION_ID,message.getInternalId());
        if (message.getDeliveryStatus() == 300) {
            map.put(MESSAGE_DELIVERY_STATUS, true);
        }
        if (message.getDeliveryStatus() == 400) {
            map.put(DELIVERY_ERROR_MESSAGE,message.getDeliveryErrorMessage());
            map.put(MESSAGE_DELIVERY_STATUS, false);
        }
        zeebeClient.newSetVariablesCommand(message.getInternalId())
                .variables(map)
                .send()
                .join();

        logger.info("----------------Zeebe variable published----------------");
    }
}