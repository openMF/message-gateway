package org.fineract.messagegateway.sms.util;

import com.google.gson.Gson;
import com.squareup.okhttp.*;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.SMSMessage;
import org.fineract.messagegateway.sms.service.SMSMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.util.Collection;

public class CallbackEventListner implements ApplicationListener<CallbackEvent> {

    @Value("${notificationsconfig.address}")
    private String address ;

    @Value("${notificationsconfig.protocol}")
    private String protocol ;

    @Value("${notificationsconfig.address}")
    private Integer port ;


    private SMSMessageService smsMessageService;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();
    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
    @EventListener
    @Override
    @Order(1)
    public void onApplicationEvent(CallbackEvent event) {
        SMSMessage message = event.getMessage();
        Collection<DeliveryStatusData> deliveryStatus = this.smsMessageService.getDeliveryCallbackStatus
                (message.getExternalId());
        String json = new Gson().toJson( deliveryStatus, String.class);
        String url=(String.format("%s://%s:%d/sms/callback/", protocol, address, port));
        try {
            post(url,json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

