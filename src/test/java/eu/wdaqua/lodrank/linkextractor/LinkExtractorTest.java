/**
 *
 */
package eu.wdaqua.lodrank.linkextractor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.wdaqua.lodrank.exception.InvalidResourceException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor.Exclusion;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor.Rule;
import eu.wdaqua.lodrank.urlprocessor.URLtoPLD;

/**
 * @author José M. Giménez-García
 *
 */
public class LinkExtractorTest {

	LinkExtractor linkExtractor;

	/**
	 */
	@Before
	public void setUp() {
		this.linkExtractor = new LinkExtractor();
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#LinkExtractor()}.
	 */
	@Test
	public void testLinkExtractor() {
		assertTrue(this.linkExtractor.logger instanceof Logger);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#setExclusions(java.util.HashMap)}.
	 */
	@Test
	public void testSetActions() {
		this.linkExtractor.setExclusions(new HashMap<Role, HashSet<Exclusion>>());
		assertTrue(this.linkExtractor.listOfExclusions instanceof HashMap<?, ?>);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#addAction(eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Action)}.
	 */
	@Test
	public void testAddAction1st() {
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		assertTrue(this.linkExtractor.listOfExclusions.size() == 1);
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.SUBJECT));
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.PREDICATE));
		assertTrue(this.linkExtractor.listOfExclusions.containsKey(Role.OBJECT));
		assertTrue(this.linkExtractor.listOfExclusions.get(Role.OBJECT).size() == 1);
		this.linkExtractor.listOfExclusions.get(Role.OBJECT).forEach(exclusion -> {
			assertTrue(exclusion.getRole().equals(Role.PREDICATE));
			assertTrue(exclusion.getRule().equals(Rule.EQUAL));
			assertTrue(exclusion.value.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		});
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#addAction(eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Action)}.
	 */
	@Test
	public void testAddActionEqual() {
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		assertTrue(this.linkExtractor.listOfExclusions.size() == 1);
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.SUBJECT));
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.PREDICATE));
		assertTrue(this.linkExtractor.listOfExclusions.containsKey(Role.OBJECT));
		assertTrue(this.linkExtractor.listOfExclusions.get(Role.OBJECT).size() == 1);
		this.linkExtractor.listOfExclusions.get(Role.OBJECT).forEach(exclusion -> {
			assertTrue(exclusion.getRole().equals(Role.PREDICATE));
			assertTrue(exclusion.getRule().equals(Rule.EQUAL));
			assertTrue(exclusion.value.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		});
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#addAction(eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, eu.wdaqua.lodrank.linkextractor.LinkExtractor.Action)}.
	 */
	@Test
	public void testAddAction2ndRole() {
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		this.linkExtractor.addExclusion(Role.SUBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		assertTrue(this.linkExtractor.listOfExclusions.size() == 2);
		assertTrue(this.linkExtractor.listOfExclusions.containsKey(Role.SUBJECT));
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.PREDICATE));
		assertTrue(this.linkExtractor.listOfExclusions.containsKey(Role.OBJECT));
		assertTrue(this.linkExtractor.listOfExclusions.get(Role.SUBJECT).size() == 1);
		assertTrue(this.linkExtractor.listOfExclusions.get(Role.OBJECT).size() == 1);
		this.linkExtractor.listOfExclusions.get(Role.SUBJECT).forEach(exclusion -> {
			assertTrue(exclusion.getRole().equals(Role.PREDICATE));
			assertTrue(exclusion.getRule().equals(Rule.EQUAL));
			assertTrue(exclusion.value.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		});
		this.linkExtractor.listOfExclusions.get(Role.OBJECT).forEach(exclusion -> {
			assertTrue(exclusion.getRole().equals(Role.PREDICATE));
			assertTrue(exclusion.getRule().equals(Rule.EQUAL));
			assertTrue(exclusion.value.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		});
	}

	@Test
	public void testAddAction2ndRule() {
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/2000/01/rdf-schema#label");
		assertTrue(this.linkExtractor.listOfExclusions.size() == 1);
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.SUBJECT));
		assertFalse(this.linkExtractor.listOfExclusions.containsKey(Role.PREDICATE));
		assertTrue(this.linkExtractor.listOfExclusions.containsKey(Role.OBJECT));
		assertTrue(this.linkExtractor.listOfExclusions.get(Role.OBJECT).size() == 2);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#setUrlProcessor(eu.wdaqua.lodrank.urlprocessor.URLProcessor)}.
	 */
	@Test
	public void testSetUrlProcessor() {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		assertTrue(this.linkExtractor.urlProcessor instanceof URLtoPLD);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#setDataset(java.net.URL)}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testSetDataset() throws MalformedURLException {
		final URLtoPLD urlProcessor = new URLtoPLD();
		this.linkExtractor.setUrlProcessor(urlProcessor);
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		assertTrue(this.linkExtractor.datasetID.equals(urlProcessor.getDataset(new URL("http://xmlns.com/foaf/0.1/Person"))));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#setTriple(org.apache.jena.graph.Triple)}.
	 */
	@Test
	public void testSetTriple() {
		this.linkExtractor.setTriple(new Triple(NodeFactory.createBlankNode(), NodeFactory.createBlankNode(), NodeFactory.createBlankNode()));
		assertTrue(this.linkExtractor.triple instanceof Triple);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#setQuad(org.apache.jena.sparql.core.Quad)}.
	 *
	 * @throws InvalidResourceException
	 */
	@Test
	public void testSetQuad() throws InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setQuad(new Quad(NodeFactory.createURI("http://xmlns.com/foaf/0.1/Person"), NodeFactory.createBlankNode(), NodeFactory.createBlankNode(), NodeFactory.createBlankNode()));
		assertTrue(this.linkExtractor.triple instanceof Triple);
		assertTrue(this.linkExtractor.datasetID instanceof String);
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinks()}.
	 *
	 * @throws MalformedURLException
	 * @throws InvalidResourceException
	 */
	@Test
	public void testGetLinks() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setDuplicates(true);
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		this.linkExtractor.setTriple(new Triple(NodeFactory.createURI("http://csarven.ca/#cert"), NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://www.w3.org/ns/auth/cert#RSAPublicKey")));
		final Entry<String, Collection<String>> entry = this.linkExtractor.getLinks();
		assertTrue(entry.getKey().equals(this.linkExtractor.urlProcessor.getDataset(new URL("http://xmlns.com/foaf/0.1/Person"))));
		assertTrue(entry.getValue().contains("csarven.ca"));
		assertTrue(entry.getValue().contains("w3.org"));
		assertTrue(entry.getValue().contains("w3.org"));
	}

	@Test
	public void testGetLinksWithExclusions() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.addExclusion(Role.OBJECT, Role.PREDICATE, Rule.EQUAL, "http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		this.linkExtractor.setTriple(new Triple(NodeFactory.createURI("http://csarven.ca/#cert"), NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://www.w3.org/ns/auth/cert#RSAPublicKey")));
		final Entry<String, Collection<String>> entry = this.linkExtractor.getLinks();
		assertTrue(entry.getKey().equals(this.linkExtractor.urlProcessor.getDataset(new URL("http://xmlns.com/foaf/0.1/Person"))));
		assertTrue(entry.getValue().size() == 2);
		assertTrue(entry.getValue().contains("csarven.ca"));
		assertTrue(entry.getValue().contains("w3.org"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinksFromTriple(org.apache.jena.graph.Triple, java.net.URL)}.
	 *
	 * @throws InvalidResourceException
	 * @throws MalformedURLException
	 */
	@Test
	public void testGetLinksFromTripleTripleURL() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		final Collection<String> list = this.linkExtractor.getLinksFromTriple(new Triple(NodeFactory.createURI("http://csarven.ca/#cert"), NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://www.w3.org/ns/auth/cert#RSAPublicKey")), new URL("http://xmlns.com/foaf/0.1/Person"));
		assertTrue(list.contains("csarven.ca"));
		assertTrue(list.contains("w3.org"));
		assertTrue(list.contains("w3.org"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinksFromTriple(org.apache.jena.graph.Triple)}.
	 *
	 * @throws MalformedURLException
	 * @throws InvalidResourceException
	 */
	@Test
	public void testGetLinksFromTripleTriple() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		final Collection<String> list = this.linkExtractor.getLinksFromTriple(new Triple(NodeFactory.createURI("http://csarven.ca/#cert"), NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://www.w3.org/ns/auth/cert#RSAPublicKey")));
		assertTrue(list.contains("csarven.ca"));
		assertTrue(list.contains("w3.org"));
		assertTrue(list.contains("w3.org"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinkFromRole(eu.wdaqua.lodrank.linkextractor.LinkExtractor.Role, org.apache.jena.graph.Triple)}.
	 *
	 * @throws MalformedURLException
	 * @throws InvalidResourceException
	 */
	@Test
	public void testGetLinkFromRole() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		final Triple triple = new Triple(NodeFactory.createURI("http://csarven.ca/#cert"), NodeFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"), NodeFactory.createURI("http://www.w3.org/ns/auth/cert#RSAPublicKey"));
		assertTrue(this.linkExtractor.getLinkFromRole(Role.SUBJECT, triple).equals("csarven.ca"));
		assertTrue(this.linkExtractor.getLinkFromRole(Role.PREDICATE, triple).equals("w3.org"));
		assertTrue(this.linkExtractor.getLinkFromRole(Role.OBJECT, triple).equals("w3.org"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinkFromNode(org.apache.jena.graph.Node)}.
	 *
	 * @throws MalformedURLException
	 * @throws InvalidResourceException
	 */
	@Test
	public void testGetLinkFromNode() throws MalformedURLException, InvalidResourceException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		assertTrue(this.linkExtractor.getLinkFromNode(NodeFactory.createURI("http://csarven.ca/#cert")).equals("csarven.ca"));
	}

	/**
	 * Test method for {@link eu.wdaqua.lodrank.linkextractor.LinkExtractor#getLinkFromUrl(java.net.URL)}.
	 *
	 * @throws MalformedURLException
	 */
	@Test
	public void testGetLinkFromUrl() throws MalformedURLException {
		this.linkExtractor.setUrlProcessor(new URLtoPLD());
		this.linkExtractor.setDataset(new URL("http://xmlns.com/foaf/0.1/Person"));
		assertTrue(this.linkExtractor.getLinkFromUrl(new URL("http://csarven.ca/#cert")).equals("csarven.ca"));
	}

}
