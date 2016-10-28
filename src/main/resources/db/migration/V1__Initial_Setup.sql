CREATE TABLE m_sms_bridge (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tenant_id               VARCHAR(32)                                     NOT NULL,
  phone_no                VARCHAR(255)                                    NOT NULL,
  provider_key            VARCHAR(100)                                    NOT NULL,
  provider_name            VARCHAR(100)                                    NOT NULL,
  description            VARCHAR(500)                                     NOT NULL,
  created_on              TIMESTAMP                                       NULL DEFAULT NULL,
  last_modified_on        TIMESTAMP                                       NULL DEFAULT NULL
);

CREATE TABLE m_outbound_messages (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tenant_id               VARCHAR(32)                                     NOT NULL,
  external_id             VARCHAR(100)                                    NULL DEFAULT NULL,
  internal_id             VARCHAR(100)                                    NOT NULL,
  delivery_error_message  VARCHAR(500)                                    NULL DEFAULT NULL,
  source_address	      VARCHAR(100)                                    NULL DEFAULT NULL,
  provider_id             BIGINT(20)                                      NOT NULL,
  mobile_number           VARCHAR(255)                                    NOT NULL,
  submitted_on_date       TIMESTAMP                                       NOT NULL,
  delivered_on_date       TIMESTAMP                                       NOT NULL,
  delivery_status	      INT(3)										  NOT NULL,
  message		          VARCHAR(4096)                                   NOT NULL,
  CONSTRAINT `m_outbound_messages_1` FOREIGN KEY (`provider_id`) REFERENCES `m_sms_bridge` (`id`)
);

CREATE TABLE m_provider_configuration (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  provider_id             BIGINT(20)                                     NOT NULL,
  config_name             VARCHAR(100)                                    NULL DEFAULT NULL,
  config_value             VARCHAR(100)                                    NOT NULL,
  CONSTRAINT `m_provider_configuration_1` FOREIGN KEY (`provider_id`) REFERENCES `m_sms_bridge` (`id`)
);


CREATE TABLE event_sourcing (
  id               BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT ,
  tenant_id        VARCHAR(32)                                                   NOT NULL,
  entity           VARCHAR(32)                                                   NOT NULL,
  action           VARCHAR(32)                                                   NOT NULL,
  payload          VARCHAR(4096)                                                 NOT NULL,
  processed        BOOLEAN                                                       NOT NULL,
  error_message    VARCHAR(256),
  created_on       TIMESTAMP                                                     NOT NULL,
  last_modified_on TIMESTAMP                                                     NOT NULL
);

INSERT INTO `m_sms_bridge` (`tenant_id`, `phone_no`, `provider_key`, `provider_name`, `description`) 
VALUES ('default', '+12512873781', '12512873781', 'Twilio SMS Provider', 'Twilio SMS Provider'),
('default', '+12512873781', '12512873781', 'Infobip SMS Provider', 'Infobip SMS Provider') ;

INSERT INTO `m_provider_configuration` (`provider_id`, `config_name`, `config_value`) 
VALUES (1, 'Provider_Account_Id', 'ACcb95cd9fb2e7d72f85c06aea6473f41d'),
(1, 'Provider_Auth_Token', 'fed820a353def95f1e5aad077fef670e') ;

