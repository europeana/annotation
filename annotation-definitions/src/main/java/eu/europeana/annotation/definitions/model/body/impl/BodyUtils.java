package eu.europeana.annotation.definitions.model.body.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class BodyUtils {
	
	/**
	 * This method extracts euType from the input string with multiple types.
	 * The syntax of the euType is as follows: "euType:<Annotation Part>#<Object Type>"
	 * e.g. BODY#SEMANTIC_TAG
	 * @param typesString
	 * @return
	 */
	public String getEuTypeFromBodyType(String bodyType) {
		String res = "";
		if (!bodyType.isEmpty()) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.EU_TYPE + ":(.*?)]");
			Matcher matcher = pattern.matcher(bodyType);
			if (matcher.find()) {
			    res = matcher.group(1);
			}		
		}
		return res;
	}

}
