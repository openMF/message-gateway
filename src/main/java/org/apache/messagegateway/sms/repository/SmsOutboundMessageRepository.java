package org.apache.messagegateway.sms.repository;

import java.util.List;

import org.apache.messagegateway.sms.domain.SMSMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SmsOutboundMessageRepository extends JpaRepository<SMSMessage, Long>, JpaSpecificationExecutor<SMSMessage> {
	
    /** 
     * find {@link SmsMessageStatusType} objects by delivery status
     * 
     * @param deliveryStatus -- {@link SmsMessageStatusType} deliveryStatus
     * @param pageable -- Abstract interface for pagination information.
     * @return List of {@link SmsMessageStatusType} list
     **/
    List<SMSMessage> findByDeliveryStatus(Integer deliveryStatus, Pageable pageable);
	
	/** 
	 * find {@link SmsMessageStatusType} object by externalId
	 * 
	 * @param externalId -- {@link SmsMessageStatusType} externalId
	 * @return {@link SmsMessageStatusType}
	 **/
    SMSMessage findByExternalId(String externalId);
	
	/** 
	 * find {@link SmsMessageStatusType} objects with id in "idList" and mifosTenantIdentifier equal to "mifosTenantIdentifier"
	 * 
	 * @param idList -- {@link SmsMessageStatusType} id list
	 * @param mifosTenantIdentifier -- Mifos X tenant identifier e.g. demo
	 * @return List of {@link SmsMessageStatusType} objects
	 **/
	List<SMSMessage> findByIdInAndTenantId(List<Long> idList, String mifosTenantIdentifier);
}
