/**
 *
 */
package eu.wdaqua.lodrank;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import eu.wdaqua.lodrank.exception.DestinationNotOpenableException;
import eu.wdaqua.lodrank.exception.SourceNotOpenableException;
import eu.wdaqua.lodrank.linkextractor.LinkExtractor;
import eu.wdaqua.lodrank.linkwriter.LinkWriter;
import eu.wdaqua.lodrank.listprocessor.ListProcessor;
import eu.wdaqua.lodrank.loader.DictionaryLoader;
import eu.wdaqua.lodrank.loader.ListLoader;
import eu.wdaqua.lodrank.rdfprocessor.RDFProcessor;
import eu.wdaqua.lodrank.urlprocessor.URLProcessor;
import eu.wdaqua.lodrank.urlprocessor.URLtoDictionaryEntry;
import eu.wdaqua.lodrank.urlprocessor.URLtoPLD;

/**
 * @author José M. Giménez-García
 *
 */
public class LODRank {

	// Configuration file to use if non is given by input parameter
	protected static final String	DEFAULT_CONFIGURATION_FILE		= "./configuration.xml";

	// Parameter names in configuration file
	protected static final String	NAME_LANG						= "lang.value";
	protected static final String	NAME_FORCE_LANG					= "lang.force";
	protected static final String	NAME_INPUT_FILE					= "input.file";
	protected static final String	NAME_INPUT_URL					= "input.URL";
	protected static final String	NAME_START						= "input.start";
	protected static final String	NAME_STEP						= "input.step";
	protected static final String	NAME_INPUT_SEPARATOR			= "input.separator";
	protected static final String	NAME_PROCESS_SUBJECTS			= "process.subjects";
	protected static final String	NAME_PROCESS_PREDICATES			= "process.predicates";
	protected static final String	NAME_PROCESS_OBJECTS			= "process.objects";
	protected static final String	NAME_PROCESS_DUPLICATES			= "process.duplicates";
	protected static final String	NAME_DICTIONARY_FILE			= "dictionary.file";
	protected static final String	NAME_DICTIONARY_URL				= "dictionary.url";
	protected static final String	NAME_DICTIONARY_SEPARATOR		= "dictionary.separator";
	protected static final String	NAME_EXCLUSIONS					= "exclusions";
	protected static final String	NAME_OUTPUT_FOLDER				= "output.folder";
	protected static final String	NAME_OUTPUT_FILE				= "output.file";

	// Properties without parameter in configuration file
	protected static final String	NAME_EXCLUSION					= NAME_EXCLUSIONS + ".exclusion";
	protected static final String	NAME_ROLE_TO_EXCLUDE			= NAME_EXCLUSION + ".role_to_exclude";
	protected static final String	NAME_ROLE_TO_CHECK				= NAME_EXCLUSION + ".role_to_check";
	protected static final String	NAME_CONDITION					= NAME_EXCLUSION + ".condition";
	protected static final String	NAME_VALUE						= NAME_EXCLUSION + ".value";

	// Default values if neither input parameter or configuration file properties are given
	protected static final String	DEFAULT_LANG					= Lang.NTRIPLES.getLabel();
	protected static final boolean	DEFAULT_FORCE_LANG				= false;
	protected static final String	DEFAULT_INPUT_FILE				= null;
	protected static final String	DEFAULT_INPUT_URL				= null;
	protected static final int		DEFAULT_START					= 1;
	protected static final int		DEFAULT_STEP					= 1;
	protected static final String	DEFAULT_INPUT_SEPARATOR			= " ";
	protected static final boolean	DEFAULT_PROCESS_SUBJECTS		= true;
	protected static final boolean	DEFAULT_PROCESS_PREDICATES		= true;
	protected static final boolean	DEFAULT_PROCESS_OBJECTS			= true;
	protected static final boolean	DEFAULT_PROCESS_DUPLICATES		= false;
	protected static final String	DEFAULT_DICTIONARY_FILE			= null;
	protected static final String	DEFAULT_DICTIONARY_URL			= null;
	protected static final String	DEFAULT_DICTIONARY_SEPARATOR	= ",";
	protected static final String	DEFAULT_OUTPUT_FOLDER			= "output";
	protected static final String	DEFAULT_OUTPUT_FILE				= "links.csv";

	protected Logger				logger;
	protected JCommander			jc;
	protected Configuration			conf;

	protected Lang					lang;

	// JCommander parameters
	@Parameter(names = { "-h", "--help" }, help = true, hidden = true)
	protected boolean				help							= false;
	@Parameter(names = { "-l", "--lang" }, description = "RDF encoding language. N-TRIPLES by default.")
	protected String				pLang							= null;
	@Parameter(names = { "-fl", "--forcelanguage" }, description = "If false, RDF language will be inferred from RDF source name and use --lang as fallback. If true --lang will be used for every RDF source")
	protected Boolean				pForceLang						= null;
	@Parameter(names = { "-if", "--inputfile" }, description = "Source file with list of datasets. Standard input if neither file nor URL are given.")
	protected String				pInputFile						= null;
	@Parameter(names = { "-iu", "--inputurl" }, description = "Source URL with list of datasets. Standard input if neither URL nor file are given.")
	protected String				pInputURL						= null;
	@Parameter(names = { "-oo", "--outputfolder" }, description = "Output folder. \"output\" by default")
	protected String				pOutputFolder					= null;
	@Parameter(names = { "-oi", "--outputfile" }, description = "Output file. \"links.csv\" by default")
	protected String				pOutputFile						= null;
	@Parameter(names = { "-c", "--configuration" }, description = "Configuration file; configuration.xml by default.")
	protected String				pConfigurationFile				= null;
	@Parameter(names = { "-a", "--start" }, description = "Line number to start processing; 1 by default.")
	protected Integer				pStart							= null;
	@Parameter(names = { "-e", "--step" }, description = "Number of lines to jump before processing the line; 1 by default.")
	protected Integer				pStep							= null;
	@Parameter(names = { "-is", "--inputseparator" }, description = "Separator of source and dataset name in input file. Space by default.")
	protected String				pInputSeparator					= null;
	@Parameter(names = { "-ps", "--processSubjects" }, description = "Process subjects of rdfIterator; True by default", arity = 1)
	protected Boolean				pProcessSubjects				= null;
	@Parameter(names = { "-pp", "--processPredicates" }, description = "Process predicates of rdfIterator.", arity = 1)
	protected Boolean				pProcessPredicates				= null;
	@Parameter(names = { "-po", "--processObjects" }, description = "Process objects of rdfIterator.", arity = 1)
	protected Boolean				pProcessObjects					= null;
	@Parameter(names = { "-df", "--dictionaryfile" }, description = "Dictionary file. PLD if neither file nor URL are given.")
	protected String				pDictionaryFile					= null;
	@Parameter(names = { "-du", "--dictionaryurl" }, description = "Dictionary URL. PLD if neither URL nor file are given.")
	protected String				pDictionaryURL					= null;
	@Parameter(names = { "-ds", "--dictoinaryseparator" }, description = "Separator of URL and dataset name in dictoinary file. Comma by default.")
	protected String				pDictionarySeparator			= null;
	@Parameter(names = { "-d", "--duplicates" }, description = "Allow to extract duplicate links. False by default.", arity = 1)
	protected Boolean				pDuplicates						= null;

	public static void main(final String[] args) throws SourceNotOpenableException, ConfigurationException, DestinationNotOpenableException {
		final LODRank lodRank = new LODRank(args);
		lodRank.run();
	}

	protected LODRank(final String[] args) throws ConfigurationException {
		this.logger = LogManager.getLogger(getClass());
		this.conf = new Configurations().xml(getConfigurationFile());

		// Validate parameters
		this.jc = new JCommander(this, args);
		if (getInputFile() != null && getInputURL() != null) {
			this.logger.error("It is not possible to specify input file and input URL at the same time.");
			System.exit(1);
		} else if (getDictionaryFile() != null && getDictionaryURL() != null) {
			this.logger.error("It is not possible to specify dictionary file and dictionary URL at the same time.");
			System.exit(1);
		} else if (this.help) {
			this.jc.usage();
			System.exit(0);
		}

		// Initializations
		this.lang = RDFLanguages.nameToLang(getLang());
	}

	public void run() throws SourceNotOpenableException, DestinationNotOpenableException {

		this.logger.debug("Creating URLProcessor");
		final URLProcessor urlProcessor;
		if (getDictionaryFile() == null && getDictionaryURL() == null) {
			this.logger.info("Dictionary not provided, will use PLD to identify dataset.");
			urlProcessor = new URLtoPLD();
		} else {
			urlProcessor = new URLtoDictionaryEntry();
			final DictionaryLoader dictionaryLoader = new DictionaryLoader();
			dictionaryLoader.setSeparator(getDictionarySeparator());
			if (getDictionaryFile() != null) {
				this.logger.info("Reading dictionary from file [" + getDictionaryFile() + "].");
				dictionaryLoader.attachSource(new Source(new File(getDictionaryFile())));
			} else {
				this.logger.info("Reading dictionary from URL [" + getDictionaryURL() + "].");
				try {
					dictionaryLoader.attachSource(new Source(new URL(getDictionaryURL())));
				} catch (final MalformedURLException e) {
					this.logger.error(getDictionaryURL() + " is not a valid URL.");
					dictionaryLoader.close();
					throw new SourceNotOpenableException(getDictionaryURL() + " is not a valid URL.", e);
				}
			}
			((URLtoDictionaryEntry) urlProcessor).loadDictionary(dictionaryLoader);
		}

		this.logger.debug("Creating LinkExtractor.");
		final LinkExtractor linkExtractor = new LinkExtractor();
		if (this.conf.containsKey(NAME_EXCLUSIONS)) {
			this.logger.info("Reading list of exclusions from configuration file");
			final List<String> roles_to_exclude = this.conf.getList(String.class, NAME_ROLE_TO_EXCLUDE);
			final List<String> roles_to_check = this.conf.getList(String.class, NAME_ROLE_TO_CHECK);
			final List<String> conditions = this.conf.getList(String.class, NAME_CONDITION);
			final List<String> values = this.conf.getList(String.class, NAME_VALUE);
			for (int i = 0; i < roles_to_exclude.size(); i++) {
				linkExtractor.addExclusion(roles_to_exclude.get(i), roles_to_check.get(i), conditions.get(i), values.get(i));
			}
		}
		linkExtractor.setUrlProcessor(urlProcessor);

		this.logger.debug("Creating LinkWriter");
		final LinkWriter linkWriter = new LinkWriter();
		this.logger.info("Setting duplicates to " + getDuplicates());
		linkWriter.setDuplicates(getDuplicates());
		this.logger.info("Setting output folder to " + getOutputFolder());
		linkWriter.setOutputFolder(new File(getOutputFolder()));
		this.logger.info("Setting output file to " + getOutputFile());
		linkWriter.setFileName(getOutputFile());
		this.logger.debug("Setting overwrite to " + false);
		linkWriter.setOverwrite(false);

		this.logger.debug("Creating RDFProcessor");
		final RDFProcessor rdfProcessor = new RDFProcessor();
		rdfProcessor.setLinkExtractor(linkExtractor);
		rdfProcessor.setLinkWriter(linkWriter);

		this.logger.debug("Creating ListProcessor.");
		final ListProcessor listProcessor = new ListProcessor();
		final ListLoader listLoader = new ListLoader();
		listProcessor.setLang(this.lang);
		if (getInputFile() == null && getInputURL() == null) {
			this.logger.info("List of sources not provided. Using standard input.");
			listLoader.attachSource(new Source(System.in));
		} else {
			if (getInputFile() != null) {
				this.logger.info("Reading input from file [" + getInputFile() + "].");
				listLoader.attachSource(new Source(new File(getInputFile())));
			} else {
				this.logger.info("Reading input from URL [" + getInputURL() + "].");
				try {
					listLoader.attachSource(new Source(new URL(getInputURL())));
				} catch (final MalformedURLException e) {
					this.logger.error(getInputURL() + " is not a valid URL.");
					listLoader.close();
					throw new SourceNotOpenableException(getInputURL() + " is not a valid URL.", e);
				}
			}
		}
		this.logger.info("Setting start at line " + getStart() + ".");
		listLoader.setStart(getStart());
		this.logger.info("Setting step for every " + getstep() + " lines.");
		listLoader.setStep(getstep());
		this.logger.info("Setting [" + "] as separator.");
		listLoader.setSeparator(getInputSeparator());
		this.logger.debug("Attaching ListLoader to ListProcessor");
		listProcessor.setListLoader(listLoader);
		this.logger.info("Setting Force Lang to " + getForceLang());
		listProcessor.setForceLang(getForceLang());
		listProcessor.setRDFProcessor(rdfProcessor);

		this.logger.info("Starting process...");
		listProcessor.run();
		this.logger.info("Process finished.");
	}

	/*
	 * From here methods to access the parameters
	 */

	protected String getConfigurationFile() {
		return this.pConfigurationFile != null ? this.pConfigurationFile : DEFAULT_CONFIGURATION_FILE;
	}

	protected String getLang() {
		return get(this.pLang, NAME_LANG, DEFAULT_LANG);
	}

	protected boolean getForceLang() {
		return get(this.pForceLang, NAME_FORCE_LANG, DEFAULT_FORCE_LANG);
	}

	protected String getInputFile() {
		return get(this.pInputFile, NAME_INPUT_FILE, DEFAULT_INPUT_FILE);
	}

	protected String getInputURL() {
		return get(this.pInputURL, NAME_INPUT_URL, DEFAULT_INPUT_URL);
	}

	protected int getStart() {
		return get(this.pStart, NAME_START, DEFAULT_START);
	}

	protected int getstep() {
		return get(this.pStep, NAME_STEP, DEFAULT_STEP);
	}

	protected String getInputSeparator() {
		return get(this.pInputSeparator, NAME_INPUT_SEPARATOR, DEFAULT_INPUT_SEPARATOR);
	}

	protected boolean getProcessSubjects() {
		return get(this.pProcessSubjects, NAME_PROCESS_SUBJECTS, DEFAULT_PROCESS_SUBJECTS);
	}

	protected boolean getProcessPredicates() {
		return get(this.pProcessPredicates, NAME_PROCESS_PREDICATES, DEFAULT_PROCESS_PREDICATES);
	}

	protected boolean getProcessObjects() {
		return get(this.pProcessObjects, NAME_PROCESS_OBJECTS, DEFAULT_PROCESS_OBJECTS);
	}

	protected String getDictionaryFile() {
		return get(this.pDictionaryFile, NAME_DICTIONARY_FILE, DEFAULT_DICTIONARY_FILE);
	}

	protected String getDictionaryURL() {
		return get(this.pDictionaryURL, NAME_DICTIONARY_URL, DEFAULT_DICTIONARY_URL);
	}

	protected String getDictionarySeparator() {
		return get(this.pDictionarySeparator, NAME_DICTIONARY_SEPARATOR, DEFAULT_DICTIONARY_SEPARATOR);
	}

	protected boolean getDuplicates() {
		return get(this.pDuplicates, NAME_PROCESS_DUPLICATES, DEFAULT_PROCESS_DUPLICATES);
	}

	protected String getOutputFolder() {
		return get(this.pOutputFolder, NAME_OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
	}

	protected String getOutputFile() {
		return get(this.pOutputFile, NAME_OUTPUT_FILE, DEFAULT_OUTPUT_FILE);
	}

	/*
	 * From here helper methods to access parameters
	 */

	protected String get(final String paramValue, final String confName, final String defaultValue) {
		return paramValue != null ? paramValue : this.conf.getString(confName, defaultValue);
	}

	protected int get(final Integer paramValue, final String confName, final int defaultValue) {
		return paramValue != null ? paramValue : this.conf.getInt(confName, defaultValue);
	}

	protected boolean get(final Boolean paramValue, final String confName, final boolean defaultValue) {
		return paramValue != null ? paramValue : this.conf.getBoolean(confName, defaultValue);
	}

}
