package org.apache.messagegateway.configuration;

import org.apache.messagegateway.sms.domain.SMSBridge;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(basePackages = {
        "org.mifos.module.sms.repository",
        "org.apache.messagegateway.sms.repository"
})
@EntityScan(basePackageClasses = {
        SMSBridge.class,
        org.apache.messagegateway.sms.domain.SMSBridgeConfig.class
})
@ComponentScan(basePackages = {
        "org.apache.messagegateway.sms.api",
        "org.apache.messagegateway.*"
})
public class MessageGatewayConfiguration {

    public MessageGatewayConfiguration() {
        super();
    }

    @Bean
    public SimpleApplicationEventMulticaster applicationEventMulticaster() {
        final SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return multicaster;
    }
}
