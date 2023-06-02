package org.fineract.messagegateway.provider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {
    @Value("${provider.id}")
    private Long id;


    @Bean
    @ConditionalOnProperty(
            value="provider.enabled",
            havingValue = "true")
    public Long getProviderConfig(){
        return id;
    }

}
