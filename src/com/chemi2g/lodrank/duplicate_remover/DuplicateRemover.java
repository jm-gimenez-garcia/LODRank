package com.chemi2g.lodrank.duplicate_remover;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;

public class DuplicateRemover {

	HashSet<String> dictionary;

	public static void main(String[] args) {
		DuplicateRemover duplicateRemover = new DuplicateRemover();
		duplicateRemover.readDictionary(args[0]);
		duplicateRemover.run();
	}

	void readDictionary(String file) {
		BufferedReader reader = null;
		String line;
		try {
			reader = new BufferedReader(new FileReader(file));
			dictionary = new HashSet<>();
			while ((line = reader.readLine()) != null) {
				dictionary.add(line);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void run() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				String[] datasets = line.split(" ");
				if (dictionary.contains(datasets[1])) {
					writer.write(line + "\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
