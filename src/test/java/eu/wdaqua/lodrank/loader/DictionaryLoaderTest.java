/**
 *
 */
package eu.wdaqua.lodrank.loader;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.source.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class DictionaryLoaderTest {

	protected static final String	URL			= "http://data.dws.informatik.uni-mannheim.de/lodcloud/2014/ISWC-RDB/dump.nq.gz";
	protected static final String	DICTIONARY	= "dictionary.csv";
	protected static final String	EMPTY_FILE	= "emptyfile.csv";
	protected static final String	PATTERN		= "^http://www.rdfabout.com/rdf/usgov/geo/";
	protected static final String	DATASET		= "2000-us-census-rdf";

	protected DictionaryLoader		loader;
	protected ClassLoader			classLoader;

	@Before
	public void setUp() {
		this.loader = new DictionaryLoader();
		this.classLoader = getClass().getClassLoader();
		this.loader.setSeparator(",");
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.DictionaryLoader#next()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testNext() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(DICTIONARY).toURI())));
		this.loader.open();
		final SimpleEntry<Pattern, String> entry = this.loader.next();
		assertTrue(entry.getKey().pattern().equals(PATTERN));
		assertTrue(entry.getValue().equals(DATASET));
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#attachSource(Source)}.
	 */
	@Test
	public void testAttachSource() {
		final Source source = new Source(new File(DICTIONARY));
		this.loader.attachSource(source);
		assertTrue(this.loader.source.equals(source));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#open()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testOpenFile() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(DICTIONARY).toURI())));
		this.loader.open();
		assertTrue(this.loader.scanner != null);
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#open()}.
	 *
	 * @throws MalformedURLException
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testOpenURL() throws MalformedURLException, SourceNotOpenableException {
		this.loader.attachSource(new Source(new URL(URL)));
		this.loader.open();
		assertTrue(this.loader.scanner != null);
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#open()}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testOpenInputStream() throws SourceNotOpenableException {
		this.loader.attachSource(new Source(System.in));
		this.loader.open();
		assertTrue(this.loader.scanner != null);
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#hasNext()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testHasNextTrue() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(DICTIONARY).toURI())));
		this.loader.open();
		assertTrue(this.loader.hasNext() == true);
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#hasNext()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testHasNextFalse() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(EMPTY_FILE).toURI())));
		this.loader.open();
		assertTrue(this.loader.hasNext() == false);
		this.loader.close();
	}
}
