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
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.apache.stanbol.commons.jsonld.JsonLdProperty;
import org.apache.stanbol.commons.jsonld.JsonLdPropertyValue;
import org.apache.stanbol.commons.jsonld.JsonLdResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.SelectorObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.StyleObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;

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
   
    TypeUtils typeHelper = new TypeUtils();
			
    public TypeUtils getTypeHelper() {
		return typeHelper;
	}

	/**
     * @param annotation
     */
    public AnnotationLd(Annotation annotation) {
    	setAnnotation(annotation);
    }
    
    /**
     * @param jsonLd
     */
    public AnnotationLd(JsonLd jsonLd) {
    	setJsonLd(jsonLd);
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
        if (!StringUtils.isNotBlank(annotation.getType())) {
        	jsonLdResource.addType(annotation.getType());
        } else {
        	jsonLdResource.addType(WebAnnotationFields.ANNOTATION_LD_TYPE);
        }
        
        if (annotation.getAnnotationId() != null && !StringUtils.isBlank(annotation.getAnnotationId().toString())) 
        	jsonLdResource.putProperty(WebAnnotationFields.SID, annotation.getAnnotationId().toString());   
        if (!StringUtils.isBlank(annotation.getType())) 
        	jsonLdResource.putProperty(WebAnnotationFields.TYPE, annotation.getType());   
       	jsonLdResource.putProperty(addSerializedByProperty(annotation));        
        if (annotation.getAnnotatedAt() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.ANNOTATED_AT, convertDateToStr(annotation.getAnnotatedAt()));
        jsonLdResource.putProperty(addAnnotatedByProperty(annotation));                
        if (annotation.getSerializedAt() != null) 
        	jsonLdResource.putProperty(WebAnnotationFields.SERIALIZED_AT, convertDateToStr(annotation.getSerializedAt()));
        if (!StringUtils.isBlank(annotation.getMotivatedBy())) 
        	jsonLdResource.putProperty(WebAnnotationFields.MOTIVATED_BY, annotation.getMotivatedBy());
        jsonLdResource.putProperty(addStyledByProperty(annotation));                
        jsonLdResource.putProperty(addBodyProperty(annotation));
        jsonLdResource.putProperty(addTargetProperty(annotation));
        
        put(jsonLdResource);
    }

    public String parseByKey(String key, String term) {
    	String res = "";
    	if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(term)
    			&& term.contains(key)) {
//    		int indexKey = term.indexOf(key);
    		String patternRegex = key + "\":\"\\w+\"";
    		Pattern pattern = Pattern.compile(patternRegex);
    	    // in case you would like to ignore case sensitivity,
    	    // you could use this statement:
    	    // Pattern pattern = Pattern.compile("\\s+", Pattern.CASE_INSENSITIVE);
    	    Matcher matcher = pattern.matcher(term);
    	    // check all occurance
    	    while (matcher.find()) {
    	      System.out.print("Start index: " + matcher.start());
    	      System.out.print(" End index: " + matcher.end() + " ");
    	      System.out.println(matcher.group());
    	    }
//    		List<String> termList = Arrays.asList(term.split("\""));
    		res = "";
    	}
    	return res;
    }
    
    /**
     * This method converts AnnotationLd to Annotation object.
     * @return Annotation object
     */
    @SuppressWarnings("rawtypes")
	public Annotation getAnnotation() {
		
		Annotation annotation = AnnotationObjectFactory.getInstance().createModelObjectInstance(
				AnnotationTypes.OBJECT_TAG.name());
    	
//    	ModelObjectFactory objectFactory = new ModelObjectFactory();
//    	BaseObjectTag annotation = (BaseObjectTag) objectFactory.createModelObjectInstance(
//    			AnnotationPartTypes.ANNOTATION.name() + WebAnnotationFields.SPLITTER + AnnotationTypes.OBJECT_TAG.name());    	
    	JsonLdResource resource = getResource("");
    	Iterator<?> it = resource.getPropertyMap().entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry)it.next();
		    String key = pairs.getKey().toString();
		    Object mapValue = pairs.getValue();
		    switch (key) {
		    case WebAnnotationFields.SID:
		    	String annotationIdValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(annotationIdValue)) 
					((BaseObjectTag) annotation).calculateAnnotationIdByString(annotationIdValue);
		    	break;
		    case WebAnnotationFields.TYPE:
		    	String typeValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(typeValue)) 
					annotation.setType(typeValue);
		    	break;
		    case WebAnnotationFields.ANNOTATED_AT:
		    	String annotatedAtValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(annotatedAtValue)) 
					annotation.setAnnotatedAt(AnnotationLd.convertStrToDate(annotatedAtValue));
		    	break;
		    case WebAnnotationFields.SERIALIZED_AT:
		    	String serializedAtValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(serializedAtValue)) 
					annotation.setSerializedAt(AnnotationLd.convertStrToDate(serializedAtValue));
		    	break;
		    case WebAnnotationFields.MOTIVATED_BY:
		    	String motivatedByValue = getLiteralPropertyValue(mapValue);
				if (!StringUtils.isBlank(motivatedByValue)) 
					annotation.setMotivatedBy(motivatedByValue);
		    	break;
		    case WebAnnotationFields.BODY:
		    	Body body = getBody(mapValue);
			    annotation.setBody(body);
		    	break;
		    case WebAnnotationFields.TARGET:	    	
				Target target = getTarget(mapValue);						
			    annotation.setTarget(target);
		    	break;
		    case WebAnnotationFields.SERIALIZED_BY:
				Agent serializedBy = getSerializedBy(mapValue);
				annotation.setSerializedBy(serializedBy);
		    	break;
		    case WebAnnotationFields.ANNOTATED_BY:
				Agent annotatedBy = getAnnotatedBy(mapValue);
				annotation.setAnnotatedBy(annotatedBy);
		    	break;
		    case WebAnnotationFields.STYLED_BY:
		    	Style style = getStyledBy(mapValue);
				annotation.setStyledBy(style);
		    	break;
		    default:
		    	break;
		    }		    
		}		
		return annotation;
    }

	private String getLiteralPropertyValue(Object mapValue) {
		String propValue = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);					
			if (!StringUtils.isBlank(propertyValue.getLiteralValue())) {
				propValue = propertyValue.getLiteralValue();
			}	
		}
		return propValue;
	}

	/**
	 * This method retrieves Agent object for serializedBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Agent object
	 */
	private Agent getSerializedBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Agent agent = getAgentByProperty(property);
		return agent;
	}

	private Agent getAgentByProperty(JsonLdProperty property) {
		
		Agent agent = null;  
//				objectFactory.createModelObjectInstance(
//				AnnotationPartTypes.AGENT.name() + WebAnnotationFields.SPLITTER + AgentTypes.SOFTWARE_AGENT.name());
		//ModelObjectFactory objectFactory = new ModelObjectFactory();
		
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			String euType = extractEuType(propertyValue);
			//if not set 
			if (StringUtils.isBlank(euType))
				throw new AnnotationAttributeInstantiationException(euType);
			
			agent = AgentObjectFactory.getInstance().createModelObjectInstance(euType);
			if (hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				agent.setAgentType(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));			
			if (hasValue(propertyValue, WebAnnotationFields.SID)) 
				agent.setOpenId(propertyValue.getValues().get(WebAnnotationFields.SID));
			if (hasValue(propertyValue, WebAnnotationFields.NAME)) 
				agent.setName(propertyValue.getValues().get(WebAnnotationFields.NAME));
			if (hasValue(propertyValue, WebAnnotationFields.FOAF_HOMEPAGE)) 
				agent.setHomepage(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE));
		}
		return agent;
	}

	protected boolean hasValue(JsonLdPropertyValue propertyValue, String fieldName) {
		return !StringUtils.isBlank(propertyValue.getValues().get(fieldName));
	}

	private String extractEuType(JsonLdPropertyValue propertyValue) {
		return extractEuType(propertyValue, WebAnnotationFields.AT_TYPE);
	}

	private String extractEuType(JsonLdPropertyValue propertyValue, String fieldName) {
		String typeArray = propertyValue.getValues().get(fieldName);
		String euType = getTypeHelper().getEuTypeFromTypeArray(typeArray);
		return euType;
	}

	/**
	 * This method retrieves Agent object for annotatedBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Agent object
	 */
	private Agent getAnnotatedBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		return getAgentByProperty(property);
	}

	/**
	 * This method retrieves Style object for styledBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Style object
	 */
	private Style getStyledBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Style style = null;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			String euType = "";
			if (!StringUtils.isBlank(propertyValue.getType())) 
				euType = getTypeHelper().getEuTypeFromTypeArray(propertyValue.getType());
//				euType = propertyValue.getType();
			if (StringUtils.isBlank(euType) && hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				euType = getTypeHelper().getEuTypeFromTypeArray(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));
			//if not set 
			if (StringUtils.isBlank(euType))
				throw new AnnotationAttributeInstantiationException(euType);
			
			style = StyleObjectFactory.getInstance().createModelObjectInstance(euType);
			
			if (!StringUtils.isBlank(propertyValue.getType())) 
				style.setHttpUri(propertyValue.getType());
			if (StringUtils.isBlank(propertyValue.getType()) && hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) 
				style.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));
			if (hasValue(propertyValue, WebAnnotationFields.STYLE_CLASS)) 
				style.setContentType(propertyValue.getValues().get(WebAnnotationFields.STYLE_CLASS));
			if (hasValue(propertyValue, WebAnnotationFields.SOURCE)) 
				style.setValue(propertyValue.getValues().get(WebAnnotationFields.SOURCE));
		}
		return style;
	}

	/**
	 * This method retrieves Target object from AnnotationLd object.
	 * @param mapValue
	 * @return Target object
	 */
	private Target getTarget(Object mapValue) {
		Target target = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			if (hasValue(propertyValue, WebAnnotationFields.TARGET_TYPE)) {
				String euType = extractEuType(propertyValue, WebAnnotationFields.TARGET_TYPE);			
				//if not set 
				if (StringUtils.isBlank(euType))
					throw new AnnotationAttributeInstantiationException(euType);
				
				target = TargetObjectFactory.getInstance().createModelObjectInstance(euType);
//				target = (Target) objectFactory.createModelObjectInstance(
//						AnnotationPartTypes.TARGET.name() + WebAnnotationFields.SPLITTER + euType);
				target.setTargetType(propertyValue.getValues().get(WebAnnotationFields.TARGET_TYPE));
				if (hasValue(propertyValue, WebAnnotationFields.CONTENT_TYPE)) 
					target.setContentType(propertyValue.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (hasValue(propertyValue, WebAnnotationFields.HTTP_URI)) 
					target.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.HTTP_URI));
				
				JsonLdProperty sourceProperty = propertyValue.getProperty(WebAnnotationFields.SOURCE);
				JsonLdPropertyValue propertyValue2 = (JsonLdPropertyValue) sourceProperty.getValues().get(0);
				InternetResource source = new BaseInternetResource();
				if (hasValue(propertyValue2, WebAnnotationFields.CONTENT_TYPE)) 
					source.setContentType(propertyValue2.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (hasValue(propertyValue2, WebAnnotationFields.SID)) 
					source.setHttpUri(propertyValue2.getValues().get(WebAnnotationFields.SID));
				if (hasValue(propertyValue2, WebAnnotationFields.FORMAT)) 
					source.setMediaType(propertyValue2.getValues().get(WebAnnotationFields.FORMAT));
				target.setSource(source);
				
				Selector selector = SelectorObjectFactory.getInstance().createModelObjectInstance(
						SelectorTypes.SVG_RECTANGLE_SELECTOR.name());
//				Rectangle selector = new SvgRectangleSelector();
				target.setSelector((Selector)selector);
			}
		}
		return target;
	}

	/**
	 * This method retrieves Body object from AnnotationLd object.
	 * @param mapValue
	 * @return Body object
	 */
	private Body getBody(Object mapValue) {
		Body body = null;
		JsonLdProperty property = (JsonLdProperty) mapValue;
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			if (hasValue(propertyValue, WebAnnotationFields.AT_TYPE)) {
				String euType = extractEuType(propertyValue);

				//if not set 
				if (StringUtils.isBlank(euType))
					throw new AnnotationAttributeInstantiationException(euType);
				
				body = BodyObjectFactory.getInstance().createModelObjectInstance(euType);
				
//				body = (Body) objectFactory.createModelObjectInstance(
//						AnnotationPartTypes.BODY.name() + WebAnnotationFields.SPLITTER + euType);
//				
				body.setBodyType(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));

				if (hasValue(propertyValue, WebAnnotationFields.CHARS)) 
					body.setValue(propertyValue.getValues().get(WebAnnotationFields.CHARS));
				if (hasValue(propertyValue, WebAnnotationFields.DC_LANGUAGE)) 
					body.setLanguage(propertyValue.getValues().get(WebAnnotationFields.DC_LANGUAGE));
				if (hasValue(propertyValue, WebAnnotationFields.FORMAT)) 
					body.setContentType(propertyValue.getValues().get(WebAnnotationFields.FORMAT));
				if (hasValue(propertyValue, WebAnnotationFields.FOAF_PAGE)) 
					body.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.FOAF_PAGE));
			}
		}
		return body;
	}
    
	/**
     * Adds the values from the passed JsonLd object. 
     * 
     * @param jsonLd
     */
	public void setJsonLd(JsonLd jsonLd) {
             
    	setUseTypeCoercion(false);
        setUseCuries(true);
        
        if (jsonLd != null) {
    		if (jsonLd.getNamespacePrefixMap() != null) {
    	    	setNamespacePrefixMap(jsonLd.getNamespacePrefixMap());
    		}
    		if (jsonLd.getUsedNamespaces() != null) {
    			setUsedNamespaces(jsonLd.getUsedNamespaces());
    		}
    		if (jsonLd.getResourceSubjects() != null) {
    			Iterator<String> itr = jsonLd.getResourceSubjects().iterator();
    			while (itr.hasNext()) {
    				String resourceName = itr.next();
    				if (resourceName != null) {
    					put(jsonLd.getResource(resourceName));
    				}
    			}
    		}
   			setUseJointGraphs(jsonLd.isUseJointGraphs());
   			setUseTypeCoercion(jsonLd.isUseTypeCoercion());
   			setUseCuries(jsonLd.isUseCuries());
   			setApplyNamespaces(jsonLd.isApplyNamespaces());
    	}    	
    }
    
    /**
     * This method takes passed serialised AnnotationLd GSON string and
     * passes it to the parser. 
     * @param serializedAnnotationLd
     * @return Annotation object
     */
    public static Annotation deserialise(String serialisedAnnotationLd) {
    	Annotation res = null;
        Gson gson = new Gson();
        AnnotationLd annotationLdDeserialisedObject = gson.fromJson(serialisedAnnotationLd, AnnotationLd.class);
        String annotationLdDeserialisedString = annotationLdDeserialisedObject.toString();
        AnnotationLd.toConsole("deserialise: ", annotationLdDeserialisedString);
        try {
			JsonLd deserialisedJsonLd = JsonLdParser.parseExt(annotationLdDeserialisedString);
			res = AnnotationLd.getAnnotationFromJsonLd(deserialisedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return res;
    }
    
    /**
     * This method converts deserialised JsonLd to Annotation object.
     * @param deserialisedJsonLd
     * @return Annotation object
     */
    public static Annotation getAnnotationFromJsonLd(JsonLd deserialisedJsonLd) {
    	Annotation res = null;
    	AnnotationLd annotationLd = new AnnotationLd(deserialisedJsonLd);
    	res = annotationLd.getAnnotation();
    	return res;
    }
    
    public static Date convertStrToDate(String str) {
    	Date res = null; 
    	DateFormat formatter = new SimpleDateFormat(WebAnnotationFields.DATE_FORMAT);
    	try {
			res = formatter.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return res;
    }
    
    public static String convertDateToStr(Date date) {
    	String res = "";    	
    	DateFormat df = new SimpleDateFormat(WebAnnotationFields.DATE_FORMAT);
    	res = df.format(date);    	
    	return res;
    }
    
	private JsonLdProperty addTargetProperty(Annotation annotation) {
		JsonLdProperty targetProperty = new JsonLdProperty(WebAnnotationFields.TARGET);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getTarget() != null) {
        	if (!StringUtils.isBlank(annotation.getTarget().getTargetType())) 
//        		propertyValue.addType(annotation.getTarget().getTargetType());
    			propertyValue.addType(annotation.getTarget().getTargetType().replace("[", "").replace("]", ""));

            if (!StringUtils.isBlank(annotation.getTarget().getTargetType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.TARGET_TYPE, annotation.getTarget().getTargetType());
            if (!StringUtils.isBlank(annotation.getTarget().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getContentType());
            if (!StringUtils.isBlank(annotation.getTarget().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.HTTP_URI, annotation.getTarget().getHttpUri());
        	
	        JsonLdProperty sourceProperty = new JsonLdProperty(WebAnnotationFields.SOURCE);
	        JsonLdPropertyValue propertyValue2 = new JsonLdPropertyValue();
//	        JsonLdPropertyValue propertyValue21 = new JsonLdPropertyValue();
//	        Map<String, String> valueMap = new HashMap<String, String>();
	        
        	if (annotation.getTarget().getSource() != null) { 
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getContentType())) 
            		propertyValue2.getValues().put(WebAnnotationFields.CONTENT_TYPE, annotation.getTarget().getSource().getContentType());
//            		propertyValue2.setType(annotation.getTarget().getSource().getContentType());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getHttpUri())) 
            		propertyValue2.getValues().put(WebAnnotationFields.SID, annotation.getTarget().getSource().getHttpUri());
//            		valueMap.put(WebAnnotationFields.SID, annotation.getTarget().getSource().getHttpUri());
            	if (!StringUtils.isBlank(annotation.getTarget().getSource().getMediaType())) 
//            		valueMap.put(WebAnnotationFields.FORMAT, annotation.getTarget().getSource().getMediaType());
//            		propertyValue21.getValues().put(WebAnnotationFields.FORMAT, annotation.getTarget().getSource().getMediaType());
            		propertyValue2.getValues().put(WebAnnotationFields.FORMAT, annotation.getTarget().getSource().getMediaType());
//        		propertyValue2.getValues().putAll(valueMap);
//            	propertyValue2.setValues(valueMap); 
//            	propertyValue2.setValue(valueMap); 
            	sourceProperty.addValue(propertyValue2);        
//            	sourceProperty.addValue(propertyValue21);        
		        propertyValue.putProperty(sourceProperty);
        	}
	        
	        JsonLdProperty selectorProperty = new JsonLdProperty(WebAnnotationFields.SELECTOR);
	        JsonLdPropertyValue propertyValue3 = new JsonLdPropertyValue();
	        propertyValue3.setType(""); // if property is empty - set empty type
	        
	        selectorProperty.addValue(propertyValue3);        
	        propertyValue.putProperty(selectorProperty);
	        
	        targetProperty.addValue(propertyValue);
        }
		return targetProperty;
	}
    
	private JsonLdProperty addBodyProperty(Annotation annotation) {
		JsonLdProperty bodyProperty = new JsonLdProperty(WebAnnotationFields.BODY);
		JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        
        if (annotation != null && annotation.getBody() != null) {	
            if (!StringUtils.isBlank(annotation.getBody().getBodyType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.AT_TYPE, annotation.getBody().getBodyType());
            if (!StringUtils.isBlank(annotation.getBody().getValue()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CHARS, annotation.getBody().getValue());
            if (!StringUtils.isBlank(annotation.getBody().getLanguage()))         	
            	propertyValue.getValues().put(WebAnnotationFields.DC_LANGUAGE, annotation.getBody().getLanguage());
            if (!StringUtils.isBlank(annotation.getBody().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FORMAT, annotation.getBody().getContentType());	
            if (!StringUtils.isBlank(annotation.getBody().getHttpUri()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FOAF_PAGE, annotation.getBody().getHttpUri());
	        bodyProperty.addValue(propertyValue);        
        }
		return bodyProperty;
	}

	private JsonLdProperty addSerializedByProperty(Annotation annotation) {
		JsonLdProperty serializedByProperty = new JsonLdProperty(WebAnnotationFields.SERIALIZED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getSerializedBy();        
        addAgentByProperty(propertyValue, agent);
        serializedByProperty.addValue(propertyValue);
		return serializedByProperty;
	}

	private void addAgentByProperty(JsonLdPropertyValue propertyValue,
			Agent agent) {
		if (agent != null && !StringUtils.isBlank(agent.getOpenId())) 
        	propertyValue.getValues().put(WebAnnotationFields.SID, agent.getOpenId());
//        if (agent != null && !StringUtils.isBlank(agent.getAgentType())) 
//        	propertyValue.setType(agent.getAgentType());
        if (agent != null && !StringUtils.isBlank(agent.getAgentType())) 
        	propertyValue.getValues().put(WebAnnotationFields.AT_TYPE, agent.getAgentType());
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        if (agent != null && !StringUtils.isBlank(agent.getHomepage())) 
        	propertyValue.getValues().put(WebAnnotationFields.FOAF_HOMEPAGE, agent.getHomepage());
	}

	private JsonLdProperty addAnnotatedByProperty(Annotation annotation) {
		JsonLdProperty annotatedByProperty = new JsonLdProperty(WebAnnotationFields.ANNOTATED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getAnnotatedBy();      
//        if (agent != null && !StringUtils.isBlank(agent.getAgentType())) 
//        	propertyValue.setType(agent.getAgentType());
////        propertyValue.setType("http://xmlns.com/foaf/0.1/person");
//        if (agent != null && !StringUtils.isBlank(agent.getName())) 
//        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        addAgentByProperty(propertyValue, agent);
        annotatedByProperty.addValue(propertyValue);
		return annotatedByProperty;
	}

	private JsonLdProperty addStyledByProperty(Annotation annotation) {
		JsonLdProperty styledByProperty = new JsonLdProperty(WebAnnotationFields.STYLED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Style style = annotation.getStyledBy();        
        if (style != null && !StringUtils.isBlank(style.getHttpUri())) 
        	propertyValue.setType(style.getHttpUri());
//        if (style != null && !StringUtils.isBlank(style.getMediaType())) 
//        	propertyValue.setType(style.getMediaType());
        if (style != null && !StringUtils.isBlank(style.getContentType())) 
        	propertyValue.getValues().put(WebAnnotationFields.STYLE_CLASS, style.getContentType());
        if (style != null && !StringUtils.isBlank(style.getValue())) 
        	propertyValue.getValues().put(WebAnnotationFields.SOURCE, style.getValue());
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
