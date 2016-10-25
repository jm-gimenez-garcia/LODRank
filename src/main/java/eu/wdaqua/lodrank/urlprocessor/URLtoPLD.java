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
		} catch (final IllegalStateException e) {
			this.logger.debug(this.url.getHost() + " is not under public suffix. Trying to get public suffix intead.");
			return InternetDomainName.from(this.url.getHost()).publicSuffix() != null ? InternetDomainName.from(this.url.getHost()).publicSuffix().toString() : null;
		} catch (final IllegalArgumentException e) {
			this.logger.warn("Could not extract link because " + this.url.toString() + " has not a valid domain name.");
			return null;
		}
	}

}
