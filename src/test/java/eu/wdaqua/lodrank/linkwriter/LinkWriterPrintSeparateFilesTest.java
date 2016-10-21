/**
 *
 */
package eu.wdaqua.lodrank.linkwriter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author José M. Giménez-García
 *
 */
public class LinkWriterPrintSeparateFilesTest {

	protected static final String	FOLDER	= "src/test/resources/testfolder";
	protected static final String	FILE	= "testfile";

	protected File					folder;

	protected LinkWriter			writer;
	protected ClassLoader			classLoader;

	/**
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException {
		this.folder = new File(FOLDER);
		this.classLoader = getClass().getClassLoader();

		this.writer = new LinkWriter();
		this.writer.setOutputFolder(this.folder);
		this.writer.setSeparateFiles(true);

		this.writer.open();

		Set<String> set = new HashSet<>();
		set.add("http://www.w3.org");
		set.add("http://xmlns.com");
		this.writer.addLinks("http://csarven.ca", set);
		set = new HashSet<>();
		set.add("http://xmlns.com/foaf");
		set.add("http://www.w3.org/1999/02/22-rdf-syntax-ns");
		set.add("http://www.w3.org/2000/01/rdf-schema");
		this.writer.addLinks("https://csarven.rww.io", set);
	}

	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(this.folder);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#printLinkSeparateFiles(java.lang.String, java.lang.String)}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testPrintLinkSeparateFiles() throws IOException {
		this.writer.setSeparateFiles(true);
		this.writer.open();
		this.writer.printLinkSeparateFiles("http://csarven.ca", "http://www.w3.org");
		this.writer.close();
		final Scanner scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.ca"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://www.w3.org"));
		scanner.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#printLinks(boolean)}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testPrintLinksTrueWithSeparateFiles() throws IOException {
		this.writer.printLinks(true);
		this.writer.close();
		Scanner scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.ca"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://xmlns.com"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://www.w3.org"));
		scanner.close();
		scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.rww.io"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/1999/02/22-rdf-syntax-ns"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/2000/01/rdf-schema"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://xmlns.com/foaf"));
		scanner.close();
		assertTrue(this.writer.links.size() == 0);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#printLinks(boolean)}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testPrintLinksFalseWithSeparateFiles() throws IOException {
		this.writer.printLinks(false);
		this.writer.close();
		Scanner scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.ca"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://xmlns.com"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://www.w3.org"));
		scanner.close();
		scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.rww.io"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/1999/02/22-rdf-syntax-ns"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/2000/01/rdf-schema"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://xmlns.com/foaf"));
		scanner.close();
		assertTrue(this.writer.links.size() == 2);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#printLinks()}.
	 *
	 * @throws IOException
	 */
	@Test
	public void testPrintLinksWithSeparateFiles() throws IOException {
		this.writer.printLinks();
		this.writer.close();
		Scanner scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.ca"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://xmlns.com"));
		assertTrue(scanner.nextLine().equals("http://csarven.ca,http://www.w3.org"));
		scanner.close();
		scanner = new Scanner(new File(this.folder.getPath() + FileSystems.getDefault().getSeparator() + "csarven.rww.io"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/1999/02/22-rdf-syntax-ns"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://www.w3.org/2000/01/rdf-schema"));
		assertTrue(scanner.nextLine().equals("https://csarven.rww.io,http://xmlns.com/foaf"));
		scanner.close();
		assertTrue(this.writer.links.size() == 0);
	}

}
