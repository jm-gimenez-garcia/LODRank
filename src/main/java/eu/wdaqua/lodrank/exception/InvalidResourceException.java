package eu.wdaqua.lodrank.exception;

public class InvalidResourceException extends Exception {

	private static final long serialVersionUID = -8647238352718473942L;

	public InvalidResourceException(final String msg, final Throwable e) {
		super(msg, e);
	}

}
