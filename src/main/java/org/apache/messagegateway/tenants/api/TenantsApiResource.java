package org.apache.messagegateway.tenants.api;

import org.apache.messagegateway.tenants.domain.Tenant;
import org.apache.messagegateway.tenants.service.TenantsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tenants")
public class TenantsApiResource {

	private final TenantsService tenantService ;
	
	@Autowired
	public TenantsApiResource(final TenantsService tenantService) {
		this.tenantService = tenantService ;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<String> createSMSBridgeConfig(@RequestBody final Tenant tenant) {
    		 String appKey = this.tenantService.createTenant(tenant) ;
    		 return new ResponseEntity<>(appKey, HttpStatus.CREATED);
    }
}
