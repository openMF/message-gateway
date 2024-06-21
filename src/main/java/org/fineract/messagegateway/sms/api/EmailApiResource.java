package org.fineract.messagegateway.sms.api;

import org.fineract.messagegateway.sms.data.EmailRequestDTO;
import org.fineract.messagegateway.sms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/emails")
public class EmailApiResource {

    @Autowired
    EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendEmail(
            @RequestHeader("Platform-Tenant-Id") String platformTenantId,
            @RequestHeader(value = "X-CallbackUrl", required = false) String callbackUrl,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @Valid @RequestBody EmailRequestDTO emailRequest
    ) {
        if (platformTenantId == null || platformTenantId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Platform-Tenant-Id header is missing");
        }

        emailService.sendEmail(emailRequest,callbackUrl);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Email accepted to be sent");
    }
}

