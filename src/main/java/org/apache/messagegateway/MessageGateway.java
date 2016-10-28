package org.apache.messagegateway;

import org.apache.messagegateway.configuration.MessageGatewayConfiguration;
import org.springframework.boot.SpringApplication;

public class MessageGateway {

    public MessageGateway() {
        super();
    }

    public static void main(String[] args) {
        SpringApplication.run(MessageGatewayConfiguration.class, args);
    }
}
