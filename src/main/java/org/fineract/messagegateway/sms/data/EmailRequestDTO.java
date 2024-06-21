package org.fineract.messagegateway.sms.data;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
public class EmailRequestDTO {
        @NotEmpty
        private List< String> to;

        @NotEmpty
        private String subject;

        @NotEmpty
        private String body;

        // Getters and setters

        public List<String> getTo() {
            return to;
        }

        public void setTo(List<String> to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
}
