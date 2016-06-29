package com.chemi2g.lodrank.outlink_extractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URIcomparator {

	Date							date				= new Date();

	HashMap<String, String>			dictionaryDumps		= new HashMap<String, String>();
	LinkedHashMap<Pattern, String>	dictionaryPatterns	= new LinkedHashMap<Pattern, String>(16, 0.75f, true);; // defined with access order

	OutlinkConfiguration			conf;
	Matcher							matcher;

	public URIcomparator() {
		this.conf = OutlinkConfiguration.getInstance();
		readDictionaries();
		System.out.println("Dictionaries loaded.");
	}

	void readDictionaries() {
		String line;
		String[] fields;
		BufferedReader reader;
		Pattern pattern;
		// final HashMap<String, String> dumpMap = new HashMap<String, String>();
		// final HashSet<String> namespaceSet = new HashSet<String>();
		try {
			// Read Dump download URLs dictionary
			reader = new BufferedReader(new FileReader(this.conf.getDictionaryDumpsFile()));
			System.out.println("Reading Dumps Dictionary from " + this.conf.getDictionaryDumpsFile());
			while ((line = reader.readLine()) != null) {
				fields = line.split(",");
				this.dictionaryDumps.put(fields[1], fields[0]);
				// dumpMap.put(fields[0], fields[1]);
			}
			reader.close();

			// Read Namespaces dictionary
			reader = new BufferedReader(new FileReader(this.conf.getDictionaryNamespacesFile()));
			System.out.println("Reading Namespaces Dictionary from " + this.conf.getDictionaryNamespacesFile());
			while ((line = reader.readLine()) != null) {
				fields = line.split(",");
				if (this.dictionaryDumps.containsValue(fields[0])) { // Assign only if we are going to process the dataset
					pattern = Pattern.compile("^" + fields[1]);
					this.dictionaryPatterns.put(pattern, fields[0]);
					// namespaceSet.add(dumpMap.get(fields[1]));
				}
			}
			reader.close();

			this.dictionaryDumps.values().removeIf(value -> !this.dictionaryPatterns.containsValue(value));

		} catch (final FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String getDatasetFromDumpURL(final String url) {
		return this.dictionaryDumps.get(url);
	}

	String getDatasetFromIRI(final String iri) {
		String dataset = null;
		for (final Map.Entry<Pattern, String> entry : this.dictionaryPatterns.entrySet()) {
			this.matcher = entry.getKey().matcher(iri);
			if (this.matcher.find()) {
				dataset = this.dictionaryPatterns.get(entry.getKey()); // Moving the pattern to the first position.
				break;
			}
		}
		return dataset;
	}
}
