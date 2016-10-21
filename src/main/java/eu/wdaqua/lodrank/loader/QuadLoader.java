/**
 *
 */
package eu.wdaqua.lodrank.loader;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.lang.PipedQuadsStream;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.sparql.core.Quad;

/**
 * @author José M. Giménez-García
 *
 */
public class QuadLoader extends RDFLoader<Quad> {

	protected Lang lang = Lang.NQUADS;

	/**
	 * @param lang
	 */
	protected QuadLoader(final Lang lang) {
		super(lang);
	}

	@Override
	protected PipedRDFStream<Quad> getRdfStream(final PipedRDFIterator<Quad> iterator) {
		return new PipedQuadsStream(iterator);
	}

}
