/**
 *
 */
package com.chemi2g.lodrank.outlink_extractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.riot.RiotException;

/**
 * @author chemi2g
 *
 */
public class LODRank {

	HashSet<String>			processedDatasets;
	int						numDatasets;
	long					numTriples;

	OutlinkConfiguration	conf;
	OutlinkExtractor		outlinkExtractor;
	Date					date	= new Date();

	private LODRank(final String[] args) {
		this.conf = OutlinkConfiguration.newInstance(args);
	}

	public static void main(final String[] args) {
		final LODRank lodrank = new LODRank(args);
		lodrank.readPartialProcessing(OutlinkConfiguration.DEFAULT_CONFIG_FOLDER);
		lodrank.run(OutlinkConfiguration.DEFAULT_CONFIG_FOLDER);
	}

	void readPartialProcessing(final String path) {
		BufferedReader reader = null;
		String line;
		// File fileDatasets = new File(path + "/" + PROCESSED_DATASETS_FILE);
		try {
			try {
				// Read number of triples
				reader = new BufferedReader(new FileReader(path + "/" + OutlinkConfiguration.PROCESSED_TRIPLES_FILE));
				this.numTriples = Long.parseLong(reader.readLine());
				reader.close();
			} catch (final NumberFormatException e) {
				this.numTriples = 0;
				this.numDatasets = 0;
				System.err.println("Error reading number of triples processed. Starting count again.");
			}
			// Read already processed dictionaryPatterns
			this.processedDatasets = new HashSet<>();
			reader = new BufferedReader(new FileReader(path + "/" + OutlinkConfiguration.PROCESSED_DATASETS_FILE));
			while ((line = reader.readLine()) != null) {
				this.processedDatasets.add(line);
				this.numDatasets++;
			}
			System.out.println("Partial status retrieved. Datasets processed: " + this.numDatasets + ". Triples processed: " + this.numTriples);
			System.out.println("Resuming process...");
		} catch (final FileNotFoundException e) {
			System.err.println("No files to retrieve partial processing status. Starting again.");
			this.processedDatasets = new HashSet<>();
			this.numTriples = 0;
			this.numDatasets = 0;
		} catch (final IOException e) {
			e.printStackTrace();
			System.err.println("Error while retrieven partial processing status. Starting again.");
			this.processedDatasets = new HashSet<>();
			this.numTriples = 0;
			this.numDatasets = 0;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void run(final String path) {
		this.outlinkExtractor = new OutlinkExtractor(this.conf.processSubjects(), this.conf.processPredicates(), this.conf.processObjects(), this.numTriples);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter processedDatasetsWriter;
		String line;
		int numLine = 0;
		if (this.processedDatasets == null) {
			this.processedDatasets = new HashSet<>();
		}

		try {
			processedDatasetsWriter = new BufferedWriter(new FileWriter(path + "/" + OutlinkConfiguration.PROCESSED_DATASETS_FILE, true));

			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					FileWriter numTriplesWriter = null;
					try {
						numTriplesWriter = new FileWriter(path + "/" + OutlinkConfiguration.PROCESSED_TRIPLES_FILE);
						numTriplesWriter.write(Long.toString(LODRank.this.outlinkExtractor.getNumTriples()) + "\n");
						numTriplesWriter.flush();
						processedDatasetsWriter.flush();
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							numTriplesWriter.close();
						} catch (final IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

			while ((line = reader.readLine()) != null) {
				if (((++numLine) - this.conf.getStart()) % this.conf.getStep() == 0) {
					final String[] urls = line.split(" ");
					if (!this.processedDatasets.contains(urls[1])) {
						Entry<String, Set<String>> outlinks;
						try {
							if ((outlinks = this.outlinkExtractor.processDataset(urls[1], urls[0])) != null) {
								final OutlinkWriter ow = new OutlinkWriter(outlinks.getKey(), outlinks.getValue());
								ow.run();
								processedDatasetsWriter.write(urls[1] + "\n");
							}
						} catch (final FileNotFoundException e) {
							System.err.println(new Timestamp(this.date.getTime()) + " No cleaned file found for dataset " + urls[1]);
							System.err.println(new Timestamp(this.date.getTime()) + " Resuming the process...");
						} catch (final RiotException e) {
							System.err.println(new Timestamp(this.date.getTime()) + " Error with Jena Parser while processing dataset " + urls[1]);
							System.err.println(new Timestamp(this.date.getTime()) + " Resuming the process...");
						} catch (final IOException e) {
							System.err.println(new Timestamp(this.date.getTime()) + " IOException while processing dataset " + urls[1]);
							e.printStackTrace();
							System.err.println(new Timestamp(this.date.getTime()) + " Resuming the process...");
						} catch (final HttpException e) {
							System.err.println(new Timestamp(this.date.getTime()) + " HttpException while processing dataset " + urls[1]);
							e.printStackTrace();
							System.err.println(new Timestamp(this.date.getTime()) + " Resuming the process...");
						}
					} else {
						System.out.println(new Timestamp(this.date.getTime()) + urls[1] + " already processed. Skipping.");
					}
				}
				// else {
				// System.out.println("Dataset " + numLine + " ignored");
				// }
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
