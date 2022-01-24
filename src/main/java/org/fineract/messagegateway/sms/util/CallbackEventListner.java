package org.fineract.messagegateway.sms.util;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.service.SMSMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
@Component
public class CallbackEventListner implements ApplicationListener<CallbackEvent> {

    @Value("${callbackconfig.host}")
    private String address ;

    @Value("${callbackconfig.protocol}")
    private String protocol ;

    @Value("${callbackconfig.port}")
    private int port ;

    private static final Logger logger = LoggerFactory.getLogger(CallbackEventListner.class);

    private SMSMessageService smsMessageService;

    public MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public CallbackEventListner(SMSMessageService smsMessageService) {
        this.smsMessageService = smsMessageService;
    }

    String post(String url, String json) throws IOException {
        logger.info("In post of callback event listener");
        RequestBody body = RequestBody.create(JSON, String.valueOf(json));
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    @Override
    public void onApplicationEvent(CallbackEvent event) {
        logger.info("From application event method in callback listener");
        SMSMessage message = event.getMessage();
        logger.info("Back to listener from event with message ="+ message);
        Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryCallbackStatus
                (message.getExternalId());
        String json = new Gson().toJson( deliveryStatus);
        String url=(String.format("%s://%s:%d/sms/callback/", protocol, address, port));
        try {
            post(url,json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("From application event "+ url + " " + json);
    }
}

