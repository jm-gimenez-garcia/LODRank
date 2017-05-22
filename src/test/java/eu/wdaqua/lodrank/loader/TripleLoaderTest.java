/**
 *
 */
package eu.wdaqua.lodrank.loader;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.source.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class TripleLoaderTest {

	protected static final String	URL		= "http://data.dws.informatik.uni-mannheim.de/lodcloud/2014/ISWC-RDB/dump.nq.gz";
	protected static final String	FILE	= "triples.nt";

	TripleLoader					loader;
	ClassLoader						classLoader;

	@Before
	public void setUp() {
		this.loader = new TripleLoader(Lang.NTRIPLES);
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#open()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testOpenFile() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(FILE).toURI())));
		this.loader.open();
		assertTrue(this.loader.rdfIterator instanceof PipedRDFIterator<?>);
		assertTrue(this.loader.rdfStream instanceof PipedRDFStream<?>);
		assertTrue(this.loader.executor instanceof ExecutorService);
		assertTrue(this.loader.inputStream instanceof InputStream);
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
		assertTrue(this.loader.rdfIterator instanceof PipedRDFIterator<?>);
		assertTrue(this.loader.rdfStream instanceof PipedRDFStream<?>);
		assertTrue(this.loader.executor instanceof ExecutorService);
		assertTrue(this.loader.inputStream instanceof InputStream);
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.RDFLoader#next()}.
	 *
	 * @throws URISyntaxException
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testNext() throws URISyntaxException, SourceNotOpenableException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(FILE).toURI())));
		this.loader.open();
		final Triple triple = this.loader.next();
		assertTrue(triple.getSubject().toString().equals("http://csarven.ca/#cert"));
		assertTrue(triple.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		assertTrue(triple.getObject().toString().equals("http://www.w3.org/ns/auth/cert#RSAPublicKey"));
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.RDFLoader#getRDFLoader(org.apache.jena.riot.Lang)}.
	 */
	@Test
	public void testGetRDFLoader() {
		final RDFLoader<?> loader = RDFLoader.getRDFLoader(Lang.NTRIPLES);
		assertTrue(loader instanceof TripleLoader);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.RDFLoader#RDFLoader(org.apache.jena.riot.Lang)}.
	 */
	@Test
	public void testTripleLoader() {
		assertTrue(this.loader.lang.equals(Lang.NTRIPLES));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#attachSource(Source)}.
	 */
	@Test
	public void testAttachSource() {
		final Source source = new Source(new File("triples.nt"));
		this.loader.attachSource(source);
		assertTrue(this.loader.source.equals(source));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.Loader#hasNext()}.
	 *
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testHasNext() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(FILE).toURI())));
		this.loader.open();
		assertTrue(this.loader.hasNext() == true);
		this.loader.close();
	}

}
