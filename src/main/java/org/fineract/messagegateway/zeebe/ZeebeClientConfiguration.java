package org.fineract.messagegateway.zeebe;


import io.camunda.zeebe.client.ZeebeClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZeebeClientConfiguration {

    @Value("${zeebe.broker.contactpoint}")
    private String zeebeBrokerContactpoint;

    @Value("${zeebe.client.max-execution-threads}")
    private int zeebeClientMaxThreads;

    @Bean
    public ZeebeClient setup() {
        return ZeebeClient.newClientBuilder()
                .gatewayAddress(zeebeBrokerContactpoint)
                .usePlaintext()
                .numJobWorkerExecutionThreads(zeebeClientMaxThreads)
                .build();
    }
}
