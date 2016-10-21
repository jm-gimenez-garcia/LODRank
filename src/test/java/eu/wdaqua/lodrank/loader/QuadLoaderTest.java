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

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.Source;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class QuadLoaderTest {

	protected static final String	URL		= "http://download.lodlaundromat.org/85d5a476b56fde200e770cefa0e5033c";
	protected static final String	FILE	= "quads.nq";

	QuadLoader						loader;
	ClassLoader						classLoader;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.loader = new QuadLoader(Lang.NQUADS);
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.QuadLoader#QuadLoader(org.apache.jena.riot.Lang)}.
	 */
	@Test
	public void testQuadLoader() {
		assertTrue(this.loader.lang.equals(Lang.NQUADS));
	}

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
	 * @throws SourceNotOpenableException
	 * @throws URISyntaxException
	 */
	@Test
	public void testNext() throws SourceNotOpenableException, URISyntaxException {
		this.loader.attachSource(new Source(new File(this.classLoader.getResource(FILE).toURI())));
		this.loader.open();
		final Quad quad = this.loader.next();
		assertTrue(quad.getSubject().toString().equals("http://csarven.ca/#cert"));
		assertTrue(quad.getPredicate().toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		assertTrue(quad.getObject().toString().equals("http://www.w3.org/ns/auth/cert#RSAPublicKey"));
		assertTrue(quad.getGraph().toString().equals("http://csarven.ca"));
		this.loader.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.loader.RDFLoader#getRDFLoader(org.apache.jena.riot.Lang)}.
	 */
	@Test
	public void testGetRDFLoader() {
		final RDFLoader<?> loader = RDFLoader.getRDFLoader(Lang.NQUADS);
		assertTrue(loader instanceof QuadLoader);
	}

}
