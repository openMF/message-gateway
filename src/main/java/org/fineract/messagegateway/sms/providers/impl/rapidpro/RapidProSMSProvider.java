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
package org.fineract.messagegateway.sms.providers.impl.rapidpro;

import okhttp3.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.fineract.messagegateway.configuration.HostConfig;
import org.fineract.messagegateway.constants.MessageGatewayConstants;
import org.fineract.messagegateway.exception.MessageGatewayException;
import org.fineract.messagegateway.sms.domain.SMSBridge;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.providers.SMSProvider;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "RapidProSMS")
public class RapidProSMSProvider extends SMSProvider {

  private static final Logger logger = LoggerFactory.getLogger(
    RapidProSMSProvider.class
  );

  private HashMap<String, OkHttpClient> restClients = new HashMap<>();

  private static final String SCHEME = "https";

  private final String callBackUrl;

  @Autowired
  public RapidProSMSProvider(final HostConfig hostConfig) {
    callBackUrl =
      String.format(
        "%s://%s:%d/rapidprosms/report/",
        hostConfig.getProtocol(),
        hostConfig.getHostName(),
        hostConfig.getPort()
      );
    logger.info("Registering call back to rapidprosms:" + callBackUrl);
  }

  @Override
  public void sendMessage(SMSBridge smsBridgeConfig, SMSMessage message)
    throws MessageGatewayException {
    logger.info("Reached RapidPro Provider...");
    OkHttpClient okHttpClient = getRestClient(smsBridgeConfig);
    String url = smsBridgeConfig.getConfigValue(
      MessageGatewayConstants.PROVIDER_URL
    ) + "/broadcasts.json";
    String providerAuthToken = smsBridgeConfig.getConfigValue(
      MessageGatewayConstants.PROVIDER_AUTH_TOKEN
    );
    logger.info("URL.....{}", url);
    try {
      OkHttpClient client = new OkHttpClient.Builder().build();
      MediaType mediaType = MediaType.parse("application/json");
      RequestBody body = RequestBody.create(
        mediaType,
        "{\"urns\": [\"tel:+919658600961\"],\"contacts\": [\"6006fddf-0b0f-4e29-b1a9-b929c17c8301\"],\"text\": \"hello Rohnak\"}"
      );
      Request request = new Request.Builder()
        .url(url)
        .method("POST", body)
        .addHeader(
          "Authorization",
          "Token 9e25e87271ef3eebc89fd430340d93f3409ed357"
        )
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      message.setDeliveryStatus(SmsMessageStatusType.SENT.getValue());
      message.setDeliveredOnDate(new Date());
      message.setResponse(response.body().string());
    } catch (IOException e) {
      throw new MessageGatewayException(e.getMessage());
    }
  }

  private OkHttpClient getRestClient(final SMSBridge smsBridge) {
    String authorizationKey = encodeBase64(smsBridge);
    OkHttpClient client = this.restClients.get(authorizationKey);
    if (client == null) {
      client = this.get(smsBridge);
      this.restClients.put(authorizationKey, client);
    }
    return client;
  }

  OkHttpClient get(final SMSBridge smsBridgeConfig) {
    logger.debug("Creating a new Twilio Client ....");

    final OkHttpClient client = new OkHttpClient();
    return client;
  }
}
