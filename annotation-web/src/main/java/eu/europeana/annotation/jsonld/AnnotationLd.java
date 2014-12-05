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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;

/**
 * The AnnotationLd class provides an API to create a JSON-LD object structure for Annotation
 * and to serialize this structure. It is designed for Annotation interface.
 * 
 * <p>
 * This implementation is based on the JSON-LD 1.0 specification editor's draft from
 * January 5, 2013. Available online at <a href="http://json-ld.org/spec">http://json-ld.org/spec</a>.
 */
public class AnnotationLd extends JsonLd {
    
    private static final Logger logger = LoggerFactory.getLogger(AnnotationLd.class);
   
    
    /**
     * @param annotation
     */
    public AnnotationLd(Annotation annotation) {
    	setAnnotation(annotation);
    }
    
	/**
     * Adds the given annotation to this JsonLd object using the resource's subject as key. If the key is NULL
     * and there does not exist a resource with an empty String as key the resource will be added using
     * an empty String ("") as key. 
     * 
     * @param annotation
     */
    public void setAnnotation(Annotation annotation) {
                
    	setUseTypeCoercion(false);
        setUseCuries(true);
        addNamespacePrefix("http://www.w3.org/ns/oa-context-20130208.json", "oa");

        JsonLdResource jsonLdResource = new JsonLdResource();
        jsonLdResource.setSubject("");
        jsonLdResource.addType(SolrAnnotationConst.ANNOTATION_LD_TYPE);
        
        if (!StringUtils.isBlank(annotation.getType())) 
        	jsonLdResource.putProperty(SolrAnnotationConst.TYPE, annotation.getType());   
       	jsonLdResource.putProperty(addSerializedByProperty(annotation));        
        if (annotation.getAnnotatedAt() != null) 
        	jsonLdResource.putProperty(SolrAnnotationConst.ANNOTATED_AT, convertDateToStr(annotation.getAnnotatedAt()));
        jsonLdResource.putProperty(addAnnotatedByProperty(annotation));                
        if (annotation.getSerializedAt() != null) 
        	jsonLdResource.putProperty(SolrAnnotationConst.SERIALIZED_AT, convertDateToStr(annotation.getSerializedAt()));
        if (!StringUtils.isBlank(annotation.getMotivatedBy())) 
        	jsonLdResource.putProperty(SolrAnnotationConst.MOTIVATED_BY, annotation.getMotivatedBy());
        jsonLdResource.putProperty(addStyledByProperty(annotation));                
        jsonLdResource.putProperty(addBodyProperty(annotation));
        jsonLdResource.putProperty(addTargetProperty(annotation));
        
        put(jsonLdResource);
    }

    public static Date convertStrToDate(String str) {
    	Date res = null; 
    	DateFormat formatter = new SimpleDateFormat(SolrAnnotationConst.DATE_FORMAT);
    	try {
			res = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return res;
    }
    
    public static String convertDateToStr(Date date) {
    	String res = "";    	
    	DateFormat df = new SimpleDateFormat(SolrAnnotationConst.DATE_FORMAT);
    	res = df.format(date);    	
    	return res;
    }
    
	private JsonLdProperty addTargetProperty(Annotation annotation) {
		JsonLdProperty targetProperty = new JsonLdProperty(SolrAnnotationConst.TARGET);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getTarget() != null) {
        	if (!StringUtils.isBlank(annotation.getTarget().getTargetType())) 
        		propertyValue.addType(annotation.getTarget().getTargetType());
	
	        JsonLdProperty sourceProperty = new JsonLdProperty(SolrAnnotationConst.SOURCE);
	        JsonLdPropertyValue propertyValue2 = new JsonLdPropertyValue();
	        
        	if (annotation.getTarget().getSource() != null) { 
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getContentType())) 
            		propertyValue2.setType(annotation.getTarget().getSource().getContentType());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getHttpUri())) 
            		propertyValue2.getValues().put(SolrAnnotationConst.SID, annotation.getTarget().getSource().getHttpUri());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getMediaType())) 
            		propertyValue2.getValues().put(SolrAnnotationConst.FORMAT, annotation.getTarget().getSource().getMediaType());
		        sourceProperty.addValue(propertyValue2);        
		        propertyValue.putProperty(sourceProperty);
        	}
	        
	        JsonLdProperty selectorProperty = new JsonLdProperty(SolrAnnotationConst.SELECTOR);
	        JsonLdPropertyValue propertyValue3 = new JsonLdPropertyValue();
	        propertyValue3.setType(""); // if property is empty - set empty type
	        
	        selectorProperty.addValue(propertyValue3);        
	        propertyValue.putProperty(selectorProperty);
	        
	        targetProperty.addValue(propertyValue);
        }
		return targetProperty;
	}

	private JsonLdProperty addBodyProperty(Annotation annotation) {
		JsonLdProperty bodyProperty = new JsonLdProperty(SolrAnnotationConst.BODY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getBody() != null) {
            if (!StringUtils.isBlank(annotation.getBody().getBodyType())) {        	
				String bodyTypes = annotation.getBody().getBodyType();
				String commaSeparatedTypes = bodyTypes.substring(1, bodyTypes.length() - 1);
		        String[] tokens = commaSeparatedTypes.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
		        for(String t : tokens) {
					propertyValue.addType(t);
		        }
        	}
	
            if (!StringUtils.isBlank(annotation.getBody().getValue()))         	
            	propertyValue.getValues().put(SolrAnnotationConst.CHARS, annotation.getBody().getValue());
            if (!StringUtils.isBlank(annotation.getBody().getLanguage()))         	
            	propertyValue.getValues().put(SolrAnnotationConst.DC_LANGUAGE, annotation.getBody().getLanguage());
            if (!StringUtils.isBlank(annotation.getBody().getContentType()))         	
            	propertyValue.getValues().put(SolrAnnotationConst.FORMAT, annotation.getBody().getContentType());
	        bodyProperty.addValue(propertyValue);        
	
	        JsonLdPropertyValue JsonLdPropertyValue2 = new JsonLdPropertyValue();
	        
            if (!StringUtils.isBlank(annotation.getBody().getMediaType()))         	
            	JsonLdPropertyValue2.addType(annotation.getBody().getMediaType());
	        JsonLdPropertyValue2.getValues().put(SolrAnnotationConst.FOAF_PAGE, annotation.getBody().getHttpUri());
	        bodyProperty.addValue(JsonLdPropertyValue2);
        }
		return bodyProperty;
	}

	private JsonLdProperty addSerializedByProperty(Annotation annotation) {
		JsonLdProperty serializedByProperty = new JsonLdProperty(SolrAnnotationConst.SERIALIZED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getSerializedBy();        
        if (agent != null && !StringUtils.isBlank(agent.getAgentType().name())) 
        	propertyValue.setType(agent.getAgentType().name());
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(SolrAnnotationConst.NAME, agent.getName());
        if (agent != null && !StringUtils.isBlank(agent.getHomepage())) 
        	propertyValue.getValues().put(SolrAnnotationConst.FOAF_HOMEPAGE, agent.getHomepage());
        serializedByProperty.addValue(propertyValue);
		return serializedByProperty;
	}

	private JsonLdProperty addAnnotatedByProperty(Annotation annotation) {
		JsonLdProperty annotatedByProperty = new JsonLdProperty(SolrAnnotationConst.ANNOTATED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getAnnotatedBy();      
        propertyValue.setType("http://xmlns.com/foaf/0.1/person");
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(SolrAnnotationConst.NAME, agent.getName());
        annotatedByProperty.addValue(propertyValue);
		return annotatedByProperty;
	}

	private JsonLdProperty addStyledByProperty(Annotation annotation) {
		JsonLdProperty styledByProperty = new JsonLdProperty(SolrAnnotationConst.STYLED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Style style = annotation.getStyledBy();        
        if (style != null && !StringUtils.isBlank(style.getMediaType())) 
        	propertyValue.setType(style.getMediaType());
        if (style != null && !StringUtils.isBlank(style.getContentType())) 
        	propertyValue.getValues().put(SolrAnnotationConst.STYLE_CLASS, style.getContentType());
        if (style != null && !StringUtils.isBlank(style.getValue())) 
        	propertyValue.getValues().put(SolrAnnotationConst.SOURCE, style.getValue());
        styledByProperty.addValue(propertyValue);
		return styledByProperty;
	}
    
    public static void toConsole(String description, String actual) {
    	logger.info(description);
    	logger.info(actual);
        String s = actual;
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replace("\"", "\\\"");
        s = s.replace("\n", "\\n");
        logger.info(s);
    }
}
