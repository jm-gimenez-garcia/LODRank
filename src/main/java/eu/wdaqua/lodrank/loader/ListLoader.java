/**
 *
 */
package eu.wdaqua.lodrank.loader;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

/**
 * @author José M. Giménez-García
 *
 */
public class ListLoader extends Loader<Entry<String, String>> {

	protected int		start		= 1, step = 1;
	protected boolean	firstRead	= true;
	protected String	separator	= " ";

	public void setStart(final int start) {
		this.start = start;
	}

	public void setStep(final int step) {
		this.step = step;
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
	}

	@Override
	public Entry<String, String> next() {
		if (this.firstRead) {
			this.logger.debug("Skipping the first " + (this.start - 1) + " lines.");
			for (int i = 1; i < this.start; i++) {
				this.scanner.nextLine();
			}
			this.firstRead = false;
		} else {
			this.logger.debug("Skipping " + (this.step - 1) + " lines.");
			for (int i = 1; i < this.step; i++) {
				this.scanner.nextLine();
			}
		}
		// Return the element(s) for the line. Only return 2nd field if more than one field is gotten by split command.
		final String[] fields = this.scanner.nextLine().split(this.separator);
		this.logger.debug("Read list line.");
		this.logger.debug("First field = [" + fields[0] + "].");
		this.logger.debug("Second field = [" + (fields.length == 1 ? null : fields[1]) + "].");
		return new SimpleEntry<>(fields[0], fields.length == 1 ? null : fields[1]);
	}

}
