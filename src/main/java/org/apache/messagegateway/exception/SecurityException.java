package org.apache.messagegateway.exception;

public class SecurityException extends RuntimeException {

	private SecurityException(final String msg) {
		super(msg);
	}

	public static SecurityException tenantAlreadyExisits(final String tenant) {
		return new SecurityException("Tenant Already existing with "+tenant+" identifier");
	}
}
