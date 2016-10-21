package eu.wdaqua.lodrank.exception;

import java.io.IOException;

public class DestinationNotOpenableException extends IOException {

	private static final long serialVersionUID = 4985956284161545399L;

	public DestinationNotOpenableException(final String msg, final Throwable e) {
		super(msg, e);
	}

	public DestinationNotOpenableException(final String msg) {
		super(msg);
	}

}
