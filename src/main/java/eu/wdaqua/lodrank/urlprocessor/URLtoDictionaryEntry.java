package eu.wdaqua.lodrank.urlprocessor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.loader.DictionaryLoader;

public class URLtoDictionaryEntry extends URLProcessor {

	protected LinkedHashMap<Pattern, String> dictionary;

	public URLtoDictionaryEntry() {
		this.dictionary = new LinkedHashMap<>();
	}

	@Override
	public String getDataset() {
		Matcher matcher;
		String dataset = null;
		for (final Map.Entry<Pattern, String> entry : this.dictionary.entrySet()) {
			matcher = entry.getKey().matcher(this.url.toString());
			if (matcher.find()) {
				dataset = this.dictionary.get(entry.getKey()); // Moving the pattern to the first position.
				break;
			}
		}
		return dataset;
	}

	public URLProcessor loadDictionary(final DictionaryLoader loader) throws SourceNotOpenableException {
		loader.open();
		loader.forEachRemaining(entry -> this.dictionary.put(entry.getKey(), entry.getValue()));
		loader.close();
		return this;
	}

	public URLProcessor addDictionaryEntry(final Pattern pattern, final String dataset) {
		this.dictionary.put(pattern, dataset);
		return this;
	}

	public URLProcessor addDictionaryEntry(final String patternString, final String dataset) {
		final Pattern pattern = Pattern.compile(patternString);
		return addDictionaryEntry(pattern, dataset);
	}

}
