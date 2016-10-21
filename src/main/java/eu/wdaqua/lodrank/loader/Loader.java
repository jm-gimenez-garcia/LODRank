package eu.wdaqua.lodrank.loader;

import java.util.Iterator;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

public abstract class Loader<I> implements Iterator<I>, AutoCloseable {

	protected Source	source;
	protected Scanner	scanner;

	protected Logger	logger;

	public Loader() {
		this.logger = LogManager.getLogger(getClass());
	}

	public void attachSource(final Source source) {
		this.logger.debug("Attaching source " + source.toString() + " to " + getClass().getName());
		this.source = source;
	}

	public void open() throws SourceNotOpenableException {
		this.scanner = new Scanner(this.source.getInputStream());
	}

	@Override
	public boolean hasNext() {
		return this.scanner.hasNext();
	}

	@Override
	public abstract I next();

	@Override
	public void close() {
		this.scanner.close();
	}

}
