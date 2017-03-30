/**
 *
 */
package eu.wdaqua.lodrank.urlprocessor;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.google.common.net.InternetDomainName;

/**
 * @author José M. Giménez-García
 *
 */
public class URLtoPLDTest {

	protected static final String	URL	= "http://download.lodlaundromat.org/85d5a476b56fde200e770cefa0e5033c";

	protected URLtoPLD				urlProcessor;
	protected URL					url;

	/**
	 * @throws MalformedURLException
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws MalformedURLException {
		this.urlProcessor = new URLtoPLD();
		this.url = new URL(URL);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoPLD#getDataset()}.
	 */
	@Test
	public void testGetDataset() {
		this.urlProcessor.setURL(this.url);
		assertTrue(this.urlProcessor.getDataset().equals(InternetDomainName.from(this.url.getHost()).topPrivateDomain().toString()));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLProcessor#URLProcessor()}.
	 */
	@Test
	public void testURLProcessor() {
		assertTrue(this.urlProcessor.logger != null);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLProcessor#setURL(java.net.URL)}.
	 */
	@Test
	public void testSetURL() {
		this.urlProcessor.setURL(this.url);
		assertTrue(this.urlProcessor.url.toString().equals("http://download.lodlaundromat.org/85d5a476b56fde200e770cefa0e5033c"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLProcessor#getDataset(java.net.URL)}.
	 */
	@Test
	public void testGetDatasetURL() {
		assertTrue(this.urlProcessor.getDataset(this.url).equals(InternetDomainName.from(this.url.getHost()).topPrivateDomain().toString()));
	}

}
