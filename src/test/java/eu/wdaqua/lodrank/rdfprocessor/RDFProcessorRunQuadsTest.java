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

import eu.wdaqua.lodrank.source.Source;
import eu.wdaqua.lodrank.source.Source.Format;
import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.loader.RDFLoader;
import eu.wdaqua.lodrank.urlprocessor.URLtoPLD;

/**
 * @author José M. Giménez-García
 *
 */
public class RDFProcessorRunQuadsTest {

	protected static final String	FILE_NAME	= "quads.nq";
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

		final RDFLoader<?> rdfLoader = RDFLoader.getRDFLoader(Lang.NQUADS);
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
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#runQuads()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testRunQuads() throws IOException {
		this.rdfProcessor.linkWriter.open();
		this.rdfProcessor.runQuads();
		this.rdfProcessor.linkWriter.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#run(Source.Format)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunFormatQuads() throws IOException {
		this.rdfProcessor.run(Format.QUADS);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.rdfprocessor.RDFProcessor#run(org.apache.jena.riot.Lang)}.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testRunLangNQuads() throws IOException {
		this.rdfProcessor.run(Lang.NQUADS);
	}

}
