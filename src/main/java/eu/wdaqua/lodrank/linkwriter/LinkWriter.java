/**
 *
 */
package eu.wdaqua.lodrank.linkwriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.util.Collection;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class LinkWriter {

	protected Logger									logger;
	protected PrintWriter								writer;
	protected HashMap<String, PrintWriter>				writers;
	protected File										outputFolder;
	protected String									fileName		= "output.csv";
	protected HashMap<String, HashMap<String, Integer>>	links;
	protected String									separator		= ",";
	protected boolean									separateFiles	= false;
	protected boolean									duplicates		= false;
	protected boolean									overwrite		= false;

	public LinkWriter() {
		this.logger = LogManager.getLogger();
		this.links = new HashMap<>();
	}

	public void setOverwrite(final boolean overwrite) {
		this.overwrite = overwrite;
	}

	public void setSeparateFiles(final boolean separateFiles) {
		this.separateFiles = separateFiles;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public void setDuplicates(final boolean duplicates) {
		this.duplicates = duplicates;

	}

	public void setOutputFolder(final File folder) throws DestinationNotOpenableException {
		if (!folder.exists()) {
			folder.mkdirs();
		} else {
			if (!folder.isDirectory()) {
				this.logger.error("Destination is not a outputFolder.");
				throw new DestinationNotOpenableException("Destination is not a outputFolder");
			}
		}
		this.outputFolder = folder;
	}

	public <C extends Collection<String>> void addLinks(final String dataset, final C links) {
		if (!this.links.containsKey(dataset)) {
			final HashMap<String, Integer> map = new HashMap<>();
			this.links.put(dataset, map);
		}
		if (this.duplicates) {
			links.forEach(link -> {
				this.links.get(dataset).merge(link, 1, (linkValue, counter) -> {
					if (++counter == Integer.MAX_VALUE) {
						this.printLinks(true);
						counter = 1;
					}
					return counter;
				});
			});
		} else {
			links.forEach(link -> this.links.get(dataset).put(link, 1));
		}

	}

	public void printLinks() {
		printLinks(true);
	}

	public void printLinks(final boolean delete) {
		this.links.forEach((dataset, links) -> {
			if (this.separateFiles) {
				links.forEach((link, counter) -> {
					for (int i = 1; i <= counter; i++) {
						try {
							printLinkSeparateFiles(dataset, link);
						} catch (final DestinationNotOpenableException e) {
							this.logger.error("Could not open destination file for dataset " + dataset + " .");
						}
					}
				});
			} else {
				links.forEach((link, counter) -> {
					for (int i = 1; i <= counter; i++) {
						printLinkSingleFile(dataset, link);
					}
				});
			}

		});
		if (delete) {
			this.links = new HashMap<>();
		}
	}

	public void open() throws DestinationNotOpenableException {
		if (!this.separateFiles) {
			final File auxFile = new File(this.outputFolder.toString() + FileSystems.getDefault().getSeparator() + this.fileName);
			try {
				// auxFile.createNewFile();
				this.writer = new PrintWriter(new FileOutputStream(auxFile, !this.overwrite));
			} catch (final IOException e) {
				this.logger.error("Could not create destination file [" + auxFile.getPath() + "].");
				throw new DestinationNotOpenableException("Could not open destination file [" + auxFile.getPath() + "]", e);
			}
		} else {
			this.writers = new HashMap<>();
		}
	}

	public void close() {
		if (this.separateFiles) {
			this.writers.forEach((dataset, writer) -> writer.close());
		} else {
			this.writer.flush();
			this.writer.close();
		}
	}

	protected void printLinkSingleFile(final String dataset, final String link) {
		this.writer.println(dataset + this.separator + link);
	}

	protected void printLinkSeparateFiles(final String dataset, final String link) throws DestinationNotOpenableException {
		if (!this.writers.containsKey(dataset)) {
			try {
				final File auxFile = new File(this.outputFolder.toString() + FileSystems.getDefault().getSeparator() + dataset.replaceAll(".*?://", ""));
				auxFile.createNewFile();
				this.writers.put(dataset, new PrintWriter(new FileOutputStream(auxFile, !this.overwrite)));
			} catch (final IOException e) {
				this.logger.error("Could not open destination file for dataset " + dataset + " .");
				throw new DestinationNotOpenableException("Could not open destination file for dataset " + dataset, e);
			}
		}
		this.writers.get(dataset).println(dataset + this.separator + link);
	}

}
