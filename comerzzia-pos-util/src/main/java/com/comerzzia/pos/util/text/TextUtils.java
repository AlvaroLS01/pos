package com.comerzzia.pos.util.text;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class TextUtils {

	private static TextUtils instance;

	public static TextUtils getInstance() {
		if (instance == null) {
			instance = new TextUtils();
		}
		return instance;
	}

	public static void setCustomInstance(TextUtils instance) {
		TextUtils.instance = instance;
	}

	/**
	 * Split text into lines based on the maximum number of characters per line.
	 * 
	 * @param text - Text which will be splitted.
	 * @param maxCharactersPerLine - Maximum number of characters per line.
	 */
	public List<String> split(String text, int maxCharactersPerLine) {
		return split(text, maxCharactersPerLine, "");
	}
	
	/**
	 * Split text into lines based on the maximum number of characters per line.
	 * 
	 * @param text - Text which will be splitted.
	 * @param maxCharactersPerLine - Maximum number of characters per line.
	 * @param separator - Regular expression used for initial splitting
	 */
	public List<String> split(String text, int maxCharactersPerLine, String separator) {
		List<String> lines = new ArrayList<String>();

		if (StringUtils.isBlank(text)) {
			return lines;
		}

		if (StringUtils.isBlank(separator)) {
			separator = System.lineSeparator();
		}

		if (separator.equals("|")) {
			separator = "\\|";
		}

		String[] inputLines = text.split(separator);
		for (int i = 0; i < inputLines.length; i++) {
			String inputLine = inputLines[i];
			if (inputLine.length() <= maxCharactersPerLine) {
				lines.add(inputLine);
			}
			else {
				String[] lineWords = inputLine.split(" ");
				String line = "";
				for (int j = 0; j < lineWords.length; j++) {
					String word = lineWords[j];

					if (line.length() + 1 + word.length() < maxCharactersPerLine) {
						if (StringUtils.isNotBlank(line)) {
							line = line + " " + word;
						}
						else {
							line = word;
						}
					}
					else {
						lines.add(line);
						line = word;
					}
					if (j == lineWords.length - 1) {
						lines.add(line);
					}
				}
			}
		}

		return lines;
	}

}
