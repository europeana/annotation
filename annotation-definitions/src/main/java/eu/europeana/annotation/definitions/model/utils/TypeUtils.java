package eu.europeana.annotation.definitions.model.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;

public class TypeUtils {
	
	/**
	 * This method extracts euType from the input string with multiple types.
	 * The syntax of the euType is as follows: "euType:<Annotation Part>#<Object Type>"
	 * e.g. BODY#SEMANTIC_TAG
	 * @param typesString
	 * @return
	 */
	public String getEuTypeFromTypeArray(String typeArray) {
		String res = "";
		if (StringUtils.isNotEmpty(typeArray)) {
			Pattern pattern = Pattern.compile(WebAnnotationFields.EU_TYPE + ":(.*?)]");
			Matcher matcher = pattern.matcher(typeArray);
			if (matcher.find()) {
			    res = matcher.group(1);
			}		
		}
		return res;
	}
	
	/**
	 * This method removes tabs for Web Service parameters.
	 * @param value
	 * @return value without tabs
	 */
	public String removeTabs(String value) {
		return value.replaceAll("\t", "");
	}

}
