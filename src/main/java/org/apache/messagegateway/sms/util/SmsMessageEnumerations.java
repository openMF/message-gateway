package org.apache.messagegateway.sms.util;

import org.apache.messagegateway.sms.data.EnumOptionData;

/** 
 * SMS message enumerations 
 **/
public class SmsMessageEnumerations {
	
	/** 
	 * SMS message delivery status enumeration 
	 * 
	 * @return EnumOptionData object
	 **/
	public static EnumOptionData status(final Integer statusId) {
        return status(SmsMessageStatusType.fromInt(statusId));
    }

	/** 
	 * SMS message delivery status enumeration 
	 * 
	 * @return EnumOptionData object
	 **/
    public static EnumOptionData status(final SmsMessageStatusType status) {
        EnumOptionData optionData = new EnumOptionData(SmsMessageStatusType.INVALID.getValue().longValue(),
                SmsMessageStatusType.INVALID.getCode(), "Invalid");
        
        switch (status) {
            case INVALID:
                optionData = new EnumOptionData(SmsMessageStatusType.INVALID.getValue().longValue(),
                        SmsMessageStatusType.INVALID.getCode(), "Invalid");
            break;
            case PENDING:
                optionData = new EnumOptionData(SmsMessageStatusType.PENDING.getValue().longValue(),
                        SmsMessageStatusType.PENDING.getCode(), "Pending");
            break;
            case SENT:
                optionData = new EnumOptionData(SmsMessageStatusType.SENT.getValue().longValue(), SmsMessageStatusType.SENT.getCode(),
                        "Sent");
            break;
            case DELIVERED:
                optionData = new EnumOptionData(SmsMessageStatusType.DELIVERED.getValue().longValue(),
                        SmsMessageStatusType.DELIVERED.getCode(), "Delivered");
            break;
            case FAILED:
                optionData = new EnumOptionData(SmsMessageStatusType.FAILED.getValue().longValue(), SmsMessageStatusType.FAILED.getCode(),
                        "Failed");
            break;

        }

        return optionData;
    }
}
