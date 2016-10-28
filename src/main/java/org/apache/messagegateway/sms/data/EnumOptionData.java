package org.apache.messagegateway.sms.data;

public class EnumOptionData {

	private final Long id;

	private final String code;

	private final String value;

	public EnumOptionData(final Long id, final String code, final String value) {
		this.id = id;
		this.code = code;
		this.value = value;
	}

	public Long getId() {
		return this.id;
	}

	public String getCode() {
		return this.code;
	}

	public String getValue() {
		return this.value;
	}
}
