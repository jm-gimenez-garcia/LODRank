/**
 *
 */
package eu.wdaqua.lodrank.urlprocessor;

import com.google.common.net.InternetDomainName;

/**
 * @author chemi2g
 *
 */
public class URLtoPLD extends URLProcessor {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.wdaqua.lodrank.URLtoDataset#getDataset()
	 */
	@Override
	public String getDataset() {
		try {
			return this.url != null ? InternetDomainName.from(this.url.getHost()).topPrivateDomain().toString() : null;
		} catch (final IllegalArgumentException | IllegalStateException e) {
			this.logger.warn("Could not extract link because " + this.url.toString() + " has not a valid domain name.");
			return null;
		}
	}

}
