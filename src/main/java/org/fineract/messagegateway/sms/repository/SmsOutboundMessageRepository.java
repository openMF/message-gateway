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
package org.fineract.messagegateway.sms.repository;

import java.util.Date;
import java.util.List;

import org.fineract.messagegateway.sms.domain.OutboundMessages;
import org.fineract.messagegateway.sms.util.SmsMessageStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SmsOutboundMessageRepository extends JpaRepository<OutboundMessages, Long>, JpaSpecificationExecutor<OutboundMessages> {
	
    /** 
     * find {@link SmsMessageStatusType} objects by delivery status
     * 
     * @param deliveryStatus -- {@link SmsMessageStatusType} deliveryStatus
     * @param pageable -- Abstract interface for pagination information.
     * @return List of {@link SmsMessageStatusType} list
     **/
    Page<OutboundMessages> findByDeliveryStatus(Integer deliveryStatus, Pageable pageable);

	@Query("select m from OutboundMessages m where m.deliveryStatus = :status and (m.createdOnUtc is null or m.createdOnUtc < :createdBefore) order by m.id asc")
	Page<OutboundMessages> findRetryablePendingMessages(@Param("status") Integer status, @Param("createdBefore") Date createdBefore, Pageable pageable);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Transactional
	@Query("update OutboundMessages m set m.claimedAt = :claimedAt where m.id = :id and m.deliveryStatus = :pendingStatus and (m.claimedAt is null or m.claimedAt < :staleBefore)")
	int claimPendingMessage(@Param("id") Long id, @Param("claimedAt") Date claimedAt, @Param("pendingStatus") Integer pendingStatus, @Param("staleBefore") Date staleBefore);
	
	/** 
	 * find {@link SmsMessageStatusType} object by externalId
	 * 
	 * @param externalId -- {@link SmsMessageStatusType} externalId
	 * @return {@link SmsMessageStatusType}
	 **/
    OutboundMessages findByExternalId(String externalId);

	/** 
	 * find {@link SmsMessageStatusType} objects with id in "idList" and tenantId equal to "tenantId"
	 *
	 * @param idList -- {@link SmsMessageStatusType} id list
	 * @param tenantId -- id of the tenant the messages belong to
	 * @return List of {@link SmsMessageStatusType} objects
	 **/
	List<OutboundMessages> findByIdInAndTenantId(List<Long> idList, Long tenantId);

	OutboundMessages findByInternalId(Long internalId);
}
