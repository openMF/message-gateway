package org.apache.messagegateway.configuration;

import org.apache.messagegateway.sms.domain.AbstractPersistableCustom;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.domain.SMSBridgeConfig;
import org.apache.messagegateway.sms.domain.SMSMessage;
import org.apache.messagegateway.tenants.domain.Tenant;
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
        "org.apache.messagegateway.sms.repository",
        "org.apache.messagegateway.tenants.repository"
		
})
@EntityScan(basePackageClasses = {
		AbstractPersistableCustom.class,
        SMSBridge.class,
        SMSBridgeConfig.class,
        SMSMessage.class,
        Tenant.class
})
@ComponentScan(basePackages = {
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
