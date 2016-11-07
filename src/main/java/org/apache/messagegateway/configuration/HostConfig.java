package org.apache.messagegateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HostConfig {

	@Value("${hostconfig.host-address}")
	private String hostName ;
	
	@Value("${hostconfig.protocol}")
	private String protocol ;
	
	@Value("${server.port}")
	private Integer port ;
	
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
