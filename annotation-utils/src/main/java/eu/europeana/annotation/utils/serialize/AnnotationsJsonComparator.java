/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package eu.europeana.annotation.utils.serialize;

import java.util.Comparator;
import java.util.HashMap;

import org.apache.stanbol.commons.jsonld.JsonLdCommon;

/**
 * This class is specific to annotations must be moved to (europeana) annotation repository
 * A comparator for JSON-LD maps to ensure the order of certain key elements
 * like '#', '@', 'a' in JSON-LD output.
 *
 * @author Jan Roerden
 */
@Deprecated
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
