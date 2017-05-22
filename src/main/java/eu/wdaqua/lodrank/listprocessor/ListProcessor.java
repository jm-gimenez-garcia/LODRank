/**
 *
 */
package eu.wdaqua.lodrank.listprocessor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.source.Source;
import eu.wdaqua.lodrank.source.Source.Input;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.loader.ListLoader;
import eu.wdaqua.lodrank.loader.RDFLoader;
import eu.wdaqua.lodrank.rdfprocessor.RDFProcessor;

/**
 * @author José M. Giménez-García
 *
 */
public class ListProcessor {

	protected Lang			lang;
	protected boolean		forceLang;
	protected Input			source;
	protected ListLoader	listLoader;
	protected RDFProcessor	rdfProcessor;

	protected Logger		logger;

	public ListProcessor() {
		this.logger = LogManager.getLogger(getClass());
	}

	public void setRDFProcessor(final RDFProcessor rdfProcessor) {
		this.rdfProcessor = rdfProcessor;
	}

	public void setLang(final Lang lang) {
		this.lang = lang;
	}

	public void setForceLang(final boolean forceLang) {
		this.forceLang = forceLang;
	}

	public void setListLoader(final ListLoader loader) {
		this.listLoader = loader;
	}

	public void run() throws SourceNotOpenableException {
		this.listLoader.open();
		this.listLoader.forEachRemaining(input -> {
			try {
				final Lang langToUse;
				if (this.forceLang == true) {
					langToUse = this.lang;
				} else {
					langToUse = RDFLanguages.resourceNameToLang(input.getKey(), this.lang);
				}
				final RDFLoader<?> rdfLoader = RDFLoader.getRDFLoader(langToUse);
				rdfLoader.attachSource(Source.getSource(input.getKey()));
				this.rdfProcessor.setDataset(input.getValue() != null ? new URL(input.getValue()) : null);
				this.rdfProcessor.setRDFLoader(rdfLoader);
				this.rdfProcessor.run(langToUse);
			} catch (final SourceNotOpenableException | MalformedURLException e) {
				this.logger.error("Error opening dataset [" + input.getKey() + "," + input.getValue() + "]", e);
			} catch (final IOException e) {
				this.logger.error("Error writing links for dataset [" + input.getKey() + "," + input.getValue() + "]", e);
			}
		});
		this.listLoader.close();
	}
}
