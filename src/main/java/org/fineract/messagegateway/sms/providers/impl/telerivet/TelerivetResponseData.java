package org.fineract.messagegateway.sms.providers.impl.telerivet;

public class TelerivetResponseData {
    String event;
    String secret;
    String id;
    String phone_id;
    String  contact_id;
    String direction;
    String status;
    String message_type;
    String source;
    int time_created;
    int time_sent;
    int time_updated;
    String from_number;
    String to_number;
    String content;
    int starred;
    int simulated;
    int track_clicks;
    String user_id;
    String project_id;
    int priority;
    String error_message;
    String contact;

    public String getEvent() {
        return event;
    }
    public String getSecret() {
        return secret;
    }
    public String getMessageId() {
        return id;
    }
    public String getPhoneId() {
        return phone_id;
    }
    public String getContactId() {
        return contact_id;
    }
    public String getDirection() {
        return direction;
    }
    public String getMessageStatus() {
        return status;
    }
    public String getMessageType() {
        return message_type;
    }
    public String getSource() {
        return source;
    }
    public int getTimeCreated() {
        return time_created;
    }
    public int getTimeSent() {
        return time_sent;
    }
    public int getTimeUpdated() {
        return time_updated;
    }
    public String getFromNumber() {
        return from_number;
    }
    public String getToNumber() {
        return to_number;
    }
    public String getContent() {
        return content;
    }
    public String getErrorMessage() {
        return error_message;
    }







}
