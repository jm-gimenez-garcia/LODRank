/**
 *
 */
package eu.wdaqua.lodrank.loader;

import org.apache.jena.graph.Triple;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

/**
 * @author Jos� M. Gim�nez-Garc�a
 *
 */
public class TripleLoader extends RDFLoader<Triple> {

	protected Lang lang = Lang.NTRIPLES;

	protected TripleLoader(final Lang lang) {
		super(lang);
	}

	@Override
	protected PipedRDFStream<Triple> getRdfStream(final PipedRDFIterator<Triple> iterator) {
		return new PipedTriplesStream(iterator);
	}

}
