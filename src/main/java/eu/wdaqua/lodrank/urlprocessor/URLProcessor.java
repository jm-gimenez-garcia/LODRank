package eu.wdaqua.lodrank.urlprocessor;

import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class URLProcessor {

	protected URL		url;
	protected Logger	logger;

	public URLProcessor() {
		super();
		this.logger = LogManager.getLogger(getClass());
	}

	public void setURL(final URL input) {
		this.url = input;
	}

	public String getDataset(final URL input) {
		setURL(input);
		return getDataset();
	}

	public abstract String getDataset();

}
