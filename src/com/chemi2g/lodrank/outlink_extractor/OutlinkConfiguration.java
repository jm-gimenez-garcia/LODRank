package com.chemi2g.lodrank.outlink_extractor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public final class OutlinkConfiguration {

	private static volatile OutlinkConfiguration	instance;

	public static final String						DEFAULT_CONFIG_FOLDER		= "res";
	public static final String						DEFAULT_OUTPUT_FOLDER		= "output";
	public static final String						PROCESSED_TRIPLES_FILE		= "processedTriples.aux";
	public static final String						PROCESSED_DATASETS_FILE		= "processedDatasets.aux";

	static final int								START_DEFAULT				= 1;
	static final int								STEP_DEFAULT				= 1;
	static final boolean							PROCESS_SUBJECTS_DEFAULT	= true;
	static final boolean							PROCESS_PREDICATES_DEFAULT	= true;
	static final boolean							PROCESS_OBJECTS_DEFAULT		= true;

	JCommander										jc;

	@Parameter(names = { "-h", "--help" }, help = true, hidden = true)
	boolean											help						= false;

	@Parameter(names = { "-a", "--start" }, description = "Line number to start processing")
	int												start						= START_DEFAULT;

	@Parameter(names = { "-e", "--step" }, description = "Number of lines to jump before processing the line")
	int												step						= STEP_DEFAULT;

	@Parameter(names = { "-s", "--ignoreSubjects" }, description = "Ignore subjects of triples")
	boolean											ignoreSubjects				= !PROCESS_SUBJECTS_DEFAULT;

	@Parameter(names = { "-p", "--ignorePredicates" }, description = "Ignore subjects of triples")
	boolean											ignorePredicates			= !PROCESS_PREDICATES_DEFAULT;

	@Parameter(names = { "-o", "--ignoreObjects" }, description = "Ignore subjects of triples")
	boolean											ignoreObjects				= !PROCESS_OBJECTS_DEFAULT;

	private OutlinkConfiguration(String[] args) {
		this.jc = new JCommander(this, args);
		if (this.help) {
			this.jc.usage();
			System.exit(1);
		}
	}

	private OutlinkConfiguration() {
	}

	public static OutlinkConfiguration newInstance(String[] args) {
		synchronized (OutlinkConfiguration.class) {
			instance = new OutlinkConfiguration(args);
		}
		return instance;
	}

	public static OutlinkConfiguration getInstance() {
		if (instance == null) {
			synchronized (OutlinkConfiguration.class) {
				if (instance == null) {
					instance = new OutlinkConfiguration();
				}
			}
		}
		return instance;
	}

	public int getStart() {
		return start;
	}

	public int getStep() {
		return step;
	}

	public boolean processSubjects() {
		return !ignoreSubjects;
	}

	public boolean processPredicates() {
		return !ignorePredicates;
	}

	public boolean processObjects() {
		return !ignoreObjects;
	}

}
