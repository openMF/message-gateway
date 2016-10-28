package org.apache.messagegateway.sms.data;

import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.TypeOfNumber;

/** 
 * Represents an sms short message to be sent out to the SMSC (short message service center)
 **/
public class SmsShortMessage {
    private final Long messageId;
    private final String serviceType;
    private final TypeOfNumber sourceAddressTypeofNumber;
    private final NumberingPlanIndicator sourceAddressNumberingPlanIndicator;
    private final String sourceAddress;
    private final TypeOfNumber destinationAddressTypeOfNumber;
    private final NumberingPlanIndicator destinationAddressNumberingPlanIndicator;
    private final String destinationAddress;
    private final ESMClass esmClass;
    private final byte protocolId;
    private final byte priorityFlag;
    private final String scheduleDeliveryTime;
    private final String validityPeriod;
    private final RegisteredDelivery registeredDelivery;
    private final byte replaceIfPresentFlag;
    private final DataCoding dataCoding;
    private final byte defaultMessageId;
    private final byte[] shortMessageBytes;
    private final OptionalParameter[] optionalParameters;
    private final int messageSegmentNumber;
    private final int totalNumberOfMessageSegments;
    
    /**
     * @param messageId
     * @param serviceType
     * @param sourceAddressTypeofNumber
     * @param sourceAddressNumberingPlanIndicator
     * @param sourceAddress
     * @param destinationAddressTypeOfNumber
     * @param destinationAddressNumberingPlanIndicator
     * @param destinationAddress
     * @param esmClass
     * @param protocolId
     * @param priorityFlag
     * @param scheduleDeliveryTime
     * @param validityPeriod
     * @param registeredDelivery
     * @param replaceIfPresentFlag
     * @param dataCoding
     * @param defaultMessageId
     * @param shortMessageBytes
     * @param optionalParameters
     * @param messageSegmentNumber
     * @param totalNumberOfMessageSegments
     */
    private SmsShortMessage(final Long messageId, final String serviceType, 
            final TypeOfNumber sourceAddressTypeofNumber,
            final NumberingPlanIndicator sourceAddressNumberingPlanIndicator, final String sourceAddress,
            TypeOfNumber destinationAddressTypeOfNumber,
            NumberingPlanIndicator destinationAddressNumberingPlanIndicator, String destinationAddress,
            ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod,
            RegisteredDelivery registeredDelivery, byte replaceIfPresentFlag, DataCoding dataCoding,
            byte defaultMessageId, final byte[] shortMessageBytes, int messageSegmentNumber,
            int totalNumberOfMessageSegments, OptionalParameter... optionalParameters) {
        this.messageId = messageId;
        this.serviceType = serviceType;
        this.sourceAddressTypeofNumber = sourceAddressTypeofNumber;
        this.sourceAddressNumberingPlanIndicator = sourceAddressNumberingPlanIndicator;
        this.sourceAddress = sourceAddress;
        this.destinationAddressTypeOfNumber = destinationAddressTypeOfNumber;
        this.destinationAddressNumberingPlanIndicator = destinationAddressNumberingPlanIndicator;
        this.destinationAddress = destinationAddress;
        this.esmClass = esmClass;
        this.protocolId = protocolId;
        this.priorityFlag = priorityFlag;
        this.scheduleDeliveryTime = scheduleDeliveryTime;
        this.validityPeriod = validityPeriod;
        this.registeredDelivery = registeredDelivery;
        this.replaceIfPresentFlag = replaceIfPresentFlag;
        this.dataCoding = dataCoding;
        this.defaultMessageId = defaultMessageId;
        this.shortMessageBytes = shortMessageBytes;
        this.optionalParameters = optionalParameters;
        this.messageSegmentNumber = messageSegmentNumber;
        this.totalNumberOfMessageSegments = totalNumberOfMessageSegments;
    }
    
    /**
     * @param messageId
     * @param serviceType
     * @param sourceAddressTypeofNumber
     * @param sourceAddressNumberingPlanIndicator
     * @param sourceAddress
     * @param destinationAddressTypeOfNumber
     * @param destinationAddressNumberingPlanIndicator
     * @param destinationAddress
     * @param esmClass
     * @param protocolId
     * @param priorityFlag
     * @param scheduleDeliveryTime
     * @param validityPeriod
     * @param registeredDelivery
     * @param replaceIfPresentFlag
     * @param dataCoding
     * @param defaultMessageId
     * @param shortMessageBytes
     * @param messageSegmentNumber
     * @param totalNumberOfMessageSegments
     * @param optionalParameters
     * @return
     */
    public static SmsShortMessage newSmsShortMessage(final Long messageId, String serviceType, TypeOfNumber sourceAddressTypeofNumber,
            NumberingPlanIndicator sourceAddressNumberingPlanIndicator, String sourceAddress,
            TypeOfNumber destinationAddressTypeOfNumber,
            NumberingPlanIndicator destinationAddressNumberingPlanIndicator, String destinationAddress,
            ESMClass esmClass, byte protocolId, byte priorityFlag, String scheduleDeliveryTime, String validityPeriod,
            RegisteredDelivery registeredDelivery, byte replaceIfPresentFlag, DataCoding dataCoding,
            byte defaultMessageId, final byte[] shortMessageBytes, int messageSegmentNumber,
            int totalNumberOfMessageSegments, OptionalParameter... optionalParameters) {
        return new SmsShortMessage(messageId, serviceType, sourceAddressTypeofNumber, 
                sourceAddressNumberingPlanIndicator, sourceAddress, 
                destinationAddressTypeOfNumber, destinationAddressNumberingPlanIndicator, 
                destinationAddress, esmClass, protocolId, priorityFlag, scheduleDeliveryTime, 
                validityPeriod, registeredDelivery, replaceIfPresentFlag, dataCoding, 
                defaultMessageId, shortMessageBytes, messageSegmentNumber, 
                totalNumberOfMessageSegments, optionalParameters);
    }

    /**
     * @return the messageId
     */
    public Long getMessageId() {
        return messageId;
    }

    /**
     * @return the serviceType
     */
    public String getServiceType() {
        return serviceType;
    }

    /**
     * @return the sourceAddressTypeofNumber
     */
    public TypeOfNumber getSourceAddressTypeofNumber() {
        return sourceAddressTypeofNumber;
    }

    /**
     * @return the sourceAddressNumberingPlanIndicator
     */
    public NumberingPlanIndicator getSourceAddressNumberingPlanIndicator() {
        return sourceAddressNumberingPlanIndicator;
    }

    /**
     * @return the sourceAddress
     */
    public String getSourceAddress() {
        return sourceAddress;
    }

    /**
     * @return the destinationAddressTypeOfNumber
     */
    public TypeOfNumber getDestinationAddressTypeOfNumber() {
        return destinationAddressTypeOfNumber;
    }

    /**
     * @return the destinationAddressNumberingPlanIndicator
     */
    public NumberingPlanIndicator getDestinationAddressNumberingPlanIndicator() {
        return destinationAddressNumberingPlanIndicator;
    }

    /**
     * @return the destinationAddress
     */
    public String getDestinationAddress() {
        return destinationAddress;
    }

    /**
     * @return the esmClass
     */
    public ESMClass getEsmClass() {
        return esmClass;
    }

    /**
     * @return the protocolId
     */
    public byte getProtocolId() {
        return protocolId;
    }

    /**
     * @return the priorityFlag
     */
    public byte getPriorityFlag() {
        return priorityFlag;
    }

    /**
     * @return the scheduleDeliveryTime
     */
    public String getScheduleDeliveryTime() {
        return scheduleDeliveryTime;
    }

    /**
     * @return the validityPeriod
     */
    public String getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * @return the registeredDelivery
     */
    public RegisteredDelivery getRegisteredDelivery() {
        return registeredDelivery;
    }

    /**
     * @return the replaceIfPresentFlag
     */
    public byte getReplaceIfPresentFlag() {
        return replaceIfPresentFlag;
    }

    /**
     * @return the dataCoding
     */
    public DataCoding getDataCoding() {
        return dataCoding;
    }

    /**
     * @return the defaultMessageId
     */
    public byte getDefaultMessageId() {
        return defaultMessageId;
    }

    /**
     * @return the shortMessage
     */
    public String getShortMessage() {
        String shortMessage = null;
        
        if (this.shortMessageBytes != null) {
            shortMessage = new String(this.shortMessageBytes);
        }
        
        return shortMessage;
    }

    /**
     * @return the shortMessageBytes
     */
    public byte[] getShortMessageBytes() {
        return shortMessageBytes;
    }

    /**
     * @return the optionalParameters
     */
    public OptionalParameter[] getOptionalParameters() {
        return optionalParameters;
    }

    /**
     * @return the messageSegmentNumber
     */
    public int getMessageSegmentNumber() {
        return messageSegmentNumber;
    }

    /**
     * @return the totalNumberOfMessageSegments
     */
    public int getTotalNumberOfMessageSegments() {
        return totalNumberOfMessageSegments;
    }
    
}
