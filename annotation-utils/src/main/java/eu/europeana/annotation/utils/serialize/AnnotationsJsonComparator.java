package eu.europeana.annotation.utils.serialize;

import java.util.Comparator;
import java.util.HashMap;

import org.apache.stanbol.commons.jsonld.JsonLdCommon;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
 * A comparator for JSON-LD maps to ensure the order of certain key elements
 * like '#', '@', 'a' in JSON-LD output.
 *
 * @author Jan Roerden
 */
public class AnnotationsJsonComparator implements Comparator<Object> {
    
    public static final String CREATED = "created";
    public static final String CREATOR = "creator";
    public static final String GENERATED = "generated";
    public static final String GENERATOR = "generator";
    public static final String BODY = "body";
    public static final String TARGET = "target";
    public static final String VIA = "via";
	public static final String MOTIVATION = "motivation";

	
	static final HashMap<String, Integer> propOrderMap = new HashMap<String, Integer>();
	static {
		propOrderMap.put(JsonLdCommon.CONTEXT, 10);
		propOrderMap.put(JsonLdCommon._ID, 20);
		propOrderMap.put(JsonLdCommon._TYPE, 30);
		propOrderMap.put(WebAnnotationFields.PART_OF, 32);
		propOrderMap.put(WebAnnotationFields.TOTAL, 34);
		propOrderMap.put(WebAnnotationFields.NEXT, 36);
		propOrderMap.put(WebAnnotationFields.PREV, 38);
		propOrderMap.put(WebAnnotationFields.LAST, 39);		
		propOrderMap.put(MOTIVATION, 40);
		propOrderMap.put(CREATED, 50);
		propOrderMap.put(CREATOR, 60);
		propOrderMap.put(GENERATED, 70);
		propOrderMap.put(GENERATOR, 80);
		propOrderMap.put(BODY, 90);
		propOrderMap.put(JsonLdCommon._VALUE, 100);
		propOrderMap.put(TARGET, 110);
		propOrderMap.put(VIA, 120);
	}

    @Override
    public int compare(Object arg0, Object arg1) {
    	Integer leftOrder = propOrderMap.get(arg0);
    	Integer rightOrder = propOrderMap.get(arg1);
		if(leftOrder == null)
			leftOrder = Math.abs(arg0.hashCode());
    	if(rightOrder == null)
    		rightOrder = Math.abs(arg1.hashCode());
		
    	return Integer.compare(leftOrder, rightOrder);
    }

}
