package org.fineract.messagegateway.camel.config;

/**
 * Central Definition of all the Camel Exchange Properties
 */
public class CamelProperties {

	private CamelProperties() {}

    public static final String CORRELATION_ID = "correlationId";
    public static final String TRANSACTION_BODY = "transactionBody";
    public static final String TRANSACTION_TYPE = "transactionType";
    public static final String DELIVERY_ERROR_INFORMATION = "deliveryErrorInformation";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String INTERNAL_ID = "internalId";
    public static final String MOBILE_NUMBER = "mobileNumber";
    public static final String PROVIDER_ID = "providerId";
    public static final String DELIVERY_STATUS = "deliveryStatus";
    public static final String DELIVERY_MESSAGE = "deliveryMessage";
    public static final String DATE = "originDate";
    public static final String ACCOUNT_ID = "accountId";
    public static final String TRANSACTION_AMOUNT = "amount";
    public static final String RETRY_COUNT_CALLBACK = "callbackRetry";
    public static final String TIMER = "timer";
    public static final String RETRIES = "retries";

}
