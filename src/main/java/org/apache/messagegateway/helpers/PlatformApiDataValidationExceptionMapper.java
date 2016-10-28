/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.messagegateway.helpers;

import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * An {@link ExceptionMapper} to map {@link PlatformApiDataValidationException}
 * thrown by platform into a HTTP API friendly format.
 * 
 * The {@link PlatformApiDataValidationException} is typically thrown in data
 * validation of the parameters passed in with an api request.
 */
@Provider
@Component
@Scope("singleton")
public class PlatformApiDataValidationExceptionMapper {

    public ResponseEntity<ApiGlobalErrorResponse> toResponse(final PlatformApiDataValidationException exception) {
        final ApiGlobalErrorResponse dataValidationErrorResponse = ApiGlobalErrorResponse.badClientRequest(
                exception.getGlobalisationMessageCode(), exception.getDefaultUserMessage(), exception.getErrors());
        return new ResponseEntity<>(dataValidationErrorResponse, HttpStatus.BAD_REQUEST) ;
    }
}