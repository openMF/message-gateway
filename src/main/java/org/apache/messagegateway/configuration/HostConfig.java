package org.apache.messagegateway.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class HostConfig {

	private final static String HOST_NAME = "HOSTNAME" ;
	private final static String PROTOCOL_NAME = "PROTOCOL" ;
	private final static String PORT_NAME = "PORT" ;
	
	private String hostName ;
	private String protocol ;
	private Integer port ;
	
	@Autowired ApplicationContext context ;
	
	@PostConstruct
	protected void init() {
		Environment environment = context.getEnvironment() ;
		hostName = (String)environment.getProperty(HOST_NAME) ;
		protocol = (String) environment.getProperty(PROTOCOL_NAME) ;
		port = Integer.parseInt((String) environment.getProperty(PORT_NAME)) ;
	}
	
    public String getHostName() {
    	return this.hostName ;
    }
    
    public String getProtocol() {
    	return this.protocol ;
    }

    public Integer getPort() {
    	return this.port ;
    }
}
