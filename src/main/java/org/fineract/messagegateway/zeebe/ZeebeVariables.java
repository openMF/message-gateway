package org.fineract.messagegateway.zeebe;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ZeebeVariables {

    private ZeebeVariables() {
    }

    public static final String TRANSACTION_ID = "transactionId";
    public static final String MESSAGE_DELIVERY_STATUS = "isMessageDelivered";
    public static final String DELIVERY_STATUS_CODE = "deliveryStatus";
    public static final String CALLBACK_MESSAGE = "notification-request";
    public static final String MESSAGE_TO_SEND = "deliveryMessage";
    public static final String MESSAGE_INTERNAL_ID = "internalId";
    public static final String MESSAGE_EXTERNAL_ID = "externalId";
    public static final String BRIDGE_ID = "bridgeId";
    public static final String TENANT_ID = "tenantId";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String CALLBACK_RETRY_COUNT = "callbackRetry";
    public static final String ORIGIN_DATE = "originDate";
    public static final String ACCOUNT = "accountId";
    public static final String AMOUNT = "amount";
    public static final String DELIVERY_ERROR_MESSAGE = "deliveryErrorMessage";
    public static final String SERVER_TRANSACTION_RECEIPT_NUMBER = "mpesaReceiptNumber";
}