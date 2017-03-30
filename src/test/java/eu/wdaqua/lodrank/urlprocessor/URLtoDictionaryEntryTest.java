/**
 *
 */
package eu.wdaqua.lodrank.urlprocessor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.loader.DictionaryLoader;

/**
 * @author José M. Giménez-García
 *
 */
public class URLtoDictionaryEntryTest {

	protected static final String	URL				= "http://www.rdfabout.com/rdf/usgov/geo/";
	protected static final String	DATASET			= "2000-us-census-rdf";
	protected static final String	VALID_FILE		= "dictionary_with_spaces.dat";
	protected static final String	NON_VALID_FILE	= "thisisnotavalidfile.txt";

	URL								url;

	URLtoDictionaryEntry			urlProcessor;
	ClassLoader						classLoader;
	DictionaryLoader				loader;

	/**
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws URISyntaxException, MalformedURLException {
		this.url = new URL(URL);
		this.urlProcessor = new URLtoDictionaryEntry();
		this.loader = new DictionaryLoader();
		this.classLoader = getClass().getClassLoader();
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(VALID_FILE).toURI())));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry#getDataset()}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetDataset() throws SourceNotOpenableException {
		this.urlProcessor.loadDictionary(this.loader);
		this.urlProcessor.getDataset(this.url);
		assertTrue(this.urlProcessor.getDataset().equals(DATASET));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry#loadDictionary(eu.wdaqua.lodrank.loader.DictionaryLoader)}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testLoadDictionary() throws SourceNotOpenableException {
		this.urlProcessor.loadDictionary(this.loader);
		assertTrue(this.urlProcessor.dictionary.size() == 10);
		assertTrue(this.urlProcessor.getDataset(this.url).equals(DATASET));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry#loadDictionary(eu.wdaqua.lodrank.loader.DictionaryLoader)}.
	 *
	 * @throws SourceNotOpenableException
	 *
	 * @throws URISyntaxException
	 */
	@Test(expected = SourceNotOpenableException.class)
	public void testLoadDictionarySourceNotOpenableException() throws SourceNotOpenableException {
		this.loader.attachSource(new Source(new File(NON_VALID_FILE)));
		this.urlProcessor.loadDictionary(this.loader);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry#addDictionaryEntry(java.util.regex.Pattern, java.lang.String)}.
	 */
	@Test
	public void testAddDictionaryEntryPatternString() {
		this.urlProcessor.addDictionaryEntry(Pattern.compile("^" + URL), DATASET);
		assertTrue(this.urlProcessor.getDataset(this.url).equals(DATASET));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry#addDictionaryEntry(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAddDictionaryEntryStringString() {
		this.urlProcessor.addDictionaryEntry("^" + URL, DATASET);
		assertTrue(this.urlProcessor.getDataset(this.url).equals(DATASET));
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
		assertTrue(this.urlProcessor.url.toString().equals(URL));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.urlprocessor.URLProcessor#getDataset(java.net.URL)}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetDatasetURL() throws SourceNotOpenableException {
		this.urlProcessor.loadDictionary(this.loader);
		assertTrue(this.urlProcessor.getDataset(this.url).equals(DATASET));
	}

}
