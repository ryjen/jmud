/**
 * 
 */
package net.arg3.jmud.exceptions;

/**
 * @author Ryan
 * 
 */
public class FlagNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	public FlagNotFoundException() {
		super();
	}

	public FlagNotFoundException(String msg) {
		super(msg);
	}

	public FlagNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public FlagNotFoundException(Throwable cause) {
		super(cause);
	}
}
