package com.chemi2g.lodrank;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;

public class OutlinkExtractor {

	static final String	QUADS_END						= ".nq.gz";
	static final String	LODLAUNDROMAT_ENDPOINT			= "http://sparql.backend.lodlaundromat.org";
	static final String	DATASET_URI_QUERY				= "SELECT ?url WHERE {<%s> <http://lodlaundromat.org/ontology/url> ?url}";
	static final String	DATASET_URI_QUERY_WITH_ARCHIVE	= "SELECT ?url WHERE {?archive <http://lodlaundromat.org/ontology/containsEntry> <%s> . ?archive <http://lodlaundromat.org/ontology/url> ?url}";

	long				numTriples;

	public long getNumTriples() {
		return numTriples;
	}

	URL query(String queryString, String endpoint) throws MalformedURLException {
		URL url = null;
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = qexec.execSelect();
		if (results.hasNext()) {
			// if (results.hasNext()) {
			// throw new MultipleResultsException("More than one result when
			// querying for dataset PLD");
			// }
			url = new URL(results.next().getResource("url").toString());
		}
		qexec.close();
		return url;
	}

	Entry<String, Set<String>> processDataset(String resource, String download) throws FileNotFoundException, RiotException, IOException {
		PipedRDFIterator<Triple> triples = new PipedRDFIterator<Triple>();
		PipedRDFStream<Triple> rdfStream = new PipedTriplesStream(triples);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		String datasetPLD = null;
		Set<String> outlinks = new HashSet<String>();
		Triple triple;
		String resourcePLD;
		PLDcomparator pldComparator = new PLDcomparator();
		Date date = new Date();
		long datasetTriples = 0;
		URL datasetUrl;
		if ((datasetUrl = query(String.format(DATASET_URI_QUERY, resource), LODLAUNDROMAT_ENDPOINT)) == null) {
			datasetUrl = query(String.format(DATASET_URI_QUERY_WITH_ARCHIVE, resource), LODLAUNDROMAT_ENDPOINT);
		}
		datasetPLD = pldComparator.getPLD(datasetUrl.toString());
		System.out.println(new Timestamp(date.getTime()) + " Processing dataset " + datasetPLD);
		URL downloadUrl = new URL(download);
		final InputStream stream = new GZIPInputStream(downloadUrl.openStream());

		// Create a runnable for our parser thread
		Runnable parser = new Runnable() {
			@Override
			public void run() {
				// Call the parsing process.
				if (download.endsWith(QUADS_END)) {
					RDFDataMgr.parse(rdfStream, stream, Lang.NQUADS);
				} else {
					RDFDataMgr.parse(rdfStream, stream, Lang.NTRIPLES);
				}
			}
		};

		// Start the parser on another thread
		executor.submit(parser);

		while (triples.hasNext()) {
			datasetTriples++;
			triple = triples.next();
			if (triple.getSubject().isURI()) {
				resourcePLD = pldComparator.getPLD(triple.getSubject().toString());
				if (resourcePLD != null && !datasetPLD.equals(resourcePLD)) {
					outlinks.add(resourcePLD);
				}
			}
			if (triple.getObject().isURI()) {
				resourcePLD = pldComparator.getPLD(triple.getObject().toString());
				if (resourcePLD != null && !datasetPLD.equals(resourcePLD)) {
					outlinks.add(resourcePLD);
				}
			}
		}

		numTriples += datasetTriples;
		System.out.println(new Timestamp(date.getTime()) + " Finished processing " + datasetPLD + ". Dataset triples: " + datasetTriples + ". Total triples: " + numTriples);

		return new SimpleEntry<String, Set<String>>(datasetPLD, outlinks);
	}

}
