/**
 *
 */
package eu.wdaqua.lodrank.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

import org.apache.jena.riot.Lang;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.wdaqua.lodrank.exception.SourceNotOpenableException;

/**
 * @author José M. Giménez-García
 *
 */
public class Source {
	protected final Optional<File>			file;
	protected final Optional<URL>			url;
	protected final Optional<InputStream>	inputStream;

	Logger									logger;

	public static Source getSource(final String text) throws SourceNotOpenableException {
		Source source = null;
		switch (Source.Input.fromInput(text)) {
			case FILE:
				LogManager.getLogger(Source.class).debug("Source is of type " + Input.FILE.getLabel());
				source = new Source(new File(text));
				break;
			case URL:
				LogManager.getLogger(Source.class).debug("Source is of type " + Input.URL.getLabel());
				try {
					source = new Source(new URL(text));
				} catch (final MalformedURLException e) {
					throw new SourceNotOpenableException();
				}
				break;
			case STANDARD_INPUT:
				LogManager.getLogger(Source.class).debug("Source is of type " + Input.STANDARD_INPUT.getLabel());
				source = new Source(System.in);
				break;
		}
		return source;
	}

	public Source(final File file) {
		this(Optional.of(file), Optional.empty(), Optional.empty());
	}

	public Source(final URL url) {
		this(Optional.empty(), Optional.of(url), Optional.empty());
	}

	public Source(final InputStream stream) {
		this(Optional.empty(), Optional.empty(), Optional.of(stream));
	}

	protected Source(final Optional<File> optionalFile, final Optional<URL> optionalURL, final Optional<InputStream> optionalStream) {
		this.logger = LogManager.getLogger(getClass());
		this.file = optionalFile;
		this.url = optionalURL;
		this.inputStream = optionalStream;
	}

	@Override
	public String toString() {
		String toString = "NOT INITIALIZED";
		if (isFile()) {
			toString = Input.FILE.getLabel() + ":" + asFile().getAbsolutePath();
		} else if (isURL()) {
			toString = Input.URL.getLabel() + ":" + asURL().toString();
		} else if (isInputStream()) {
			toString = Input.STANDARD_INPUT.getLabel();
		}
		return toString;
	}

	public InputStream getInputStream() throws SourceNotOpenableException {
		InputStream inputStream = null;
		if (isFile()) {
			try {
				try {
					inputStream = new GZIPInputStream(new FileInputStream(asFile()));
					this.logger.debug("Returning input stream from gzipped file " + toString() + ".");
				} catch (final IOException e) {
					inputStream = new FileInputStream(asFile());
					this.logger.debug("Returning input stream from file " + toString() + ".");
				}
			} catch (final FileNotFoundException e) {
				this.logger.error("File [" + asFile().getPath() + "] not found.");
				throw new SourceNotOpenableException("File [" + asFile().getAbsolutePath() + "] not found", e);
			}
		} else if (isURL()) {
			try {
				try {
					inputStream = new GZIPInputStream(asURL().openStream());
					this.logger.debug("Returning input stream from gzipped URL " + toString() + ".");
				} catch (final IOException e) {
					inputStream = asURL().openStream();
					this.logger.debug("Returning input stream from URL " + toString() + ".");
				}
			} catch (final IOException e) {
				this.logger.error("Error accessing the URL [" + asURL().toString() + "]");
				throw new SourceNotOpenableException("Error accessing the URL [" + asURL().toString() + "]", e);
			}
		} else if (isInputStream()) {
			inputStream = asInputStream();
			this.logger.debug("Returning input stream from input stream " + toString() + ".");
		} else {
			throw new SourceNotOpenableException("Error accessing the source");
		}
		return new CleanInputStream(inputStream);
	}

	public boolean isGZipped() {
		boolean isGZipped = true;
		if (isFile()) {
			try {
				final GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(asFile()));
				gzipInputStream.close();
			} catch (final IOException e) {
				this.logger.debug("File is not gzipped");
				isGZipped = false;
			}
		} else if (isURL()) {
			try {
				final GZIPInputStream gzipInputStream = new GZIPInputStream(asURL().openStream());
				gzipInputStream.close();
			} catch (final IOException e) {
				this.logger.debug("URL is not gzipped");
				isGZipped = false;
			}
		} else {
			try {
				final GZIPInputStream gzipInputStream = new GZIPInputStream(asInputStream());
				gzipInputStream.close();
			} catch (final IOException e) {
				this.logger.debug("Input stream is not gzipped");
				isGZipped = false;
			}
		}
		return isGZipped;
	}

	public boolean isFile() {
		boolean isFile = false;
		if (this.file.isPresent()) {
			isFile = true;
		}
		return isFile;
	}

	public boolean isURL() {
		boolean isURL = false;
		if (this.url.isPresent()) {
			isURL = true;
		}
		return isURL;
	}

	public boolean isInputStream() {
		boolean isInputStream = false;
		if (this.inputStream.isPresent()) {
			isInputStream = true;
		}
		return isInputStream;
	}

	public File asFile() {
		return this.file.get();
	}

	public URL asURL() {
		return this.url.get();
	}

	public InputStream asInputStream() {
		return this.inputStream.get();
	}

	public Optional<File> asOptionalFile() {
		return this.file;
	}

	public Optional<URL> asOptionalURL() {
		return this.url;
	}

	public Optional<InputStream> asOptionalInputStream() {
		return this.inputStream;
	}

	public enum Input {
		FILE("file"), URL("url"), STANDARD_INPUT("sysin");

		private String label;

		public static Input fromString(final String possibleLabel) {
			Input returnInput = null;
			if (possibleLabel != null) {
				for (final Input input : Input.values()) {
					if (possibleLabel.equalsIgnoreCase(input.label)) {
						returnInput = input;
					}
				}
			}
			return returnInput;
		}

		public static Input fromInput(final String text) {
			Input returnInput = FILE;
			try {
				new java.net.URL(text);
				returnInput = URL;
			} catch (final MalformedURLException e) {
				LogManager.getLogger(Input.class).info("Source is not URL. Assuming that it is a file.");
			}
			return returnInput;
		}

		Input(final CharSequence label) {
			this.label = label.toString();
		}

		public String getLabel() {
			return this.label;
		}
	}

	public enum Format {

		TRIPLES, QUADS;

		public static Format getFormat(final Lang lang) {
			final Format format;
			if ((lang.hashCode() == Lang.NQUADS.hashCode()) || (lang.hashCode() == Lang.NQ.hashCode())) {
				format = QUADS;
			} else {
				format = TRIPLES;
			}
			return format;
		}
	}

}
