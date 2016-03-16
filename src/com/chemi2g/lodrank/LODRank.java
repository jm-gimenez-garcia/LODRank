/**
 * 
 */
package com.chemi2g.lodrank;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.jena.riot.RiotException;

/**
 * @author chemi2g
 *
 */
public class LODRank {

	static final String	DEFAULT_CONFIG_PATH		= "res";
	static final String	PROCESSED_TRIPLES_FILE	= "processedTriples.aux";
	static final String	PROCESSED_DATASETS_FILE	= "processedDatasets.aux";

	HashSet<String>		processedDatasets;
	int					numDatasets;
	long				numTriples;
	int					step					= 1, start = 1;

	OutlinkExtractor	outlinkExtractor;

	public static void main(String[] args) {
		LODRank lodrank = new LODRank();
		lodrank.readPartialProcessing(DEFAULT_CONFIG_PATH);
		if (args.length == 1) {
			lodrank.setStep(Integer.parseInt(args[0]));
			lodrank.setStart(Integer.parseInt(args[0]));
		} else if (args.length >= 2) {
			lodrank.setStep(Integer.parseInt(args[0]));
			lodrank.setStart(Integer.parseInt(args[1]));
		}
		lodrank.run(DEFAULT_CONFIG_PATH);
	}

	void readPartialProcessing(String path) {
		BufferedReader reader = null;
		File file;
		String line;
		// File fileDatasets = new File(path + "/" + PROCESSED_DATASETS_FILE);
		try {
			try {
				// Read number of triples
				reader = new BufferedReader(new FileReader(path + "/" + PROCESSED_TRIPLES_FILE));
				numTriples = Long.parseLong(reader.readLine());
				reader.close();
			} catch (NumberFormatException e) {
				numTriples = 0;
				numDatasets = 0;
				System.err.println("Error reading number of triples processed. Starting count again.");
			}
			// Read already processed datasets
			processedDatasets = new HashSet<>();
			reader = new BufferedReader(new FileReader(path + "/" + PROCESSED_DATASETS_FILE));
			while ((line = reader.readLine()) != null) {
				processedDatasets.add(line);
				numDatasets++;
			}
			System.out.println("Partial status retrieved. Datasets processed: " + numDatasets + ". Triples processed: " + numTriples);
			System.out.println("Resuming process...");
		} catch (FileNotFoundException e) {
			System.err.println("No files to retrieve partial processing status. Starting again.");
			processedDatasets = new HashSet<>();
			numTriples = 0;
			numDatasets = 0;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while retrieven partial processing status. Starting again.");
			processedDatasets = new HashSet<>();
			numTriples = 0;
			numDatasets = 0;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	void run(String path) {
		outlinkExtractor = new OutlinkExtractor(numTriples);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter processedDatasetsWriter;
		String line;
		int numLine = 0;
		if (processedDatasets == null) {
			processedDatasets = new HashSet<>();
		}

		try {
			processedDatasetsWriter = new BufferedWriter(new FileWriter(path + "/" + PROCESSED_DATASETS_FILE, true));

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					FileWriter numTriplesWriter = null;
					try {
						numTriplesWriter = new FileWriter(path + "/" + PROCESSED_TRIPLES_FILE);
						numTriplesWriter.write(Long.toString(outlinkExtractor.getNumTriples()) + "\n");
						numTriplesWriter.flush();
						processedDatasetsWriter.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						try {
							numTriplesWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});

			while ((line = reader.readLine()) != null) {
				if (((++numLine) - getStart()) % getStep() == 0) {
					String[] urls = line.split(" ");
					if (!processedDatasets.contains(urls[1])) {
						Entry<String, Set<String>> outlinks;
						try {
							if ((outlinks = outlinkExtractor.processDataset(urls[1], urls[0])) != null) {
								Thread t = new Thread(new OutlinkWriter(outlinks.getKey(), outlinks.getValue()));
								t.start();
								processedDatasetsWriter.write(urls[1] + "\n");
							}
						} catch (FileNotFoundException e) {
							System.err.println("No cleaned file found for dataset " + urls[1]);
							System.err.println("Resuming the process...");
						} catch (RiotException e) {
							System.err.println("Error with Jena Parser while processing dataset " + urls[1]);
							System.err.println("Resuming the process...");
						}
					} else {
						System.out.println(urls[1] + " already processed. Skipping.");
					}
				}
				// else {
				// System.out.println("Dataset " + numLine + " ignored");
				// }
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getStep() {
		return step;
	}

	public int getStart() {
		return start;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public void setStart(int start) {
		this.start = start;
	}

}
