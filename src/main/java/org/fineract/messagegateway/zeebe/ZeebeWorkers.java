package org.fineract.messagegateway.zeebe;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.camunda.zeebe.client.ZeebeClient;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.fineract.messagegateway.provider.config.ProviderConfig;
import org.fineract.messagegateway.sms.api.SmsApiResource;
import org.fineract.messagegateway.sms.data.DeliveryStatusData;
import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

import static org.fineract.messagegateway.camel.config.CamelProperties.*;

import static org.fineract.messagegateway.zeebe.ZeebeVariables.*;

@Component
public class ZeebeWorkers {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CamelContext camelContext;

    @Autowired
    private ZeebeClient zeebeClient;

    @Value("${zeebe.client.evenly-allocated-max-jobs}")
    private int workerMaxJobs;

    @Autowired
    private SmsApiResource smsApiResource;

    @Autowired
    private ProviderConfig providerConfig;

    @Value("${zeebe.worker.retries}")
    private Integer retries;

    @Value("${zeebe.worker.timer}")
    private String timer;
    @Value("${zeebe.client.ttl}")
    private int timeToLive;

    @Value("${operationsconfig.tenantidvalue}")
    private String tenantIdValue;
    @Value("${operationsconfig.tenantappvalue}")
    private String tenantAppKeyValue;


    @PostConstruct
    public void setupWorkers() {
        zeebeClient.newWorker()
                .jobType("mg_send_message")
                .handler((client, job) -> {
                    logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
                    String instanceKey = String.valueOf(job.getProcessInstanceKey());
                    Map<String, Object> variables = job.getVariablesAsMap();
                    Exchange exchange = new DefaultExchange(camelContext);

                    exchange.setProperty(MOBILE_NUMBER,variables.get(PHONE_NUMBER));

                    exchange.setProperty(CORRELATION_ID, instanceKey);

                    exchange.setProperty(INTERNAL_ID,instanceKey);
                    exchange.setProperty(DELIVERY_MESSAGE,variables.get(MESSAGE_TO_SEND));
                    variables.put(RETRIES,retries);
                    variables.put(TIMER,timer);

                    String mobile = exchange.getProperty(MOBILE_NUMBER).toString();
                    Long internalId = Long.parseLong(exchange.getProperty(INTERNAL_ID).toString());
                    Long providerId = providerConfig.getProviderConfig();
                    OutboundMessages outboundMessages = new OutboundMessages(null,internalId,null,null,null, SmsMessageStatusType.PENDING,null,null,mobile,exchange.getProperty(DELIVERY_MESSAGE).toString(),providerId);

                    List<OutboundMessages> payload = new ArrayList<OutboundMessages>();
                    payload.add(outboundMessages);

                    smsApiResource.sendShortMessagesToProvider(tenantIdValue,tenantAppKeyValue,"zeebe",payload);

                        client.newCompleteCommand(job.getKey()).variables(variables)
                                .send()
                                .join()
                        ;
                })
                .name("mg_send_message")
                .maxJobsActive(workerMaxJobs)
                .open();

        zeebeClient.newWorker()
                .jobType("mg_get_mannual_status")
                .handler((client, job) -> {
                    logger.info("Job '{}' started from process '{}' with key {}", job.getType(), job.getBpmnProcessId(), job.getKey());
                    Map<String, Object> variables = job.getVariablesAsMap();
                    variables.put(CALLBACK_RETRY_COUNT, 1 + (Integer) variables.getOrDefault(CALLBACK_RETRY_COUNT, 0));
                    Exchange exchange = new DefaultExchange(camelContext);
                    exchange.setProperty(INTERNAL_ID,variables.get(MESSAGE_INTERNAL_ID));
                    exchange.setProperty(CORRELATION_ID, variables.get(INTERNAL_ID));
                    exchange.setProperty(RETRY_COUNT_CALLBACK, variables.get(CALLBACK_RETRY_COUNT));

                    if(Integer.parseInt(exchange.getProperty(RETRY_COUNT_CALLBACK).toString()) < 3) {

                        if (exchange.getProperty(INTERNAL_ID) != null) {
                            Long internalId = Long.parseLong(exchange.getProperty(INTERNAL_ID).toString());
                            List<Long> payload = new ArrayList<Long>();
                            payload.add(internalId);
                            //Getting delivery status of the message form the provider.
                            ResponseEntity<Collection<DeliveryStatusData>> data = smsApiResource.getDeliveryStatus(tenantIdValue, tenantAppKeyValue, "zeebe", payload);

                            fetchAndPublishDeliveryStatus(data,exchange);
                        }
                    }else{
                        logger.info("Callback Retry Over");
                        exchange.setProperty(MESSAGE_DELIVERY_STATUS,false);
                        String id = exchange.getProperty(CORRELATION_ID, String.class);
                        Map<String, Object> newVariables = new HashMap<>();
                        newVariables.put(MESSAGE_DELIVERY_STATUS, exchange.getProperty(MESSAGE_DELIVERY_STATUS));
                        newVariables.put(CALLBACK_RETRY_COUNT,exchange.getProperty(RETRY_COUNT_CALLBACK));
                        zeebeClient.newSetVariablesCommand(Long.parseLong(exchange.getProperty(INTERNAL_ID).toString()))
                                .variables(newVariables)
                                .send()
                                .join();
                        logger.info("Publishing created messages to variables: {}", newVariables);
                        zeebeClient.newPublishMessageCommand()
                                .messageName(CALLBACK_MESSAGE)
                                .correlationKey(id)
                                .timeToLive(Duration.ofMillis(timeToLive))
                                .variables(newVariables)
                                .send()
                                .join();
                    }
                    client.newCompleteCommand(job.getKey())
                            .send()
                            .join()
                    ;
                })
                .name("mg_get_mannual_status")
                .maxJobsActive(workerMaxJobs)
                .open();

    }
    public void fetchAndPublishDeliveryStatus(ResponseEntity<Collection<DeliveryStatusData>> data,Exchange exchange){
        String id = exchange.getProperty(CORRELATION_ID, String.class);
        if (!data.getBody().isEmpty() && !data.getBody().equals(null)){
            Collection<DeliveryStatusData> deliveryStatusData = data.getBody();
            JsonElement jsonElement = new Gson().toJsonTree(deliveryStatusData);

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            int deliveryStatus = jsonObject.get("deliveryStatus").getAsInt();
            if (deliveryStatus == 300) {
                logger.info("Passed");
                exchange.setProperty(MESSAGE_DELIVERY_STATUS, true);
            } else {
                boolean hasError = jsonObject.get("hasError").getAsBoolean();
                if (!hasError && jsonObject.has("errorMessage")) {
                    if (jsonObject.get("errorMessage").isJsonNull()) {
                        logger.info("Still Pending, will retry");
                    } else {
                        logger.info("Error encountered: " + jsonObject.get("errorMessage").getAsString());
                        exchange.setProperty(DELIVERY_ERROR_INFORMATION, jsonObject.get("errorMessage").getAsString());
                        exchange.setProperty(MESSAGE_DELIVERY_STATUS, false);
                    }
                }
            }
            Map<String, Object> newVariables = new HashMap<>();
            if (exchange.getProperty(MESSAGE_DELIVERY_STATUS) != null) {
                if (exchange.getProperty(MESSAGE_DELIVERY_STATUS).equals(true)) {
                    logger.info("Publishing variables: {}", newVariables);

                    //Publish zeebe variables
                    publishZeebeVariables(newVariables,exchange,id);

                } else if (exchange.getProperty(MESSAGE_DELIVERY_STATUS).equals(false)) {
                    logger.info("Publishing variables: {}", newVariables);
                    newVariables.put(DELIVERY_ERROR_MESSAGE, exchange.getProperty(DELIVERY_ERROR_INFORMATION));

                    //Publish zeebe variables
                    publishZeebeVariables(newVariables,exchange,id);
                }
            } else {
                logger.info("Publishing created variables to variables: {}",  newVariables);
                newVariables.put(CALLBACK_RETRY_COUNT, exchange.getProperty(RETRY_COUNT_CALLBACK));
                zeebeClient.newSetVariablesCommand(Long.parseLong(exchange.getProperty(INTERNAL_ID).toString()))
                        .variables(newVariables)
                        .send()
                        .join();
            }
        }
    }
    public void publishZeebeVariables(Map<String, Object> newVariables,Exchange exchange,String id){
        newVariables.put(MESSAGE_DELIVERY_STATUS, exchange.getProperty(MESSAGE_DELIVERY_STATUS));
        zeebeClient.newPublishMessageCommand()
                .messageName(CALLBACK_MESSAGE)
                .correlationKey(id)
                .variables(newVariables)
                .timeToLive(Duration.ofMillis(timeToLive))
                .send()
                .join();
    }
}