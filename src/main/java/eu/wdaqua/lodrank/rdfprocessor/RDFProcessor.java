/**
 *
 */
package eu.wdaqua.lodrank.rdfprocessor;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.sparql.core.Quad;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.source.Source.Format;
import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;
import eu.wdaqua.lodrank.exception.InvalidResourceException;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.loader.RDFLoader;

/**
 * @author José M. Giménez-García
 *
 */
public class RDFProcessor {

	protected RDFLoader<?>	loader;
	protected LinkExtractor	linkExtractor;
	protected LinkWriter	linkWriter;

	protected Logger		logger;

	public RDFProcessor() {
		this.logger = LogManager.getLogger(getClass());
	}

	public void setRDFLoader(final RDFLoader<?> loader) {
		this.loader = loader;
	}

	public void setLinkExtractor(final LinkExtractor linkExtractor) {
		this.linkExtractor = linkExtractor;
	}

	public void setLinkWriter(final LinkWriter linkWriter) {
		this.linkWriter = linkWriter;
	}

	public void setDataset(final URL dataset) {
		this.linkExtractor.setDataset(dataset);
	}

	public void run(final Lang lang) throws IOException {
		run(Format.getFormat(lang));
	}

	public void run(final Format format) throws IOException {
		switch (format) {
			case QUADS:
				runQuads();
				break;
			case TRIPLES:
				runTriples();
		}
	}

	public void runTriples() throws SourceNotOpenableException, DestinationNotOpenableException {
		this.linkWriter.open();
		this.loader.open();
		try {
			if (this.linkExtractor.getDataset() != null) {
			    long numberOfTriples = 0;
			    while (loader.hasNext()) {
			        Triple triple = (Triple) loader.next();
				//this.loader.forEachRemaining(triple -> {
					try {
						this.logger.debug("Getting links for triple: " + triple);
						this.logger.debug("Dataset: " + this.linkExtractor.getDataset());
						this.linkExtractor.setTriple((Triple) triple);
						final Entry<String, Collection<String>> entry = this.linkExtractor.getLinks();
						if (entry != null) {
							this.linkWriter.addLinks(entry.getKey(), entry.getValue());
						}
					} catch (final InvalidResourceException e) {
						this.logger.warn("Invalid resource when reading Triple [" + ((Triple) triple).toString() + "]");
					}
					if (numberOfTriples++ % 1000 == 0) {
					    logger.info("Numper of triples processed = " + numberOfTriples);
                    }
				}//);
			} else {
				this.logger.warn("Could not obtain dataset for source " + this.loader.getSource().toString() + ". No links will be extracted.");
			}
		} catch (final Throwable t) {
			this.logger.info("Catching throwable in the loop", t);
		}
		try {
			this.loader.close();
			this.linkWriter.printLinks();
			this.linkWriter.close();
		} catch (final Throwable t) {
			this.logger.info("Catching throwable after the loop", t);
		}
	}

	// public void runQuads() throws SourceNotOpenableException, DestinationNotOpenableException {
	// this.logger.debug("Extracting links from quads.");
	// this.linkWriter.open();
	// this.loader.open();
	// this.loader.forEachRemaining(quad -> {
	// try {
	// this.logger.debug("Getting links for quad: " + ((Quad) quad).toString());
	// this.linkExtractor.setQuad((Quad) quad);
	// final Entry<String, Collection<String>> entry = this.linkExtractor.getLinks();
	// if (entry != null) {
	// this.linkWriter.addLinks(entry.getKey(), entry.getValue());
	// }
	// } catch (final InvalidResourceException e) {
	// this.logger.debug("Invalid resource when reading Quad " + ((Quad) quad).toString());
	// }
	// });
	// this.loader.close();
	// this.linkWriter.printLinks();
	// this.linkWriter.close();
	// }

	public void runQuads() throws SourceNotOpenableException, DestinationNotOpenableException {
		this.logger.debug("Extracting links from quads.");
		this.linkWriter.open();
		this.loader.open();
		try {
		    long numberOfTriples = 0;
			while (this.loader.hasNext()) {
				final Quad quad = (Quad) this.loader.next();
				try {
					this.logger.debug("Getting links for quad: " + quad.toString());
					this.linkExtractor.setQuad(quad);
					final Entry<String, Collection<String>> entry = this.linkExtractor.getLinks();
					if (entry != null) {
						this.linkWriter.addLinks(entry.getKey(), entry.getValue());
					}
				} catch (final InvalidResourceException e) {
					this.logger.debug("Invalid resource when reading Quad " + quad.toString());
				}
                if (numberOfTriples++ % 1000 == 0) {
                    logger.info("Numper of triples processed = " + numberOfTriples);
                }
			}
		} catch (final Throwable t) {
			this.logger.info("Catching throwable in the loop", t);
		}
		try {
//			this.logger.info("1");
			this.loader.close();
//			this.logger.info("2");
			this.linkWriter.printLinks();
//			this.logger.info("3");
			this.linkWriter.close();
//			this.logger.info("4");
		} catch (final Throwable t) {
			this.logger.info("Catching throwable after the loop", t);
		}
	}

}
