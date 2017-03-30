/**
 *
 */
package eu.wdaqua.lodrank.loader;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * @author chemi2g
 *
 */
public class DictionaryLoader extends Loader<Entry<Pattern, String>> {

	protected String separator = " ";

	/*
	 * (non-Javadoc)
	 *
	 * @see eu.wdaqua.lodrank.loader.Loader#next()
	 */
	@Override
	public SimpleEntry<Pattern, String> next() {
		final String[] fields = this.scanner.nextLine().split(this.separator);
		this.logger.debug("Read dictionary line [" + fields[0] + this.separator + fields[1] + "].");
		return new SimpleEntry<>(Pattern.compile("^" + fields[0]), fields[1]);
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
	}

}
