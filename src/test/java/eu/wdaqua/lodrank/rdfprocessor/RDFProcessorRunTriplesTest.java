/**
 *
 */
package eu.wdaqua.lodrank.rdfprocessor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.riot.Lang;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.Source;
import eu.wdaqua.lodrank.Source.Format;
import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.loader.RDFLoader;
import eu.wdaqua.lodrank.urlprocessor.URLtoPLD;

/**
 * @author José M. Giménez-García
 *
 */
public class RDFProcessorRunTriplesTest {

	protected static final String	FILE_NAME	= "triples.nt";
	protected static final String	DATASET		= "http://csarven.ca";
	protected static final String	FOLDER		= "src/test/resources/testfolder";
	protected static final String	FILE		= "testfile";

	protected URL					dataset;

	protected RDFProcessor			rdfProcessor;
	protected ClassLoader			classLoader;

	@Before
	public void setUp() throws MalformedURLException, DestinationNotOpenableException {
		this.dataset = new URL(DATASET);
		this.rdfProcessor = new RDFProcessor();
		this.classLoader = getClass().getClassLoader();

		final RDFLoader<?> rdfLoader = RDFLoader.getRDFLoader(Lang.NTRIPLES);
		rdfLoader.attachSource(new Source(this.classLoader.getResourceAsStream(FILE_NAME)));
		this.rdfProcessor.setRDFLoader(rdfLoader);

		final LinkExtractor linkExtractor = new LinkExtractor();
		linkExtractor.setUrlProcessor(new URLtoPLD());
		linkExtractor.setDataset(this.dataset);
		this.rdfProcessor.setLinkExtractor(linkExtractor);

		final LinkWriter linkWriter = new LinkWriter();
		linkWriter.setOutputFolder(new File(FOLDER));
		linkWriter.setFileName(FILE);
		linkWriter.setSeparateFiles(false);
		this.rdfProcessor.setLinkWriter(linkWriter);
		this.rdfProcessor.setDataset(this.dataset);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#runTriples()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testRunTriples() throws IOException {
		this.rdfProcessor.linkWriter.open();
		this.rdfProcessor.runTriples();
		this.rdfProcessor.linkWriter.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#run(eu.wdaqua.lodrank.Source.Format)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunFormatTriples() throws IOException {
		this.rdfProcessor.run(Format.TRIPLES);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#run(org.apache.jena.riot.Lang)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunLangNTriples() throws IOException {
		this.rdfProcessor.run(Lang.NTRIPLES);
	}

}
