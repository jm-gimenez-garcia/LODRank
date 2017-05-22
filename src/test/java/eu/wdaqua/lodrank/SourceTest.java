/**
 *
 */
package eu.wdaqua.lodrank;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import eu.wdaqua.lodrank.source.Source;
import org.junit.Test;

import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class SourceTest {

	Source source;

	/**
	 * Test method for {@link Source#getSource(java.lang.String)}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetSourceURLhttp() throws SourceNotOpenableException {
		this.source = Source.getSource("http://test.eu/sampleurl.csv");
		assertTrue(this.source.isURL());
	}

	/**
	 * Test method for {@link Source#getSource(java.lang.String)}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetSourceURLhttps() throws SourceNotOpenableException {
		this.source = Source.getSource("https://test.eu/sampleurl.csv");
		assertTrue(this.source.isURL());
	}

	/**
	 * Test method for {@link Source#getSource(java.lang.String)}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetSourceFile() throws SourceNotOpenableException {
		this.source = Source.getSource("testfile.csv");
		assertTrue(this.source.isFile());
	}

	/**
	 * Test method for {@link Source#Source(java.io.File)}.
	 */
	@Test
	public void testSourceFile() {
		this.source = new Source(new File("testfile.csv"));
		assertTrue(this.source.isFile());
	}

	/**
	 * Test method for {@link Source#Source(java.net.URL)}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testSourceURL() throws MalformedURLException {
		this.source = new Source(new URL("https://test.eu/sampleurl.csv"));
		assertTrue(this.source.isURL());
	}

	/**
	 * Test method for {@link Source#Source(java.net.URL)}.
	 *
	 * @throws MalformedURLException
	 */
	@Test(expected = MalformedURLException.class)
	public void testSourceURLThrowsException() throws MalformedURLException {
		this.source = new Source(new URL("thisisnotaURL"));
	}

	/**
	 * Test method for {@link Source#Source(java.io.InputStream)}.
	 */
	@Test
	public void testSourceInputStream() {
		this.source = new Source(System.in);
		assertTrue(this.source.isInputStream());
	}

	/**
	 * Test method for {@link Source#isFile()}.
	 */
	@Test
	public void testIsFile() {
		this.source = new Source(new File("testfile.csv"));
		assertTrue(this.source.isFile());
	}

	/**
	 * Test method for {@link Source#isURL()}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testIsURL() throws MalformedURLException {
		this.source = new Source(new URL("https://test.eu/sampleurl.csv"));
		assertTrue(this.source.isURL());
	}

	/**
	 * Test method for {@link Source#isInputStream()}.
	 */
	@Test
	public void testIsInputStream() {
		this.source = new Source(System.in);
		assertTrue(this.source.isInputStream());
	}

	/**
	 * Test method for {@link Source#asFile()}.
	 */
	@Test
	public void testAsFile() {
		this.source = new Source(new File("testfile.csv"));
		assertTrue(this.source.asFile() instanceof File);
	}

	/**
	 * Test method for {@link Source#asFile()}.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testAsFileNoSuchElementException() {
		this.source = new Source(System.in);
		this.source.asFile();
	}

	/**
	 * Test method for {@link Source#asURL()}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testAsURL() throws MalformedURLException {
		this.source = new Source(new URL("https://test.eu/sampleurl.csv"));
		assertTrue(this.source.asURL() instanceof URL);
	}

	/**
	 * Test method for {@link Source#asURL()}.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testAsURLNoSuchElementException() {
		this.source = new Source(System.in);
		this.source.asURL();
	}

	/**
	 * Test method for {@link Source#asInputStream()}.
	 */
	@Test
	public void testAsInputStream() {
		this.source = new Source(System.in);
		assertTrue(this.source.asInputStream() instanceof InputStream);
	}

	/**
	 * Test method for {@link Source#asInputStream()}.
	 */
	@Test(expected = NoSuchElementException.class)
	public void testAsInputStreamNoSuchElementException() {
		this.source = new Source(new File("testfile.csv"));
		this.source.asInputStream();
	}

	/**
	 * Test method for {@link Source#asOptionalFile()}.
	 */
	@Test
	public void testAsOptionalFile() {
		this.source = new Source(new File("testfile.csv"));
		assertTrue(this.source.asOptionalFile() instanceof Optional<?>);
	}

	/**
	 * Test method for {@link Source#asOptionalFile()}.
	 */
	@Test
	public void testAsOptionalFileEmpty() {
		this.source = new Source(System.in);
		assertTrue(this.source.asOptionalFile().equals(Optional.empty()));
	}

	/**
	 * Test method for {@link Source#asOptionalURL()}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testAsOptionalURL() throws MalformedURLException {
		this.source = new Source(new URL("https://test.eu/sampleurl.csv"));
		assertTrue(this.source.asOptionalFile() instanceof Optional<?>);
	}

	/**
	 * Test method for {@link Source#asOptionalURL()}.
	 */
	@Test
	public void testAsOptionalURLEmpty() {
		this.source = new Source(System.in);
		assertTrue(this.source.asOptionalURL().equals(Optional.empty()));
	}

	/**
	 * Test method for {@link Source#asOptionalInputStream()}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testAsOptionalInputStream() {
		this.source = new Source(System.in);
		assertTrue(this.source.asOptionalInputStream() instanceof Optional<?>);
	}

	/**
	 * Test method for {@link Source#asOptionalInputStream()}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testAsOptionalInputStreamEmpty() {
		this.source = new Source(new File("testfile.csv"));
		assertTrue(this.source.asOptionalInputStream().equals(Optional.empty()));
	}

	/**
	 * Test method for {@link Source#getStream()}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetInputStreamFromFile() throws SourceNotOpenableException {
		this.source = new Source(getClass().getClassLoader().getResource("dictionary.csv"));
		assertTrue(this.source.getInputStream() instanceof InputStream);
		assertTrue(!(this.source.getInputStream() instanceof GZIPInputStream));
	}

	/**
	 * Test method for {@link Source#getStream()}.
	 *
	 * @throws MalformedURLException
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetInputStreamFromURL() throws MalformedURLException, SourceNotOpenableException {
		this.source = new Source(new URL("http://docs.oracle.com/javase/8/docs/api/java/io/InputStream.html"));
		assertTrue(this.source.getInputStream() instanceof InputStream);
		assertTrue(!(this.source.getInputStream() instanceof GZIPInputStream));
	}

	/**
	 * Test method for {@link Source#getStream()}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetInputStreamFromInputStream() throws SourceNotOpenableException {
		this.source = new Source(System.in);
		assertTrue(this.source.getInputStream() instanceof InputStream);
		assertTrue(!(this.source.getInputStream() instanceof GZIPInputStream));
	}

	/**
	 * Test method for {@link Source#getStream()}.
	 *
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetInputStreamFromGZippedFile() throws SourceNotOpenableException {
		this.source = new Source(getClass().getClassLoader().getResource("dictionary.csv.gz"));
		assertTrue(this.source.getInputStream() instanceof GZIPInputStream);
	}

	/**
	 * Test method for {@link Source#getStream()}.
	 *
	 * @throws MalformedURLException
	 * @throws SourceNotOpenableException
	 */
	@Test
	public void testGetInputStreamFromGZippedURL() throws MalformedURLException, SourceNotOpenableException {
		this.source = new Source(new URL("http://data.dws.informatik.uni-mannheim.de/lodcloud/2014/ISWC-RDB/dump.nq.gz"));
		assertTrue(this.source.getInputStream() instanceof GZIPInputStream);
	}

}
