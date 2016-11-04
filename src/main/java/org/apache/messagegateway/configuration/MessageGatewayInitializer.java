package org.apache.messagegateway.configuration;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

public class MessageGatewayInitializer extends SpringBootServletInitializer {

    public MessageGatewayInitializer() {
        super();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MessageGatewayConfiguration.class);
    }
}
