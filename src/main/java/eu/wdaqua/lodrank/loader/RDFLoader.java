/**
 *
 */
package eu.wdaqua.lodrank.loader;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.logging.log4j.LogManager;

import eu.wdaqua.lodrank.Source.Format;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public abstract class RDFLoader<I> extends Loader<I> {

	protected Lang					lang;

	protected PipedRDFIterator<I>	rdfIterator;
	protected PipedRDFStream<I>		rdfStream;
	protected ExecutorService		executor;
	protected InputStream			inputStream;
	protected Runnable				parser;

	public static RDFLoader<?> getRDFLoader(final Lang lang) {
		RDFLoader<?> loader = null;
		if (Format.getFormat(lang).equals(Format.QUADS)) {
			loader = new QuadLoader(lang);
		} else {
			loader = new TripleLoader(lang);
		}
		return loader;
	}

	protected RDFLoader(final Lang lang) {
		this.logger = LogManager.getLogger(getClass());
		this.logger.debug("Creating " + getClass().getName());
		setLang(lang);
	}

	public void setLang(final Lang lang) {
		this.lang = lang;
	}

	@Override
	public void open() throws SourceNotOpenableException {
		this.rdfIterator = new PipedRDFIterator<>();
		this.rdfStream = getRdfStream(this.rdfIterator); // Requires different implementation for different formats (Triples or Quads)
		this.executor = Executors.newSingleThreadExecutor();
		this.inputStream = this.source.getInputStream();
		this.parser = () -> {
			RDFDataMgr.parse(this.rdfStream, this.inputStream, this.lang);
		};
		this.executor.submit(this.parser);
		// For debug purposes
		// final Future<?> future = this.executor.submit(this.parser);
		// try {
		// future.get();
		// } catch (final Exception ex) {
		// ex.getCause().printStackTrace();
		// }
	}

	@Override
	public boolean hasNext() {
		try {
			return this.rdfIterator.hasNext();
		} catch (final RiotException e) {
			this.logger.error("Error when getting next triple. Returning hasNext = false.", e);
			return false;
		}
	}

	@Override
	public I next() {
		return this.rdfIterator.next();
	}

	@Override
	public void close() {
		this.executor.shutdown();
		this.rdfIterator.close();
		try {
			this.inputStream.close();
		} catch (final Exception e) {
			this.logger.error("Error when closing the input inputStream");
			e.printStackTrace();
		}
		// this.executor.awaitTermination(1, TimeUnit.HOURS);
	}

	protected abstract PipedRDFStream<I> getRdfStream(PipedRDFIterator<I> iterator);

}
