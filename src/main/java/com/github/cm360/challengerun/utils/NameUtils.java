package com.github.cm360.challengerun.utils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class NameUtils {

	/**
	 * Capitalizes the first letter of each word and makes all other characters
	 * lowercase by splitting the input String on spaces
	 * 
	 * Stolen straight from https://www.baeldung.com/java-string-title-case
	 * 
	 * @param text The text to convert to title case
	 * @return The inputted text converted to title case
	 */
	public static String toTitleCase(String text) {
	    if (text == null || text.isEmpty()) {
	        return text;
	    }
	    return Arrays.stream(text.split("_"))
				.map(word -> word.isEmpty() ? word
						: Character.toTitleCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}
	
	public static String enumToTitleCase(Enum<?> enumObj) {
		return toTitleCase(enumObj.name());
	}

}
