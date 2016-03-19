package com.chemi2g.lodrank.outlink_extractor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.google.common.net.InternetDomainName;

public class PLDcomparator {

	Date date = new Date();

	// static final String PATH_DOMAINS = "res/public_suffix_list.dat";
	//
	// Deque<Pattern> ccSLDpatterns = new LinkedList<Pattern>();
	// Deque<Pattern> ccSLDpatterns_recent = new LinkedList<Pattern>();
	//
	// Matcher matcher;

	public PLDcomparator(String path) {
		// readDictionary(path);
	}

	public PLDcomparator() {
		// readDictionary();
	}

	// void readDictionary(String path) {
	// String line;
	// BufferedReader reader;
	// try {
	// reader = new BufferedReader(new FileReader(path));
	// while ((line = reader.readLine()) != null) {
	// if (!(line.startsWith("//") || line.isEmpty())) {
	// line = line.replace("*", ".*");
	// ccSLDpatterns.push(Pattern.compile("([^\\/\\.]+\\." + line + ")\\/"));
	// }
	// }
	// reader.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// void readDictionary() {
	// readDictionary(PATH_DOMAINS);
	// }

	// String getPLD(String url) {
	//
	// String pld = null;
	//
	// // first look up in recent patterns
	// for (Pattern pattern : ccSLDpatterns_recent) {
	// matcher = pattern.matcher(url);
	// if (matcher.find()) {
	// pld = matcher.group(1);
	// break;
	// }
	// }
	//
	// // if not in recent, look up in all patterns
	// if (pld == null) {
	// for (Pattern pattern : ccSLDpatterns) {
	// matcher = pattern.matcher(url);
	// if (matcher.find()) {
	// pld = matcher.group(1);
	// ccSLDpatterns_recent.push(pattern);
	// break;
	// }
	// }
	// }
	//
	// return pld;
	// }

	String getPLD(URL url) {
		String pld = null;
		try {
			pld = InternetDomainName.from(url.getHost()).topPrivateDomain().toString();
		} catch (IllegalStateException | IllegalArgumentException e) {
			// System.err.println(new Timestamp(date.getTime()) + " Warning: " + url.toString() + " does not contain valid domain");
		}
		return pld;
	}

	String getPLD(String url) {
		String pld = null;
		try {
			pld = getPLD(new URL(url));
		} catch (MalformedURLException e) {
			// System.err.println(new Timestamp(date.getTime()) + " Warning: " + url + " does not contain valid domain");
		}
		return pld;
	}

}
