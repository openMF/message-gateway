CREATE TABLE m_tenants (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tenant_id               VARCHAR(32)                                     NOT NULL,
  tenant_app_key		  VARCHAR(100)									  NOT NULL,
  description			  VARCHAR(500)									  NULL DEFAULT NULL
);

CREATE TABLE m_sms_bridge (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tenant_id               BIGINT(20)                                      NOT NULL,
  tenant_phone_no         VARCHAR(255)                                    NOT NULL,
  provider_name           VARCHAR(100)                                    NOT NULL,
  provider_key 		  	  VARCHAR(100)									  NOT NULL,			
  description             VARCHAR(500)                                    NOT NULL,
  created_on              TIMESTAMP                                       NULL DEFAULT NULL,
  last_modified_on        TIMESTAMP                                       NULL DEFAULT NULL,
  CONSTRAINT `m_sms_bridge_1` FOREIGN KEY (`tenant_id`) REFERENCES `m_tenants` (`id`)
);

CREATE TABLE m_outbound_messages (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tenant_id               VARCHAR(32)                                     NOT NULL,
  external_id             VARCHAR(100)                                    NULL DEFAULT NULL,
  internal_id             VARCHAR(100)                                    NOT NULL,
  delivery_error_message  VARCHAR(500)                                    NULL DEFAULT NULL,
  source_address	      VARCHAR(100)                                    NULL DEFAULT NULL,
  sms_bridge_id           BIGINT(20)                                      NOT NULL,
  mobile_number           VARCHAR(255)                                    NOT NULL,
  submitted_on_date       TIMESTAMP                                       NOT NULL,
  delivered_on_date       TIMESTAMP                                       NOT NULL,
  delivery_status	      INT(3)										  NOT NULL,
  message		          VARCHAR(4096)                                   NOT NULL,
  CONSTRAINT `m_outbound_messages_1` FOREIGN KEY (`sms_bridge_id`) REFERENCES `m_sms_bridge` (`id`)
);

CREATE TABLE m_sms_bridge_configuration (
  id                      BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  sms_bridge_id           BIGINT(20)                                     NOT NULL,
  config_name             VARCHAR(100)                                    NULL DEFAULT NULL,
  config_value             VARCHAR(100)                                    NOT NULL,
  CONSTRAINT `m_provider_configuration_1` FOREIGN KEY (`sms_bridge_id`) REFERENCES `m_sms_bridge` (`id`)
);

INSERT INTO `m_sms_bridge` (`tenant_id`, `phone_no`, `provider_app_key`, `provider_key`, `provider_name`, `description`)
VALUES ('default', '+1234567890', 'DummyGeneratedKey','Dummy', 'Dummy SMS Provider - Testing', 'Dummy, just for testing');
