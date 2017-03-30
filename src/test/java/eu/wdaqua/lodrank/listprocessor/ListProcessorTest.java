/**
 *
 */
package eu.wdaqua.lodrank.listprocessor;

import static org.junit.Assert.assertTrue;

import org.apache.jena.riot.Lang;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.loader.ListLoader;

/**
 * @author José M. Giménez-García
 *
 */
public class ListProcessorTest {

	ListProcessor	listProcessor;
	ClassLoader		classLoader;

	/**
	 */
	@Before
	public void setUp() {
		this.listProcessor = new ListProcessor();
		this.classLoader = getClass().getClassLoader();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#ListProcessor()}.
	 */
	@Test
	public void testListProcessor() {
		assertTrue(this.listProcessor.logger instanceof Logger);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#setLang(org.apache.jena.riot.Lang)}.
	 */
	@Test
	public void testSetLang() {
		this.listProcessor.setLang(Lang.NTRIPLES);
		assertTrue(this.listProcessor.lang.equals(Lang.NTRIPLES));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#setForceLang(boolean)}.
	 */
	@Test
	public void testSetForceLang() {
		this.listProcessor.setForceLang(true);
		assertTrue(this.listProcessor.forceLang == true);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.listprocessor.ListProcessor#setListLoader(eu.wdaqua.lodrank.loader.ListLoader)}.
	 */
	@Test
	public void testSetListLoader() {
		this.listProcessor.setListLoader(new ListLoader());
		assertTrue(this.listProcessor.listLoader instanceof ListLoader);
	}

}
