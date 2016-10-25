package eu.wdaqua.lodrank.linkextractor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Quad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.exception.InvalidResourceException;
import eu.wdaqua.lodrank.urlprocessor.URLProcessor;

public class LinkExtractor {

	public enum Role {
		SUBJECT("SUBJECT"), PREDICATE("PREDICATE"), OBJECT("OBJECT");

		String label;

		public static Role getRole(final String roleString) {
			Role role = null;
			if (roleString.toUpperCase().equals(SUBJECT.toString().toUpperCase())) {
				role = SUBJECT;
			} else if (roleString.toUpperCase().equals(PREDICATE.toString().toUpperCase())) {
				role = PREDICATE;
			} else if (roleString.toUpperCase().equals(OBJECT.toString().toUpperCase())) {
				role = OBJECT;
			}
			return role;
		}

		Role(final String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}

	};

	public enum Rule {
		CONTAINS("CONTAINS"), DATASET("DATASET"), DISTINCT("DISTINCT"), EQUALS("EQUALS");

		String label;

		public static Rule getRule(final String ruleString) {
			Rule rule = null;
			if (ruleString.toUpperCase().equals(CONTAINS.toString().toUpperCase())) {
				rule = CONTAINS;
			} else if (ruleString.toUpperCase().equals(DATASET.toString().toUpperCase())) {
				rule = DATASET;
			} else if (ruleString.toUpperCase().equals(DISTINCT.toString().toUpperCase())) {
				rule = DISTINCT;
			} else if (ruleString.toUpperCase().equals(EQUALS.toString().toUpperCase())) {
				rule = EQUALS;
			}
			return rule;
		}

		Rule(final String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

	protected URLProcessor						urlProcessor;

	protected String							datasetID;
	protected Triple							triple;
	protected boolean							processSubjects	= true, processPredicates = true, processObjects = true;
	protected boolean							duplicates		= false;

	protected HashMap<Role, HashSet<Exclusion>>	listOfExclusions;

	protected Logger							logger;

	public LinkExtractor() {
		this.logger = LogManager.getLogger(getClass());
		this.listOfExclusions = new HashMap<>(3, 1);
	}

	public void setDuplicates(final boolean duplicates) {
		this.duplicates = duplicates;
	}

	public void setExclusions(final HashMap<Role, HashSet<Exclusion>> listOfExclusions) {
		this.listOfExclusions = listOfExclusions;
	}

	public void setProcessSubjects(final boolean processSubjects) {
		this.logger.debug("Setting processSubjects = " + processSubjects);
		this.processSubjects = processSubjects;
	}

	public void setProcessPredicates(final boolean processPredicates) {
		this.logger.debug("Setting processPredicates = " + processPredicates);
		this.processPredicates = processPredicates;
	}

	public void setProcessObjects(final boolean processObjects) {
		this.logger.debug("Setting processObjects = " + processObjects);
		this.processObjects = processObjects;
	}

	public void addExclusion(final String roleToExclude, final String roleToCheck, final String rule, final String value) {
		addExclusion(Role.getRole(roleToExclude), Role.getRole(roleToCheck), Rule.getRule(rule), value);
	}

	public void addExclusion(final Role roleToExclude, final Role roleToCheck, final Rule rule, final String value) {
		if (this.listOfExclusions.containsKey(roleToExclude)) {
			this.logger.debug("List of exclusions contains role " + roleToExclude + ". Adding exclusion to the list.");
			this.listOfExclusions.get(roleToExclude).add(new Exclusion(roleToCheck, rule, value));
		} else {
			this.logger.debug("List of exclusions does not contain role " + roleToExclude + ". Adding role and exclusion to the list.");
			final HashSet<Exclusion> exclusions = new HashSet<>();
			exclusions.add(new Exclusion(roleToCheck, rule, value));
			this.listOfExclusions.put(roleToExclude, exclusions);
		}
	}

	/**
	 * @param urlProcessor
	 * @return
	 */
	public void setUrlProcessor(final URLProcessor urlProcessor) {
		this.urlProcessor = urlProcessor;
	}

	/**
	 * @param dataset
	 */
	public void setDataset(final URL url) {
		this.datasetID = this.urlProcessor.getDataset(url);
	}

	public String getDataset() {
		return this.datasetID;
	}

	public void setTriple(final Triple triple) {
		this.triple = triple;
	}

	public void setQuad(final Quad quad) throws InvalidResourceException {
		try {
			setDataset(new URL(quad.getGraph().toString()));
		} catch (final MalformedURLException e) {
			throw new InvalidResourceException("Graph is not a valid URL", e);
		}
		setTriple(quad.asTriple());
	}

	public SimpleEntry<String, Collection<String>> getLinks() throws InvalidResourceException {
		SimpleEntry<String, Collection<String>> entry = null;
		final Collection<String> links = getLinksFromTriple(this.triple);
		if (!links.isEmpty()) {
			entry = new SimpleEntry<>(this.datasetID, links);
		}
		return entry;
	}

	/**
	 * @param triple
	 * @param dataset
	 * @return
	 * @throws InvalidResourceException
	 */
	protected Collection<String> getLinksFromTriple(final Triple triple, final URL dataset) throws InvalidResourceException {
		setDataset(dataset);
		return getLinksFromTriple(triple);
	}

	/**
	 * @param triple
	 * @return
	 * @throws InvalidResourceException
	 */
	protected Collection<String> getLinksFromTriple(final Triple triple) throws InvalidResourceException {
		Collection<String> links;
		if (this.duplicates) {
			links = new LinkedList<>();
		} else {
			links = new HashSet<>();
		}
		if (getDataset() != null) {
			links = addLinkIfNotNull(links, getLinkFromRole(Role.SUBJECT, triple));
			links = addLinkIfNotNull(links, getLinkFromRole(Role.PREDICATE, triple));
			links = addLinkIfNotNull(links, getLinkFromRole(Role.OBJECT, triple));
		} else {
			this.logger.warn("Could not obtain dataset for triple. No links will be extracted.");
		}
		return links;
	}

	protected Collection<String> addLinkIfNotNull(final Collection<String> list, final String link) {
		final Collection<String> returnList = list;
		if (link != null) {
			returnList.add(link);
		}
		return returnList;
	}

	protected String getLinkFromRole(final Role role, final Triple triple) throws InvalidResourceException {
		String link = null;
		if (role.equals(Role.SUBJECT) && this.processSubjects
				|| role.equals(Role.PREDICATE) && this.processPredicates
				|| role.equals(Role.OBJECT) && this.processObjects) {
			this.logger.debug("Processing " + role + ".");
			boolean process = true;
			if (this.listOfExclusions.containsKey(role)) {
				this.logger.debug("Role " + role + " is in list of exclusions. Checking if it has to be excluded.");
				for (final Exclusion exclusion : this.listOfExclusions.get(role)) {
					if (exclusion.exclude(triple)) {
						this.logger.debug("Triple [" + triple.toString() + "] exclusion: " + exclusion.getRole() + " " + exclusion.getRule() + " " + exclusion.getValue() + ".");
						process = false;
						break;
					}
				}
			}
			if (process) {
				switch (role) {
					case SUBJECT:
						link = getLinkFromNode(triple.getSubject());
						break;
					case PREDICATE:
						link = getLinkFromNode(triple.getPredicate());
						break;
					case OBJECT:
						link = getLinkFromNode(triple.getObject());
						break;
				}
				this.logger.debug("Link for role " + role + " is " + link);
			} else {
				this.logger.debug("Not extracting links for role " + role + ".");
			}
		}
		return link;
	}

	protected String getLinkFromNode(final Node node) throws InvalidResourceException {
		String link = null;
		if (node.isURI()) {
			try {
				link = getLinkFromUrl(new URL(node.toString()));
			} catch (final MalformedURLException e) {
				throw new InvalidResourceException("Error while converting resource to URL", e);
			}
		}
		return link;
	}

	protected String getLinkFromUrl(final URL url) {
		String link = null;
		final String resourceDataset = this.urlProcessor.getDataset(url);
		if (resourceDataset != null && !this.datasetID.equals(resourceDataset)) {
			link = resourceDataset;
		}
		return link;
	}

	public class Exclusion {

		protected Role				role;
		protected Rule				rule;
		protected String			value;

		protected String			dataset;
		protected Pattern			pattern;
		Function<String, Boolean>	checkFunction;

		public Exclusion(final Role role, final Rule rule, final String value) {
			this.role = role;
			this.rule = rule;
			if (rule.equals(Rule.CONTAINS)) {
				this.pattern = Pattern.compile(value);
			} else if (rule.equals(Rule.DATASET)) {
				try {
					this.dataset = LinkExtractor.this.urlProcessor.getDataset(new URL(value));
				} catch (final MalformedURLException e) {
					LinkExtractor.this.logger.error("[" + value + "] is not a valid URL. Impossible to extract dataset. Rule EQUALS will be used instead.");
					this.rule = Rule.EQUALS;
					this.value = value;
				}
			} else {
				this.value = value;
			}

			switch (this.rule) {
				case CONTAINS:
					this.checkFunction = (v) -> {
						if (this.pattern.matcher(v).find()) {
							return true;
						} else {
							return false;
						}
					};
					break;
				case DATASET:
					this.checkFunction = (v) -> {
						try {
							if (LinkExtractor.this.urlProcessor.getDataset(new URL(v)).equals(this.dataset)) {
								return true;
							} else {
								return false;
							}
						} catch (final MalformedURLException e) {
							LinkExtractor.this.logger.warn("[" + v + "] is not a valid url, therefore is not from dataset " + v);
							return false;
						}
					};
					break;
				case DISTINCT:
					this.checkFunction = (v) -> {
						if (v.equals(this.value)) {
							return false;
						} else {
							return true;
						}
					};
					break;
				case EQUALS:
					this.checkFunction = (v) -> {
						if (v.equals(value)) {
							return true;
						} else {
							return false;
						}
					};
					break;
			}
		}

		public Role getRole() {
			return this.role;
		}

		public Rule getRule() {
			return this.rule;
		}

		public String getValue() {
			return this.value;
		}

		public boolean exclude(final Triple triple) {
			final Node nodeToCheck;
			switch (this.role) {
				case SUBJECT:
					nodeToCheck = triple.getSubject();
					break;
				case PREDICATE:
					nodeToCheck = triple.getPredicate();
					break;
				case OBJECT:
					nodeToCheck = triple.getObject();
					break;
				default:
					nodeToCheck = null;
					break;
			}
			return this.checkFunction.apply(nodeToCheck.toString());
		}

		protected Boolean excludeContains(final String value) {
			return false;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((this.role == null) ? 0 : this.role.hashCode());
			result = prime * result + ((this.rule == null) ? 0 : this.rule.hashCode());
			result = prime * result + ((this.value == null) ? 0 : this.value.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final Exclusion other = (Exclusion) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (this.role != other.role) {
				return false;
			}
			if (this.rule != other.rule) {
				return false;
			}
			if (this.value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!this.value.equals(other.value)) {
				return false;
			}
			return true;
		}

		protected LinkExtractor getOuterType() {
			return LinkExtractor.this;
		}

	}

}
