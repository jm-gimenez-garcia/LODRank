package com.chemi2g.lodrank;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

public class OutlinkWriter implements Runnable {

	static final String	DEFAULT_OUTPUT_FOLDER	= "output";
	static final String	GZIP_EXTENSION			= ".gz";

	String				datasetPLD;
	Set<String>			outlinks;

	public OutlinkWriter(String datasetPLD, Set<String> outlinks) {
		this.datasetPLD = datasetPLD.replace("/", ".");
		this.outlinks = outlinks;
	}

	public void run(String path) {
		Writer writer = null;
		try {
			Date date = new Date();
			if (outlinks.size() != 0) {
				File file = new File(path + "/" + datasetPLD + GZIP_EXTENSION);
				writer = new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(file, true)));
				System.out.println(new Timestamp(date.getTime()) + " Writing dataset " + datasetPLD + " to " + file.getAbsolutePath());
				for (String outlink : outlinks) {
					writer.write(datasetPLD + " " + outlink + "\n");
				}
				System.out.println(new Timestamp(date.getTime()) + " Finished writing dataset. Outlinks written: " + outlinks.size());
			} else {
				System.out.println(new Timestamp(date.getTime()) + " No outlinks for dataset " + datasetPLD);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void run() {
		run(DEFAULT_OUTPUT_FOLDER);
	}
}
