package org.apache.messagegateway.sms.api;

import java.util.Collection;

import org.apache.messagegateway.helpers.ApiGlobalErrorResponse;
import org.apache.messagegateway.helpers.PlatformApiDataValidationException;
import org.apache.messagegateway.helpers.PlatformApiDataValidationExceptionMapper;
import org.apache.messagegateway.helpers.UnsupportedParameterException;
import org.apache.messagegateway.helpers.UnsupportedParameterExceptionMapper;
import org.apache.messagegateway.sms.domain.SMSBridge;
import org.apache.messagegateway.sms.exception.SMSBridgeNotFoundException;
import org.apache.messagegateway.sms.service.SMSBridgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smsbridge")
public class SmsBridgeApiResource {

	private final SMSBridgeService smsBridgeService ;
	
	@Autowired
    public SmsBridgeApiResource(final SMSBridgeService smsBridgeService) {
		this.smsBridgeService = smsBridgeService ;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long> createSMSBridgeConfig(@RequestBody final SMSBridge smsBridge) {
    		 Long bridgeId = this.smsBridgeService.createSmsBridgeConfig(smsBridge) ;
    		 return new ResponseEntity<>(bridgeId, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{bridgeId}", method = RequestMethod.PUT, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Long>updateSMSBridgeConfig(@PathVariable("bridgeId") final Long bridgeId, @RequestBody final String smsBridge) {
    	this.smsBridgeService.updateSmsBridge(bridgeId, smsBridge);
        return new ResponseEntity<>(bridgeId, HttpStatus.ACCEPTED);
    }
    
    @RequestMapping(value = "/{tenantId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Collection<SMSBridge>> getAllSMSBridgeConfigs(@PathVariable("tenantId") final String tenantId) {
        Collection<SMSBridge> bridges = this.smsBridgeService.retrieveProviderDetails(tenantId) ;
        return new ResponseEntity<>(bridges, HttpStatus.OK);
    }

    @RequestMapping(value = "/{tenantId}/{providerId}", method = RequestMethod.GET, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<SMSBridge> getSMSBridgeConfig(@PathVariable("tenantId") final String tenantId, @PathVariable("providerId") final Long providerId) {
        SMSBridge bridge;
		try {
			bridge = this.smsBridgeService.retrieveProviderDetails(tenantId, providerId);
			return new ResponseEntity<>(bridge, HttpStatus.CREATED);
		} catch (SMSBridgeNotFoundException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }
    
    @ExceptionHandler({PlatformApiDataValidationException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handlePlatformApiDataValidationException(PlatformApiDataValidationException e) {
    	return new PlatformApiDataValidationExceptionMapper().toResponse(e) ;
    }
    
    @ExceptionHandler({UnsupportedParameterException.class})
    public ResponseEntity<ApiGlobalErrorResponse> handleUnsupportedParameterException(UnsupportedParameterException e) {
    	return new UnsupportedParameterExceptionMapper().toResponse(e) ;
    }
}
