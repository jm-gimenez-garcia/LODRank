/**
 *
 */
package eu.wdaqua.lodrank.loader;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.source.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class ListLoaderTest {

	protected static final String	URL			= "http://data.dws.informatik.uni-mannheim.de/lodcloud/2014/ISWC-RDB/dump.nq.gz";
	protected static final String	LIST		= "list.csv";
	protected static final String	LONG_LIST	= "longlist.dat";
	protected static final String	EMPTY_FILE	= "emptyfile.csv";
	protected static final String	FILE		= "src/test/resources/triples.nt";
	protected static final String	DATASET		= "http://csarven.ca";

	ListLoader						loader;
	ClassLoader						classLoader;

	@Before
	public void setUp() {
		this.loader = new ListLoader();
		this.classLoader = getClass().getClassLoader();
		this.loader.setSeparator(",");
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.ListLoader#next()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testNext() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(LIST).toURI())));
		this.loader.open();
		final Entry<String, String> entry = this.loader.next();
		assertTrue(entry.getKey().equals(FILE));
		assertTrue(entry.getValue().equals(DATASET));
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.ListLoader#next()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testNextSkippingLines() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(LONG_LIST).toURI())));
		this.loader.setStart(3);
		this.loader.setStep(2);
		this.loader.setSeparator(" ");
		this.loader.open();
		final Entry<String, String> entry = this.loader.next();
		assertTrue(entry.getKey().equals("http://download.lodlaundromat.org/4b3f9dd9aee2ad2cba453e23a8f4ae39"));
		assertTrue(entry.getValue().equals("http://lodlaundromat.org/resource/4b3f9dd9aee2ad2cba453e23a8f4ae39"));
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#attachSource(Source)}.
	 */
	@Test
	public void testAttachSource() {
		final Source source = new Source(new File(LIST));
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
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(LIST).toURI())));
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
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(LIST).toURI())));
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
