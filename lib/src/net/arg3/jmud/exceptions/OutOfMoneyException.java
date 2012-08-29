package net.arg3.jmud.exceptions;

public class OutOfMoneyException extends Exception {

	private static final long serialVersionUID = 1L;

	public OutOfMoneyException() {
		super();
	}

	public OutOfMoneyException(String msg) {
		super(msg);
	}

	public OutOfMoneyException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public OutOfMoneyException(Throwable cause) {
		super(cause);
	}
}
