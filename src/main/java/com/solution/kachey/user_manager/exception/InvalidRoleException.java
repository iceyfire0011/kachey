package com.solution.kachey.user_manager.exception;

public class InvalidRoleException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7853425261528037701L;

	public InvalidRoleException(String message) {
        super(message);
    }
}
