package eu.wdaqua.lodrank.exception;

import java.io.IOException;

public class SourceNotOpenableException extends IOException {

	private static final long serialVersionUID = -5299624856495903009L;

	public SourceNotOpenableException() {
		super();
	}

	public SourceNotOpenableException(final String msg) {
		super(msg);
	}

	public SourceNotOpenableException(final String msg, final Throwable e) {
		super(msg, e);
	}

}
