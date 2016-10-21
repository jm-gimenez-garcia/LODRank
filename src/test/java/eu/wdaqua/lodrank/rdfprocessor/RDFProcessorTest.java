/**
 *
 */
package eu.wdaqua.lodrank.rdfprocessor;

import static org.junit.Assert.assertTrue;

import org.apache.jena.riot.Lang;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.loader.RDFLoader;

/**
 * @author José M. Giménez-García
 *
 */
public class RDFProcessorTest {

	protected RDFProcessor	rdfProcessor;
	protected ClassLoader	classLoader;

	@Before
	public void setUp() {
		this.rdfProcessor = new RDFProcessor();
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#RDFProcessor()}.
	 */
	@Test
	public void testRDFProcessor() {
		assertTrue(this.rdfProcessor.logger instanceof Logger);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#setRDFLoader(eu.wdaqua.lodrank.loader.RDFLoader)}.
	 */
	@Test
	public void testSetRDFLoader() {
		final RDFLoader<?> rdfLoader = RDFLoader.getRDFLoader(Lang.NTRIPLES);
		this.rdfProcessor.setRDFLoader(rdfLoader);
		assertTrue(this.rdfProcessor.loader.equals(rdfLoader));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#setLinkExtractor(eu.wdaqua.lodrank.linkextractor.LinkExtractor)}.
	 */
	@Test
	public void testSetLinkExtractor() {
		final LinkExtractor linkExtractor = new LinkExtractor();
		this.rdfProcessor.setLinkExtractor(linkExtractor);
		assertTrue(this.rdfProcessor.linkExtractor.equals(linkExtractor));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#setLinkWriter(eu.wdaqua.lodrank.linkwriter.LinkWriter)}.
	 */
	@Test
	public void testSetLinkWriter() {
		final LinkWriter linkWriter = new LinkWriter();
		this.rdfProcessor.setLinkWriter(linkWriter);
		assertTrue(this.rdfProcessor.linkWriter.equals(linkWriter));
	}

}
