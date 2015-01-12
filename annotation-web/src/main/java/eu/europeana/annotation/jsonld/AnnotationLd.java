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
import java.util.List;
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

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.ModelObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseObjectTag;
import eu.europeana.annotation.definitions.model.resource.InternetResource;
import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.resource.selector.Rectangle;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.selector.impl.SvgRectangleSelector;
import eu.europeana.annotation.definitions.model.resource.style.Style;
import eu.europeana.annotation.definitions.model.resource.style.impl.BaseStyle;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationPartTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;

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
    
    /*public Annotation getAnnotationByJson() {
//    	BaseObjectTag annotation = new BaseObjectTag();
    	ModelObjectFactory objectFactory = new ModelObjectFactory();
    	String euTypeAnnotation = "ANNOTATION";
    	BaseObjectTag annotation = null;
    	if (AnnotationPartTypes.contains(euTypeAnnotation)) {
    		annotation = (BaseObjectTag) objectFactory.createModelObjectInstance(euTypeAnnotation + 
    					      WebAnnotationFields.SPLITTER + AnnotationTypes.OBJECT_TAG.name());
	    	if (this.isUseJointGraphs()) {
	    		JSONObject json = getAnnotationJson(); 
	    		try {
					//JSONObject body = json.getJSONObject(WebAnnotationFields.BODY);
	//				Body body = (Body) json.get("body");
	    			@SuppressWarnings("unchecked")
					List<TreeMap<String, String>> body = (List<TreeMap<String, String>>) json.get(WebAnnotationFields.BODY);
					////String bodyStr = (String) body.get(1);
					TreeMap<String, String> bodyTree = body.get(1);
					
					String mediaType = bodyTree.get(WebAnnotationFields.MEDIA_TYPE);
					String httpUri = bodyTree.get(WebAnnotationFields.FOAF_PAGE);
	
	//				String mediaType = parseByKey(WebAnnotationFields.AT_TYPE, bodyStr);
	//				String httpUri = parseByKey(WebAnnotationFields.FOAF_PAGE, bodyStr);
	
			    	String euTypeBody = "BODY";
			    	TagBody bodyObject = null;
			    	if (AnnotationPartTypes.contains(euTypeBody)) {
			    		bodyObject = (TagBody) objectFactory.createModelObjectInstance(euTypeBody + 
			    					WebAnnotationFields.SPLITTER + BodyTypes.SEMANTIC_TAG.name());
					
	//				JSONObject styleJson = new JSONObject(bodyStr);
	//				String mediaType = (String) styleJson.get(WebAnnotationFields.AT_TYPE);
				    	annotation.setBody(bodyObject);
						if (StringUtils.isNotBlank(mediaType)) {
							annotation.getBody().setMediaType(mediaType);
						}
		//				String httpUri = (String) styleJson.get(WebAnnotationFields.FOAF_PAGE);
						if (StringUtils.isNotBlank(httpUri)) {
							annotation.getBody().setHttpUri(httpUri);
						}
					//				JSONObject body = json.getJSONObject("@context");
			    	}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	    	}
    	}
		return annotation;
    }*/
    
//    @SuppressWarnings("rawtypes")
//	public Annotation getAnnotationExt() {
//    	BaseObjectTag annotation = new BaseObjectTag();
//    	
//    	if (this.isUseJointGraphs()) {
//            Map<String,Object> jsonMap = getJsonMap();
//        	Iterator<?> it = jsonMap.entrySet().iterator();
//    		while (it.hasNext()) {
//    		    Map.Entry pairs = (Map.Entry)it.next();
//    		    String key = pairs.getKey().toString();
//    		    Object mapValue = pairs.getValue();
//    		    switch (key) {
//    		    case WebAnnotationFields.TYPE:
//    		    	String typeValue = (String) mapValue;
//    				if (!StringUtils.isBlank(typeValue)) 
//    					annotation.setType(typeValue);
//    		    	break;
//    		    case WebAnnotationFields.ANNOTATED_AT:
//    		    	String annotatedAtValue = (String) mapValue;
//    				if (!StringUtils.isBlank(annotatedAtValue)) 
//    					annotation.setAnnotatedAt(AnnotationLd.convertStrToDate(annotatedAtValue));
//    		    	break;
//    		    case WebAnnotationFields.SERIALIZED_AT:
//    		    	String serializedAtValue = (String) mapValue;
//    				if (!StringUtils.isBlank(serializedAtValue)) 
//    					annotation.setSerializedAt(AnnotationLd.convertStrToDate(serializedAtValue));
//    		    	break;
//    		    case WebAnnotationFields.MOTIVATED_BY:
//    		    	String motivatedByValue = (String) mapValue;
//    				if (!StringUtils.isBlank(motivatedByValue)) 
//    					annotation.setMotivatedBy(motivatedByValue);
//    		    	break;
//    		    case WebAnnotationFields.BODY:
//    		    	Body body = getBody(mapValue);
//    			    annotation.setBody(body);
//    		    	break;
//    		    case WebAnnotationFields.TARGET:	    	
//    				Target target = getTarget(mapValue);						
//    			    annotation.setTarget(target);
//    		    	break;
//    		    case WebAnnotationFields.SERIALIZED_BY:
//    				Agent serializedBy = getSerializedBy(mapValue);
//    				annotation.setSerializedBy(serializedBy);
//    		    	break;
//    		    case WebAnnotationFields.ANNOTATED_BY:
//    				Agent annotatedBy = getAnnotatedBy(mapValue);
//    				annotation.setAnnotatedBy(annotatedBy);
//    		    	break;
//    		    case WebAnnotationFields.STYLED_BY:
//    		    	Style style = getStyledBy(mapValue);
//    				annotation.setStyledBy(style);
//    		    	break;
//    		    default:
//    		    	break;
//    		    }		    
//    		}		
//        }
//		return annotation;
//    }

//    public Body getBodyByJson() {
//    	TagBody bodyObject = null;
//		if (this.isUseJointGraphs()) {
//			JSONObject json = getAnnotationJson(); 
//			try {
//				@SuppressWarnings("unchecked")
//				List<TreeMap<String, String>> body = (List<TreeMap<String, String>>) json.get(WebAnnotationFields.BODY);
////				TreeMap<String, String> bodyTreeFirst = body.get(0);	
//				TreeMap<String, String> bodyTree = body.get(1);
//				
//				String mediaType = bodyTree.get(WebAnnotationFields.AT_TYPE);
//				String httpUri = bodyTree.get(WebAnnotationFields.FOAF_PAGE);
//	
//		    	String euTypeBody = "BODY";
//		    	if (AnnotationPartTypes.contains(euTypeBody)) {
//		        	ModelObjectFactory objectFactory = new ModelObjectFactory();
//		    		bodyObject = (TagBody) objectFactory.createModelObjectInstance(euTypeBody + 
//		    					WebAnnotationFields.SPLITTER + BodyTypes.SEMANTIC_TAG.name());
//				
//					if (StringUtils.isNotBlank(mediaType)) {
//						bodyObject.setMediaType(mediaType);
//					}
//					if (StringUtils.isNotBlank(httpUri)) {
//						bodyObject.setHttpUri(httpUri);
//					}
//		    	}
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
//		return bodyObject;
//    }
    
    /*public Target getTargetByJson() {
    	ImageTarget targetObject = null;
		if (this.isUseJointGraphs()) {
			JSONObject json = getAnnotationJson(); 
			try {
				@SuppressWarnings("unchecked")
				TreeMap<String, String> target = (TreeMap<String, String>) json.get(WebAnnotationFields.TARGET);
//				String source = target.get(WebAnnotationFields.SOURCE);
//				TreeMap<String, String> source = (TreeMap<String, String>) target.get(WebAnnotationFields.SOURCE);
//				List<TreeMap<String, String>> target = (List<TreeMap<String, String>>) json.get(WebAnnotationFields.TARGET);
//				TreeMap<String, String> targetTree = target.get(1);
//				
//				String contentType = targetTree.get(WebAnnotationFields.AT_TYPE);
//				String httpUri = targetTree.get(WebAnnotationFields.FOAF_PAGE);
//	
//		    	String euType = "TARGET";
//		    	if (AnnotationPartTypes.contains(euType)) {
//		        	ModelObjectFactory objectFactory = new ModelObjectFactory();
//		    		targetObject = (ImageTarget) objectFactory.createModelObjectInstance(euType + 
//		    					WebAnnotationFields.SPLITTER + TargetTypes.IMAGE.name());
//				
//					if (StringUtils.isNotBlank(contentType)) {
//						targetObject.setContentType(contentType);
//					}
//					if (StringUtils.isNotBlank(httpUri)) {
//						targetObject.setHttpUri(httpUri);
//					}
//		    	}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return targetObject;
    }*/
    
    /**
     * This method converts AnnotationLd to Annotation object.
     * @return Annotation object
     */
    @SuppressWarnings("rawtypes")
	public Annotation getAnnotation() {
		
    	ModelObjectFactory objectFactory = new ModelObjectFactory();
    	BaseObjectTag annotation = (BaseObjectTag) objectFactory.createModelObjectInstance(
    			AnnotationPartTypes.ANNOTATION.name() + WebAnnotationFields.SPLITTER + AnnotationTypes.OBJECT_TAG.name());    	
    	JsonLdResource resource = getResource("");
    	Iterator<?> it = resource.getPropertyMap().entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pairs = (Map.Entry)it.next();
		    String key = pairs.getKey().toString();
		    Object mapValue = pairs.getValue();
		    switch (key) {
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
		Agent agent = new SoftwareAgent();
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			if (!StringUtils.isBlank(propertyValue.getType())) {
				agent.setAgentType(propertyValue.getType());
			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.NAME))) {
				agent.setName(propertyValue.getValues().get(WebAnnotationFields.NAME));
			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE))) {
				agent.setHomepage(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE));
			}
		}
		return agent;
	}

	/**
	 * This method retrieves Agent object for annotatedBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Agent object
	 */
	private Agent getAnnotatedBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Agent agent = new SoftwareAgent();
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.NAME))) {
				agent.setName(propertyValue.getValues().get(WebAnnotationFields.NAME));
			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE))) {
				agent.setHomepage(propertyValue.getValues().get(WebAnnotationFields.FOAF_HOMEPAGE));
			}
		}
		return agent;
	}

	/**
	 * This method retrieves Style object for styledBy field from AnnotationLd object.
	 * @param mapValue
	 * @return Style object
	 */
	private Style getStyledBy(Object mapValue) {
		JsonLdProperty property = (JsonLdProperty) mapValue;
		Style style = new BaseStyle();
		if (property.getValues() != null && property.getValues().size() > 0) {
			JsonLdPropertyValue propertyValue = (JsonLdPropertyValue) property.getValues().get(0);
			
//			if (!StringUtils.isBlank(propertyValue.getType())) {
//				style.setMediaType(propertyValue.getType());
//			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE))) {
				style.setMediaType(propertyValue.getValues().get(WebAnnotationFields.AT_TYPE));
			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.STYLE_CLASS))) {
				style.setContentType(propertyValue.getValues().get(WebAnnotationFields.STYLE_CLASS));
			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.SOURCE))) {
				style.setValue(propertyValue.getValues().get(WebAnnotationFields.SOURCE));
			}
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
			
			/*String typeStr = getTypeStringFromValueTypes(propertyValue);
			if (!StringUtils.isBlank(typeStr)) {
				target.setTargetType(typeStr);
			}*/
//			if (!StringUtils.isBlank(propertyValue.getType())) {
//				target.setTargetType(propertyValue.getType());
//			}

			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.TARGET_TYPE))) {
				ModelObjectFactory objectFactory = new ModelObjectFactory();
				String euType = ModelObjectFactory.extractEuType(
						propertyValue.getValues().get(WebAnnotationFields.TARGET_TYPE));
				target = (Target) objectFactory.createModelObjectInstance(euType);
				target.setTargetType(propertyValue.getValues().get(WebAnnotationFields.TARGET_TYPE));
				if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.CONTENT_TYPE))) 
					target.setContentType(propertyValue.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.HTTP_URI))) 
					target.setHttpUri(propertyValue.getValues().get(WebAnnotationFields.HTTP_URI));
				
				///ImageTarget secondTargetPart = (ImageTarget) getTargetByJson();
	//			if (!StringUtils.isBlank(secondTargetPart.getContentType())) {
	//				target.setContentType(secondTargetPart.getContentType());
	//			}
	//			if (!StringUtils.isBlank(secondTargetPart.getHttpUri())) {
	//				target.setHttpUri(secondTargetPart.getHttpUri());
	//			}
	
				JsonLdProperty sourceProperty = propertyValue.getProperty(WebAnnotationFields.SOURCE);
				JsonLdPropertyValue propertyValue2 = (JsonLdPropertyValue) sourceProperty.getValues().get(0);
				InternetResource source = new BaseInternetResource();
	//			if (!StringUtils.isBlank(propertyValue2.getType())) 
	//				source.setContentType(propertyValue2.getType());
				if (!StringUtils.isBlank(propertyValue2.getValues().get(WebAnnotationFields.CONTENT_TYPE))) 
					source.setContentType(propertyValue2.getValues().get(WebAnnotationFields.CONTENT_TYPE));
				if (!StringUtils.isBlank(propertyValue2.getValues().get(WebAnnotationFields.SID))) 
					source.setHttpUri(propertyValue2.getValues().get(WebAnnotationFields.SID));
				if (!StringUtils.isBlank(propertyValue2.getValues().get(WebAnnotationFields.FORMAT))) 
					source.setMediaType(propertyValue2.getValues().get(WebAnnotationFields.FORMAT));
				target.setSource(source);
	//			if (!StringUtils.isBlank(source.getHttpUri())) {
	//				target.setHttpUri(source.getHttpUri());
	//			}
	//			if (!StringUtils.isBlank(source.getContentType())) {
	//				target.setContentType(source.getContentType());
	//			}
	//			if (!StringUtils.isBlank(source.getMediaType())) {
	//				target.setMediaType(source.getMediaType());
	//			}
				
				Rectangle selector = new SvgRectangleSelector();
		//		JsonLdProperty selectorProperty = propertyValue.getProperty(WebAnnotationFields.SELECTOR);
		//		JsonLdPropertyValue propertyValue3 = (JsonLdPropertyValue) selectorProperty.getValues().get(0);
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
			
//			String typeStr = getTypeStringFromValueTypes(propertyValue);
//			if (!StringUtils.isBlank(typeStr)) {
//				body.setBodyType(typeStr);
//			}
			if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.BODY_TYPE))) {
				ModelObjectFactory objectFactory = new ModelObjectFactory();
				String euType = ModelObjectFactory.extractEuType(
						propertyValue.getValues().get(WebAnnotationFields.BODY_TYPE));
				body = (Body) objectFactory.createModelObjectInstance(euType);
				
				body.setBodyType(propertyValue.getValues().get(WebAnnotationFields.BODY_TYPE));

				if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.CHARS))) {
					body.setValue(propertyValue.getValues().get(WebAnnotationFields.CHARS));
				}
				if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.DC_LANGUAGE))) {
					body.setLanguage(propertyValue.getValues().get(WebAnnotationFields.DC_LANGUAGE));
				}
				if (!StringUtils.isBlank(propertyValue.getValues().get(WebAnnotationFields.FORMAT))) {
					body.setContentType(propertyValue.getValues().get(WebAnnotationFields.FORMAT));
				}
				
				/*TagBody secondBodyPart = (TagBody) getBodyByJson();
				if (!StringUtils.isBlank(secondBodyPart.getMediaType())) {
					body.setMediaType(secondBodyPart.getMediaType());
				}
				if (!StringUtils.isBlank(secondBodyPart.getHttpUri())) {
					body.setHttpUri(secondBodyPart.getHttpUri());
				}*/
				JsonLdPropertyValue propertyValue2 = (JsonLdPropertyValue) property.getValues().get(1);
				///String typeStr2 = getTypeStringFromValueTypes(propertyValue2);
	//			if (!StringUtils.isBlank(typeStr2)) {
	//				body.setMediaType(typeStr2);
	//			}
				if (!StringUtils.isBlank(propertyValue2.getValues().get(WebAnnotationFields.AT_TYPE))) {
					body.setBodyType(propertyValue2.getValues().get(WebAnnotationFields.AT_TYPE));
				}
				if (!StringUtils.isBlank(propertyValue2.getValues().get(WebAnnotationFields.FOAF_PAGE))) {
					body.setHttpUri(propertyValue2.getValues().get(WebAnnotationFields.FOAF_PAGE));
				}
			}
		}
		return body;
	}

	/**
	 * This method extracts type as a string from JsonPropertyValue types list.
	 * By multiple values they are separated by comma.
	 * @param propertyValue
	 * @return type string
	 */
	private String getTypeStringFromValueTypes(JsonLdPropertyValue propertyValue) {
		String res = null;
		List<String> typeList = propertyValue.getTypes();
		if (typeList != null && typeList.size() > 0) {
			String typeStr = typeList.toString().replace(" ", "");
			res = typeStr.replace("[[", "[").replace("]]", "]"); // remove list braces
		}
		return res;
	}
    
	/**
     * Adds the values from the passed JsonLd object. 
     * 
     * @param jsonLd
     */
	public void setJsonLd(JsonLd jsonLd) {
             
    	setUseTypeCoercion(false);
        setUseCuries(true);
        //TODO: verify if required and set only once ...
        //addNamespacePrefix("http://www.w3.org/ns/oa-context-20130208.json", "oa");
        
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
//            if (!StringUtils.isBlank(annotation.getBody().getBodyType())) {        	
//				String bodyTypes = annotation.getBody().getBodyType();
//				String commaSeparatedTypes = bodyTypes.substring(1, bodyTypes.length() - 1);
//		        String[] tokens = commaSeparatedTypes.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//		        for(String t : tokens) {
//					propertyValue.addType(t);
//		        }
//        	}
	
            if (!StringUtils.isBlank(annotation.getBody().getBodyType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.BODY_TYPE, annotation.getBody().getBodyType());
            if (!StringUtils.isBlank(annotation.getBody().getValue()))         	
            	propertyValue.getValues().put(WebAnnotationFields.CHARS, annotation.getBody().getValue());
            if (!StringUtils.isBlank(annotation.getBody().getLanguage()))         	
            	propertyValue.getValues().put(WebAnnotationFields.DC_LANGUAGE, annotation.getBody().getLanguage());
            if (!StringUtils.isBlank(annotation.getBody().getContentType()))         	
            	propertyValue.getValues().put(WebAnnotationFields.FORMAT, annotation.getBody().getContentType());
	        bodyProperty.addValue(propertyValue);        
	
	        JsonLdPropertyValue jsonLdPropertyValue2 = new JsonLdPropertyValue();
	        
            if (!StringUtils.isBlank(annotation.getBody().getMediaType()))         	
//            	jsonLdPropertyValue2.addType(annotation.getBody().getMediaType());
//        		jsonLdPropertyValue2.addType(annotation.getBody().getMediaType().replace("[", "").replace("]", ""));
//            	jsonLdPropertyValue2.setType(annotation.getBody().getMediaType().replace("[", "").replace("]", ""));
            	///jsonLdPropertyValue2.setType(annotation.getBody().getMediaType());
	        	jsonLdPropertyValue2.getValues().put(WebAnnotationFields.MEDIA_TYPE, annotation.getBody().getMediaType().replace("[", "").replace("]", ""));
            	//jsonLdPropertyValue2.getValues().put(WebAnnotationFields.AT_TYPE, annotation.getBody().getMediaType().replace("[", "").replace("]", ""));
//	        jsonLdPropertyValue2.getValues().put(WebAnnotationFields.AT_TYPE, "mediaTypeTest");
            if (!StringUtils.isBlank(annotation.getBody().getHttpUri()))         	
            	jsonLdPropertyValue2.getValues().put(WebAnnotationFields.FOAF_PAGE, annotation.getBody().getHttpUri());
	        bodyProperty.addValue(jsonLdPropertyValue2);
        }
		return bodyProperty;
	}

	private JsonLdProperty addSerializedByProperty(Annotation annotation) {
		JsonLdProperty serializedByProperty = new JsonLdProperty(WebAnnotationFields.SERIALIZED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getSerializedBy();        
        if (agent != null && !StringUtils.isBlank(agent.getAgentType().name())) 
        	propertyValue.setType(agent.getAgentType().name());
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        if (agent != null && !StringUtils.isBlank(agent.getHomepage())) 
        	propertyValue.getValues().put(WebAnnotationFields.FOAF_HOMEPAGE, agent.getHomepage());
        serializedByProperty.addValue(propertyValue);
		return serializedByProperty;
	}

	private JsonLdProperty addAnnotatedByProperty(Annotation annotation) {
		JsonLdProperty annotatedByProperty = new JsonLdProperty(WebAnnotationFields.ANNOTATED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Agent agent = annotation.getAnnotatedBy();      
        propertyValue.setType("http://xmlns.com/foaf/0.1/person");
        if (agent != null && !StringUtils.isBlank(agent.getName())) 
        	propertyValue.getValues().put(WebAnnotationFields.NAME, agent.getName());
        annotatedByProperty.addValue(propertyValue);
		return annotatedByProperty;
	}

	private JsonLdProperty addStyledByProperty(Annotation annotation) {
		JsonLdProperty styledByProperty = new JsonLdProperty(WebAnnotationFields.STYLED_BY);
        JsonLdPropertyValue propertyValue = new JsonLdPropertyValue();
        Style style = annotation.getStyledBy();        
        if (style != null && !StringUtils.isBlank(style.getMediaType())) 
        	propertyValue.setType(style.getMediaType());
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