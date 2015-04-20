package eu.europeana.annotation.utils.parse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.jsonld.JsonLdCommon;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class AnnotationLdParser extends JsonLdParser {

	protected final Logger logger = Logger.getLogger(this.getClass());
	
	 /**
     * Maps URIs to namespace prefixes.
     */
    protected Map<String,String> namespacePrefixMap = new HashMap<String,String>();	/**
     * Internal map to hold the namespaces and prefixes that were actually used.
     */
    protected Map<String,String> usedNamespaces = new HashMap<String,String>();


	public Annotation parseAnnotation(String jsonLdString) throws Exception {
		Annotation annoLd = null;

		JSONObject jo = parseJson(jsonLdString);
		if (jo != null) {
			annoLd = createAnnotationInstance(jo);
			parseJsonObject(jo, annoLd, 1, null);
		}

		return annoLd;
	}
	
	protected Annotation createAnnotationInstance(JSONObject jo) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Parses a single subject.
	 * 
	 * @param jo
	 *            JSON object that holds the subject's data.
	 * @param jld
	 *            JsonLd object to add the created subject resource.
	 */
//	@SuppressWarnings("deprecation")
	private void parseJsonObject(JSONObject jo, Annotation annoLd, int bnodeCount,
			String profile) {

		// The root subject is used for cases where no explicit subject is
		// specified. We need
		// at least one dummy subject (bnode) to support type coercion because
		// types are assigned to
		// subjects.
		//JsonLdResource subject = new JsonLdResource();

		try {
			if (jo.has(JsonLdCommon.CONTEXT)) {
				JSONObject context = jo.getJSONObject(JsonLdCommon.CONTEXT);
				for (int i = 0; i < context.names().length(); i++) {
					String name = context.names().getString(i).toLowerCase();
//					if (name.equals(JsonLdCommon.COERCE)) {
//						JSONObject typeObject = context.getJSONObject(name);
//						for (int j = 0; j < typeObject.names().length(); j++) {
//							String property = typeObject.names().getString(j);
//							String type = typeObject.getString(property);
//							subject.putPropertyType(property, type);
//						}
//					} else {
						addNamespacePrefix(context.getString(name), name);
					//}
				}

				jo.remove(JsonLdCommon.CONTEXT);
			}

			// If there is a local profile specified for this subject, we
			// use that one. Otherwise we assign the profile given by the
			// parameter.
			if (jo.has(JsonLdCommon.PROFILE)) {
//				String localProfile = unCURIE(jo
//						.getString(JsonLdCommon.PROFILE), getNamespacePrefixMap());
//				profile = localProfile;
//				jo.remove(JsonLdCommon.PROFILE);
			}
			//subject.setProfile(profile);

			if (jo.names() != null && jo.names().length() > 0) {
				for (int i = 0; i < jo.names().length(); i++) {
					String property = jo.names().getString(i);
					handleProperty(annoLd, jo, property);
				}
			}

		} catch (JSONException e) {
			logger.error(
					"There were JSON problems when parsing the JSON-LD String",
					e);
			e.printStackTrace();
		}
	}

	public Map<String, String> getNamespacePrefixMap() {
		return namespacePrefixMap;
	}

	public Map<String, String> getUsedNamespaces() {
		return usedNamespaces;
	}
	  /**
     * Adds a new namespace and its prefix to the list of used namespaces for this JSON-LD instance.
     * 
     * @param namespace
     *            A namespace IRI.
     * @param prefix
     *            A prefix to use and identify this namespace in serialized JSON-LD.
     */
    public void addNamespacePrefix(String namespace, String prefix) {
        namespacePrefixMap.put(namespace, prefix);
    }
    
    void parseContext(Object valueObject)
			throws JSONException {
		//clean namespacePrefix
    	//TODO: check if really needed
    	namespacePrefixMap = new HashMap<String, String>();
		String key;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = ((JSONObject) valueObject).keys(); iterator.hasNext();) {
			key = (String) iterator.next();
			namespacePrefixMap.put(key, ((JSONObject) valueObject).getString(key));
		}
		//jld.setNamespacePrefixMap(namespaces);
	}
    
//    @SuppressWarnings({ "rawtypes", "unchecked" })
	private static void handleProperty(Annotation annoLd, JSONObject jo,
			String property) throws JSONException {
		
		Object valueObject = jo.get(property);
		
		switch (property) {
		case WebAnnotationFields.ANNOTATED_AT:
			if (valueObject != null)
				annoLd.setAnnotatedAt(TypeUtils.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.SERIALIZED_AT:
			if (valueObject != null)
				annoLd.setSerializedAt(TypeUtils.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.SERIALIZED_BY:
			if (valueObject != null) {
				Agent agent = AgentObjectFactory.getInstance().createModelObjectInstance(AgentTypes.PERSON.name());
				agent.setAgentTypeAsString(AgentTypes.PERSON.name());			
				agent.setName((String) valueObject);
				annoLd.setSerializedBy(agent);
			}
			break;
		case WebAnnotationFields.MOTIVATION:
			if (valueObject != null)
				annoLd.setMotivatedBy((String) valueObject);
			break;
		case WebAnnotationFields.BODY:
			if (valueObject != null) {
				Body body = BodyObjectFactory.getInstance().createModelObjectInstance(BodyTypes.TAG.name());	
				body.setBodyType(BodyTypes.TAG.name());
				body.setValue((String) valueObject);
				annoLd.setBody(body);
			}
			break;
		case WebAnnotationFields.TARGET:
			if (valueObject != null) {
				Target target = TargetObjectFactory.getInstance().createModelObjectInstance(TargetTypes.TEXT.name());	
				target.setTargetType(TargetTypes.TEXT.name());
				target.setValue((String) valueObject);
				annoLd.setTarget(target);
			}
			break;
		case WebAnnotationFields.EQUIVALENT_TO:
			if (valueObject != null)
				annoLd.setEquivalentTo((String) valueObject);
			break;
		default:
			break;
		}
		
//		if (valueObject instanceof JSONObject) {			
//			JSONObject jsonValue = (JSONObject) valueObject;
//			String jsonValueStr = jsonValue.toString();
//			String jsonValueNormalized = normalize(jsonValueStr);
//			
//			JsonLdProperty jlp = new JsonLdProperty(property);
//			String mapString = jsonValueNormalized.substring(1, jsonValueNormalized.length() - 1); // remove braces
//			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
//			
//			if(JsonLd.CONTEXT.equals(property)){
//				parseContext(jld, valueObject);
//				return;
//			}
//			
//			
//			if (!mapString.contains("}")) {
//				/**
//				 * The property is a single map - without complex objects inside
//				 */
//				jlpv = parseJsonLdPropertyValue(mapString);
//				jlp.addValue(jlpv);
//			} else {
//				Map<String, String> propMap = 
//						(Map<String, String>) convertToMapAndList(jsonValue, jld.getNamespacePrefixMap());
//				List<String> keyList = new ArrayList<String>();
//				keyList.addAll(propMap.keySet());
//				Iterator<?> it = propMap.entrySet().iterator();
//				while (it.hasNext()) {
//				    Map.Entry pairs = (Map.Entry)it.next();
//				    String key = "\"" + pairs.getKey().toString() + "\"";
//				    int nextPos = findNextKey(key, keyList, mapString);
//				    int startPos = mapString.indexOf(key) + key.length() + 1; // +1 for ':'
//				    String value = mapString.substring(startPos, nextPos);
//				    if (value.lastIndexOf(",") == value.length() - 1) {
//					    value = value.substring(0, value.length() - 1);
//				    }
//					value = value.substring(1, value.length() - 1); // remove braces
//				    key = key.replace("\"", "");
//				    if (!value.contains(",")) {
//				    	if (!value.contains("\":\"")) {
//				    		/**
//				    		 * simple key value pair
//				    		 */				    	
//				    		jlpv.getValues().put(key, value);
//				    	} else {
//				    		/**
//				    		 * complex value with ':'
//				    		 */
//					        JsonLdProperty subProperty = new JsonLdProperty(key);
//							JsonLdPropertyValue sub_jlpv = new JsonLdPropertyValue();
//							Map<String, String> propMap2 = splitToMap(value);
//							Iterator<?> it2 = propMap2.entrySet().iterator();
//							while (it2.hasNext()) {
//							    Map.Entry pairs2 = (Map.Entry)it2.next();
//							    String key2 = pairs2.getKey().toString().replace("\"", "").replace("{", "").replace("}", "");
//							    String value2 = pairs2.getValue().toString();//.replace("\"", "").replace("{", "").replace("}", "");
//							    if (value2.length() < 2) {
//							    	value2 = "";
//							    }
//							    sub_jlpv.getValues().put(key2, value2);
//								subProperty.addValue(sub_jlpv);
//						        jlpv.putProperty(subProperty);
//							}
//				    	}
//				    } else {
//				    	if (value.contains(":") && value.contains("euType") && !key.equals("selector")) { // for the euType
////					    	if (value.contains(":") && value.contains("#")) { // for the euType
//				    		jlpv.getValues().put(key, value);
//				    	} else {
//					        JsonLdProperty subProperty = new JsonLdProperty(key);
//							JsonLdPropertyValue sub_jlpv = parseJsonLdPropertyValue(value);
//							subProperty.addValue(sub_jlpv);
//					        jlpv.putProperty(subProperty);
//				    	}
//				    }
//				}
//				jlp.addValue(jlpv);
//			}
//			subject.putProperty(jlp);
//		} else if (valueObject instanceof JSONArray) {
//			JSONArray arrayValue = (JSONArray) valueObject;
//			String jsonValueStr = arrayValue.toString();
//			String jsonValueNormalized = normalize(jsonValueStr);
//			JsonLdProperty jlp = new JsonLdProperty(property);
//			String arrayString = jsonValueNormalized.substring(1, jsonValueNormalized.length() - 1);
//			String[] propArray = splitToArray(arrayString);
//			Iterator<String> itr = Arrays.asList(propArray).iterator();
//			while (itr.hasNext()) {
//				String propString = normalizeArrayString(itr.next());
//				JsonLdPropertyValue jlpv = parseJsonLdPropertyValue(propString);
//				jlp.addValue(jlpv);
//			}
//			subject.putProperty(jlp);
//		} else if (valueObject instanceof String) {
////			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
////			JsonLdProperty jlp = new JsonLdProperty(property);
//
//			String stringValue = (String) valueObject;
//			subject.putProperty(property, unCURIE(stringValue, jld
//					.getNamespacePrefixMap()));
////    		jlpv.getValues().put(property, unCURIE(stringValue, jld
////					.getNamespacePrefixMap()));
////			jlp.addValue(jlpv);
//		} else {
////			JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
////			JsonLdProperty jlp = new JsonLdProperty(property);
//
//			subject.putProperty(property, valueObject);
////    		jlpv.getValues().put(property, (String) valueObject);
////			jlp.addValue(jlpv);
//		}
	}

}
