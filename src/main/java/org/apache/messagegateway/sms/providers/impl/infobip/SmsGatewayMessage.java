package org.apache.messagegateway.sms.providers.impl.infobip;

/** 
 * Immutable data object representing a sms gateway message 
 **/
public class SmsGatewayMessage {
	/** 
     * the internal message identifier (mifos sms message table id) 
     **/
    private Long id;
    
    /** 
     * the SMS gateway message identifier
     **/
    private String externalId;
    
    /** 
     * 
     * the sender of the SMS message 
     **/
    private String sourceAddress;
    
    /** 
     * mobile phone number of sms message recipient, must be in international format without the leading
     * "0" or "+", example: 31612345678
     **/
    private String mobileNumber;
    
    /** 
     * the sms message text to be sent out
     **/
    private String message;
    
    /** 
     * SmsGatewayMessage constructor
     * 
     * @param id the internal message identifier
     * @param externalId the sms gateway message identifier
     * @param mobileNumber the mobile number of sms message recipient
     * @param message the sms message text
     **/
    public SmsGatewayMessage(Long id, String externalId, String sourceAddress, String mobileNumber, String message) {
        this.id = id;
        this.mobileNumber = mobileNumber;
        this.message = message;
        this.externalId = externalId;
        this.sourceAddress = sourceAddress;
    }

    /**
     * @return the id
     **/
    public Long getId() {
        return id;
    }
    
    /** 
     * @return the mobileNumber 
     **/
    public String getMobileNumber() {
        return mobileNumber;
    }
    
    /** 
     * @return the message 
     **/
    public String getMessage() {
        return message;
    }
    
    /** 
     * @return the externalId 
     **/
    public String getExternalId() {
        return externalId;
    }
    
    @Override
    /** 
     * @return String representation of the SmsGatewayMessage class
     **/
    public String toString() {
        return "SmsGatewayDeliveryReport [id=" + id + ", externalId=" + externalId + ", sourceAddress" + sourceAddress +
        		", mobileNumber=" + mobileNumber + ", message=" + message + "]";
    }

	/**
	 * @return the sourceAddress
	 */
	public String getSourceAddress() {
		return sourceAddress;
	}
}
