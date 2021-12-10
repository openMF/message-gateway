package org.fineract.messagegateway.sms.providers.impl.telerivet;

public class TelerivetResponseData {
    private String event;
    private String secret;
    private String id;
    private String phone_id;
    private String  contact_id;
    private String direction;
    private  String status;
    private String message_type;
    private  String source;
    private int time_created;
    private int time_sent;
    private int time_updated;
    private String from_number;
    private String to_number;
    private String content;
    private String error_message;

    public void setEvent(String event) {
        this.event = event;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhone_id(String phone_id) {
        this.phone_id = phone_id;
    }

    public void setContact_id(String contact_id) {
        this.contact_id = contact_id;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTime_created(int time_created) {
        this.time_created = time_created;
    }

    public void setTime_sent(int time_sent) {
        this.time_sent = time_sent;
    }

    public void setTime_updated(int time_updated) {
        this.time_updated = time_updated;
    }

    public void setFrom_number(String from_number) {
        this.from_number = from_number;
    }

    public void setTo_number(String to_number) {
        this.to_number = to_number;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getEvent() {
        return event;
    }

    public String getSecret() {
        return secret;
    }

    public String getId() {
        return id;
    }

    public String getPhone_id() {
        return phone_id;
    }

    public String getContact_id() {
        return contact_id;
    }

    public String getDirection() {
        return direction;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getSource() {
        return source;
    }

    public int getTime_created() {
        return time_created;
    }

    public int getTime_sent() {
        return time_sent;
    }

    public int getTime_updated() {
        return time_updated;
    }

    public String getFrom_number() {
        return from_number;
    }

    public String getTo_number() {
        return to_number;
    }

    public String getContent() {
        return content;
    }

    public String getError_message() {
        return error_message;
    }
}
