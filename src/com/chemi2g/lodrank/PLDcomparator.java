package com.chemi2g.lodrank;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PLDcomparator {

	static final String PATH_DOMAINS = "res/public_suffix_list.dat";

	List<Pattern> ccSLDpatterns = new LinkedList<Pattern>();
	Deque<Pattern> ccSLDpatterns_recent = new LinkedList<Pattern>();

	String pld = null;
	Matcher matcher;

	public PLDcomparator(String path) {
		readDictionary(path);
	}

	public PLDcomparator() {
		readDictionary();
	}

	void readDictionary(String path) {
		String line;
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(path));
			while ((line = reader.readLine()) != null) {
				if (!(line.startsWith("//") || line.isEmpty())) {
					line = line.replace("*", ".*");
					ccSLDpatterns.add(Pattern.compile("([^\\/\\.]+\\." + line + ")\\/"));
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void readDictionary() {
		readDictionary(PATH_DOMAINS);
	}

	String getPLD(String url) {

		// first look up in recent patterns
		for (Pattern pattern : ccSLDpatterns_recent) {
			matcher = pattern.matcher(url);
			if (matcher.find()) {
				pld = matcher.group(1);
				break;
			}
		}

		// if not in recent, look up in all patterns
		if (pld == null) {
			for (Pattern pattern : ccSLDpatterns) {
				matcher = pattern.matcher(url);
				if (matcher.find()) {
					pld = matcher.group(1);
					ccSLDpatterns_recent.push(pattern);
					break;
				}
			}
		}
		return pld;
	}

}
