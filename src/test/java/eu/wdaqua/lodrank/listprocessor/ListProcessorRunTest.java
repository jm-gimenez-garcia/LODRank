/****/package eu.wdaqua.lodrank.listprocessor;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.Source;
import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.loader.ListLoader;
import eu.wdaqua.lodrank.rdfprocessor.RDFProcessor;
import eu.wdaqua.lodrank.urlprocessor.URLProcessor;
import eu.wdaqua.lodrank.urlprocessor.URLtoPLD;

/***

@author José M. Giménez-García
 *
 */
 public class ListProcessorRunTest {

 protected static final String FOLDER = "src/test/resources/testfolder";
 protected static final String OUTPUT_FILE = "testfile";
 protected static final String FILE_TRIPLES = "list_triples.csv";
 protected static final String FILE_QUADS = "list_quads.csv";
 protected static final String FILE_MIXED = "list.csv";

 File folder;
 File file;

 ListProcessor listProcessor;
 ClassLoader classLoader;

 /**
 * @throws DestinationNotOpenableException
 */
 @Before
 public void setUp() throws DestinationNotOpenableException {
 this.folder = new File(FOLDER);
 this.file = new File(FOLDER + FileSystems.getDefault().getSeparator() + OUTPUT_FILE);
 this.listProcessor = new ListProcessor();
 this.classLoader = getClass().getClassLoader();

 final URLProcessor urlProcessor = new URLtoPLD();
 final LinkExtractor linkExtractor = new LinkExtractor();
 linkExtractor.setUrlProcessor(urlProcessor);
 final LinkWriter linkWriter = new LinkWriter();
 linkWriter.setDuplicates(false);
 linkWriter.setOutputFolder(this.folder);
 linkWriter.setFileName(OUTPUT_FILE);
 final RDFProcessor rdfProcessor = new RDFProcessor();
 rdfProcessor.setLinkExtractor(linkExtractor);
 rdfProcessor.setLinkWriter(linkWriter);
 this.listProcessor = new ListProcessor();
 this.listProcessor.setRDFProcessor(rdfProcessor);
 }

 /**
 * @throws java.lang.Exception
 */
 @After
 public void tearDown() throws Exception {
 FileUtils.deleteDirectory(this.folder);
 }

 /**
 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#run()}.
 *
 * @throws SourceNotOpenableException
 * @throws FileNotFoundException
 */
 @Test
 public void testRunTriples() throws SourceNotOpenableException, FileNotFoundException {
 final ListLoader listLoader = new ListLoader();
 listLoader.attachSource(new Source(this.classLoader.getResourceAsStream(FILE_TRIPLES)));
 listLoader.setSeparator(",");
 this.listProcessor.setListLoader(listLoader);
 this.listProcessor.run();

 final Scanner scanner = new Scanner(this.file);
 assertTrue(scanner.nextLine().equals("csarven.ca,w3.org"));
 assertTrue(scanner.nextLine().equals("csarven.ca,xmlns.com"));
 assertTrue(scanner.nextLine().equals("csarven.ca,rww.io"));
 scanner.close();
 }

 /**
 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#run()}.
 *
 * @throws SourceNotOpenableException
 * @throws FileNotFoundException
 */
 @Test
 public void testRunQuads() throws SourceNotOpenableException, FileNotFoundException {
 final ListLoader listLoader = new ListLoader();
 listLoader.attachSource(new Source(this.classLoader.getResourceAsStream(FILE_QUADS)));
 listLoader.setSeparator(",");
 this.listProcessor.setListLoader(listLoader);
 this.listProcessor.run();

 final Scanner scanner = new Scanner(this.file);
 assertTrue(scanner.nextLine().equals("csarven.ca,w3.org"));
 assertTrue(scanner.nextLine().equals("rww.io,csarven.ca"));
 assertTrue(scanner.nextLine().equals("rww.io,w3.org"));
 assertTrue(scanner.nextLine().equals("rww.io,xmlns.com"));
 scanner.close();
 }

 /**
 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#run()}.
 *
 * @throws FileNotFoundException
 * @throws SourceNotOpenableException
 */
 @Test
 public void testRun() throws FileNotFoundException, SourceNotOpenableException {
 final ListLoader listLoader = new ListLoader();
 listLoader.attachSource(new Source(this.classLoader.getResourceAsStream(FILE_MIXED)));
 listLoader.setSeparator(",");
 this.listProcessor.setListLoader(listLoader);
 this.listProcessor.run();

 final Scanner scanner = new Scanner(this.file);
 assertTrue(scanner.nextLine().equals("csarven.ca,w3.org"));
 assertTrue(scanner.nextLine().equals("csarven.ca,xmlns.com"));
 assertTrue(scanner.nextLine().equals("csarven.ca,rww.io"));
 scanner.close();
 }

 }
