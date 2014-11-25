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
package eu.europeana.annotation.jsonld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonLdPropertyValue {

    private Object value;
    private String type;
    private String language;
    
    private Map<String,String> values = new HashMap<String,String>();
    private List<String> types = new ArrayList<String>();
    private Map<String,JsonLdProperty> propertyMap = new HashMap<String,JsonLdProperty>();
    
	public JsonLdPropertyValue() {
        
    }
    
    public JsonLdPropertyValue(Object value) {
        if (value instanceof JsonLdIRI) {
            JsonLdIRI iriValue = (JsonLdIRI) value;
            this.value = iriValue.getIRI();
            this.type = JsonLdCommon.ID;
        }
        else {
            this.value = value;
        }
    }

    public Object getValue() {
        return value;
    }

    public String getLiteralValue() {
        return String.valueOf(value);
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

	public Map<String,String> getValues() {
		return values;
	}

	public void setValues(Map<String,String> values) {
		this.values = values;
	}

    public void addType(String type) {
        types.add(type);
    }

    public void addAllTypes(List<String> types) {
        this.types.addAll(types);
    }
    
    public List<String> getTypes() {
        return types;
    }
	
    public void putProperty(JsonLdProperty property) {
        this.propertyMap.put(property.getName(), property);
    }

    public JsonLdProperty getPropertyValueIgnoreCase(String property) {
        for (String p : this.propertyMap.keySet()) {
            if (p.equalsIgnoreCase(property)) {
                return this.propertyMap.get(p);
            }
        }
        return null;
    }
    
    public JsonLdProperty getProperty(String property) {
        return this.propertyMap.get(property);
    }
        
    public Map<String,JsonLdProperty> getPropertyMap() {
        return this.propertyMap;
    }

}
