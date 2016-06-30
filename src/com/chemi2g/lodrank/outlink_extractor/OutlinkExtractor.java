package com.chemi2g.lodrank.outlink_extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

import org.apache.jena.atlas.web.HttpException;
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

	static final String		QUADS_END						= ".nq.gz";
	static final String		LODLAUNDROMAT_ENDPOINT			= "http://sparql.backend.lodlaundromat.org";
	static final String		DATASET_URI_QUERY				= "SELECT ?url WHERE {<%s> <http://lodlaundromat.org/ontology/url> ?url}";
	static final String		DATASET_URI_QUERY_WITH_ARCHIVE	= "SELECT ?url WHERE {?archive <http://lodlaundromat.org/ontology/containsEntry> <%s> . ?archive <http://lodlaundromat.org/ontology/url> ?url}";
	static final long		ZERO_TRIPLES					= 0;

	OutlinkConfiguration	conf;

	URIcomparator			uriComparator					= new URIcomparator();

	HashSet<String>			predicates						= new HashSet<String>();

	Date					date							= new Date();

	boolean					processSubjects, processPredicates, processObjects;
	long					numTriples;

	public OutlinkExtractor(final boolean processSubjects, final boolean processPredicates, final boolean processObjects, final long numTriples) throws IOException {
		this.conf = OutlinkConfiguration.getInstance();
		this.processSubjects = processSubjects;
		this.processPredicates = processPredicates;
		this.processObjects = processObjects;
		this.numTriples = numTriples;
		readPredicates(new File(this.conf.getPredicatesFile()));
	}

	public OutlinkExtractor(final boolean processSubjects, final boolean processPredicates, final boolean processObjects) throws IOException {
		this(processSubjects, processPredicates, processObjects, ZERO_TRIPLES);
	}

	public OutlinkExtractor(final long numTriples) throws IOException {
		this(OutlinkConfiguration.PROCESS_SUBJECTS_DEFAULT, OutlinkConfiguration.PROCESS_PREDICATES_DEFAULT, OutlinkConfiguration.PROCESS_OBJECTS_DEFAULT, numTriples);
	}

	public OutlinkExtractor() throws IOException {
		this(OutlinkConfiguration.PROCESS_SUBJECTS_DEFAULT, OutlinkConfiguration.PROCESS_PREDICATES_DEFAULT, OutlinkConfiguration.PROCESS_OBJECTS_DEFAULT, ZERO_TRIPLES);
	}

	void readPredicates(final File file) throws IOException {
		String line;
		final BufferedReader reader = new BufferedReader(new FileReader(file));
		System.out.println("Reading properties to ignore objects from " + file.toString());
		while ((line = reader.readLine()) != null) {
			this.predicates.add(line);
		}
		reader.close();
	}

	URL query(final String queryString, final String endpoint) throws MalformedURLException {
		URL url = null;
		try {
			final Query query = QueryFactory.create(queryString);
			final QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
			final ResultSet results = qexec.execSelect();
			if (results.hasNext()) {
				// if (results.hasNext()) {
				// throw new MultipleResultsException("More than one result when
				// querying for dataset PLD");
				// }
				url = new URL(results.next().getResource("url").toString());
			}
			qexec.close();
		} catch (final HttpException e) {
			System.err.println(new Timestamp(this.date.getTime()) + " HttpException while querying SPARQL endpoint");
			e.printStackTrace();
			System.err.println(new Timestamp(this.date.getTime()) + " Resuming the process...");
		}
		return url;
	}

	Entry<String, Set<String>> processDataset(final String resource, final String download) throws HttpException, FileNotFoundException, RiotException, IOException {
		Entry<String, Set<String>> processedDataset = null;
		final PipedRDFIterator<Triple> triples = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> rdfStream = new PipedTriplesStream(triples);
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		String datasetPLD = null;
		final Set<String> outlinks = new HashSet<String>();
		Triple triple;
		String resourcePLD;
		long datasetTriples = 0;
		URL datasetUrl;
		if ((datasetUrl = query(String.format(DATASET_URI_QUERY, resource), LODLAUNDROMAT_ENDPOINT)) == null) {
			datasetUrl = query(String.format(DATASET_URI_QUERY_WITH_ARCHIVE, resource), LODLAUNDROMAT_ENDPOINT);
		}
		if ((datasetPLD = this.uriComparator.getDatasetFromDumpURL(datasetUrl.toString())) != null) {
			System.out.println(new Timestamp(this.date.getTime()) + " Processing dataset " + datasetPLD);
			final URL downloadUrl = new URL(download);
			final InputStream stream = new GZIPInputStream(downloadUrl.openStream());

			// Create a runnable for our parser thread
			final Runnable parser = () -> {
				// Call the parsing process.
				if (download.endsWith(QUADS_END)) {
					RDFDataMgr.parse(rdfStream, stream, Lang.NQUADS);
				} else {
					RDFDataMgr.parse(rdfStream, stream, Lang.NTRIPLES);
				}
			};

			// Start the parser on another thread
			executor.submit(parser);

			while (triples.hasNext()) {
				datasetTriples++;
				triple = triples.next();
				if (this.processSubjects && triple.getSubject().isURI()) {
					resourcePLD = this.uriComparator.getDatasetFromIRI(triple.getSubject().toString());
					if (resourcePLD != null && !datasetPLD.equals(resourcePLD)) {
						outlinks.add(resourcePLD);
					}
				}
				if (this.processPredicates && triple.getPredicate().isURI()) {
					resourcePLD = this.uriComparator.getDatasetFromIRI(triple.getPredicate().toString());
					if (resourcePLD != null && !datasetPLD.equals(resourcePLD)) {
						outlinks.add(resourcePLD);
					}
				}
				if (this.processObjects && triple.getObject().isURI()) {
					if (this.predicates.contains(triple.getPredicate().toString())) {
						// System.out.println("Ignoring object: " + triple.getObject().toString());
						// System.out.println("Predicate: " + triple.getPredicate().toString());
					} else {
						resourcePLD = this.uriComparator.getDatasetFromIRI(triple.getObject().toString());
						if (resourcePLD != null && !datasetPLD.equals(resourcePLD)) {
							outlinks.add(resourcePLD);
						}
					}
				}
			}

			this.numTriples += datasetTriples;
			System.out.println(new Timestamp(this.date.getTime()) + " Finished processing " + datasetPLD + ". Dataset triples: " + datasetTriples + ". Total triples: " + this.numTriples);

			processedDataset = new SimpleEntry<String, Set<String>>(datasetPLD, outlinks);
		} else {
			System.out.println(new Timestamp(this.date.getTime()) + " No valid dataset for " + datasetUrl.toString() + ". Skipping dataset");
		}
		return processedDataset;
	}

	public long getNumTriples() {
		return this.numTriples;
	}
}
