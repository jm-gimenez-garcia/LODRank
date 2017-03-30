package eu.wdaqua.lodrank.exception;

public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 152763249136855620L;

	public NotImplementedException(final String msg, final Throwable e) {
		super(msg, e);
	}

	public NotImplementedException(final String msg) {
		super(msg);
	}

}
