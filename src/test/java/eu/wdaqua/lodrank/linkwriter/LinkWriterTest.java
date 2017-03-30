/**
 *
 */
package eu.wdaqua.lodrank.linkwriter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class LinkWriterTest {

	protected static final String	FOLDER	= "src/test/resources/testfolder";
	protected static final String	FILE	= "testfile";

	File							folder;

	LinkWriter						writer;
	ClassLoader						classLoader;

	/**
	 */
	@Before
	public void setUp() {
		this.folder = new File(FOLDER);
		this.writer = new LinkWriter();
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		FileUtils.deleteDirectory(this.folder);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#LinkWriter()}.
	 */
	@Test
	public void testLinkWriter() {
		assertTrue(this.writer.logger instanceof Logger);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#setSeparateFiles(boolean)}.
	 */
	@Test
	public void testSetSeparateFiles() {
		this.writer.setSeparateFiles(true);
		assertTrue(this.writer.separateFiles == true);
		this.writer.setSeparateFiles(false);
		assertTrue(this.writer.separateFiles == false);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#setOutputFolder(java.io.File)}.
	 *
	 * @throws DestinationNotOpenableException
	 */
	@Test
	public void testSetOutputFolder() throws DestinationNotOpenableException {
		this.writer.setOutputFolder(this.folder);
		assertTrue(this.writer.outputFolder.isDirectory());
		this.writer.setOutputFolder(this.folder);
		assertTrue(this.writer.outputFolder.isDirectory());
		this.folder.delete();
		this.writer.setOutputFolder(this.folder);
		assertTrue(this.writer.outputFolder.isDirectory());
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#setFileName(java.lang.String)}.
	 */
	@Test
	public void testSetFileName() {
		this.writer.setFileName(FILE);
		assertTrue(this.writer.fileName.equals(FILE));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#open()}.
	 *
	 * @throws DestinationNotOpenableException
	 */
	@Test
	public void testOpenSingleFile() throws DestinationNotOpenableException {
		this.writer.setOutputFolder(this.folder);
		this.writer.setFileName(FILE);
		this.writer.setSeparateFiles(false);
		this.writer.open();
		assertTrue(this.writer.writer instanceof PrintWriter);
		this.writer.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#open()}.
	 *
	 * @throws DestinationNotOpenableException
	 */
	@Test
	public void testOpenSeparateFiles() throws DestinationNotOpenableException {
		this.writer.setOutputFolder(this.folder);
		this.writer.setSeparateFiles(true);
		this.writer.open();
		assertTrue(this.writer.writers instanceof HashMap<?, ?>);
		this.writer.close();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkwriter.LinkWriter#addLinks(java.util.Map.Entry)}.
	 */
	@Test
	public void testSetLinks() {
		final Set<String> set = new HashSet<>();
		set.add("http://www.w3.org");
		set.add("http://xmlns.com");
		this.writer.addLinks("http://csarven.ca", set);
		assertTrue(this.writer.links.get("http://csarven.ca").size() == 2);
		assertTrue(this.writer.links.get("http://csarven.ca").containsKey("http://www.w3.org"));
		assertTrue(this.writer.links.get("http://csarven.ca").containsKey("http://xmlns.com"));
	}
}
