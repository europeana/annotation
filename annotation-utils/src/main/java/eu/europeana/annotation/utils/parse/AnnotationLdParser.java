package eu.europeana.annotation.utils.parse;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLdCommon;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import eu.europeana.annotation.definitions.exception.AnnotationInstantiationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class AnnotationLdParser extends JsonLdParser {

	protected final Logger logger = Logger.getLogger(this.getClass());
	private AnnotationIdHelper idHelper;

	/**
	 * Maps URIs to namespace prefixes.
	 */
	protected Map<String, String> namespacePrefixMap = new HashMap<String, String>();
	/**
	 * Internal map to hold the namespaces and prefixes that were actually used.
	 */
	protected Map<String, String> usedNamespaces = new HashMap<String, String>();

	public Annotation parseAnnotation(String jsonLdString)
			throws JsonParseException {
		Annotation annotaion = null;

		JSONObject jo = parseJson(jsonLdString);
		if (jo != null) {
			annotaion = createAnnotationInstance(jo);
			parseJsonObject(jo, annotaion, 1, null);
		}

		return annotaion;
	}

	protected Annotation createAnnotationInstance(JSONObject jo)
			throws JsonParseException {

		try {
			String motivation = jo.getString(WebAnnotationFields.MOTIVATION)
					.trim();

			MotivationTypes motivationType = MotivationTypes
					.getType(motivation);
			AnnotationTypes annoType = null;
			switch (motivationType) {
			case TAGGING:
				// TODO when needed make differentiation between simple tagging
				// and semantic tagging
				// i.e. body is a string literal or an object
				annoType = AnnotationTypes.OBJECT_TAG;
				break;
			case LINKING:
				// TODO when needed make differantiation between linking
				// europeana objects and linking europeana object with external
				// web pages
				// i.e. all targets are europeana objects or not ...
				annoType = AnnotationTypes.OBJECT_LINKING;
				break;
			default:
				break;
			}

			if (annoType == null)
				throw new AnnotationInstantiationException(
						"Unsupported Annotation/Motivation Type:" + jo);

			return AnnotationObjectFactory.getInstance().createObjectInstance(
					annoType);

		} catch (JSONException e) {
			throw new JsonParseException("motivation not found in json object!"
					+ jo, e);
		}

	}

	/**
	 * Parses a single subject.
	 * 
	 * @param jo
	 *            JSON object that holds the subject's data.
	 * @param jld
	 *            JsonLd object to add the created subject resource.
	 * @throws JsonParseException
	 */
	// @SuppressWarnings("deprecation")
	private void parseJsonObject(JSONObject jo, Annotation annoLd,
			int bnodeCount, String profile) throws JsonParseException {

		// The root subject is used for cases where no explicit subject is
		// specified. We need
		// at least one dummy subject (bnode) to support type coercion because
		// types are assigned to
		// subjects.
		// JsonLdResource subject = new JsonLdResource();

		try {
			if (jo.has(JsonLdCommon.CONTEXT)) {
				// JSONObject context = jo.getJSONObject(JsonLdCommon.CONTEXT);

				Object context = jo.get(JsonLdCommon.CONTEXT);
				if (context instanceof String) {
					// default context, no namespace ...
				} else {
					JSONObject contextObject = (JSONObject) context;
					// parse namespaces
					for (int i = 0; i < contextObject.names().length(); i++) {
						String name = contextObject.names().getString(i)
								.toLowerCase();
						// if (name.equals(JsonLdCommon.COERCE)) {
						// JSONObject typeObject = context.getJSONObject(name);
						// for (int j = 0; j < typeObject.names().length(); j++)
						// {
						// String property = typeObject.names().getString(j);
						// String type = typeObject.getString(property);
						// subject.putPropertyType(property, type);
						// }
						// } else {
						addNamespacePrefix(contextObject.getString(name), name);
						// }
					}
				}

				jo.remove(JsonLdCommon.CONTEXT);
			}

			// If there is a local profile specified for this subject, we
			// use that one. Otherwise we assign the profile given by the
			// parameter.
			if (jo.has(JsonLdCommon.PROFILE)) {
				// String localProfile = unCURIE(jo
				// .getString(JsonLdCommon.PROFILE), getNamespacePrefixMap());
				// profile = localProfile;
				// jo.remove(JsonLdCommon.PROFILE);
			}
			// subject.setProfile(profile);

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
	 * Adds a new namespace and its prefix to the list of used namespaces for
	 * this JSON-LD instance.
	 * 
	 * @param namespace
	 *            A namespace IRI.
	 * @param prefix
	 *            A prefix to use and identify this namespace in serialized
	 *            JSON-LD.
	 */
	public void addNamespacePrefix(String namespace, String prefix) {
		namespacePrefixMap.put(namespace, prefix);
	}

	void parseContext(Object valueObject) throws JSONException {
		// clean namespacePrefix
		// TODO: check if really needed
		namespacePrefixMap = new HashMap<String, String>();
		String key;
		for (@SuppressWarnings("rawtypes")
		Iterator iterator = ((JSONObject) valueObject).keys(); iterator
				.hasNext();) {
			key = (String) iterator.next();
			namespacePrefixMap.put(key,
					((JSONObject) valueObject).getString(key));
		}
		// jld.setNamespacePrefixMap(namespaces);
	}

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleProperty(Annotation anno, JSONObject jo, String property)
			throws JSONException, JsonParseException {

		Object valueObject = jo.get(property);
		switch (property) {
		case WebAnnotationFields.AT_TYPE:
			anno.setType((String) valueObject);
			break;
		case WebAnnotationFields.AT_ID:
			AnnotationId annotationId = parseId(valueObject, jo);
			anno.setAnnotationId(annotationId);
			break;
		case WebAnnotationFields.ANNOTATED_AT:
			anno.setAnnotatedAt(TypeUtils
					.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.ANNOTATED_BY:
			Agent annotator = parseAnnotator(valueObject);
			annotator.setInputString(valueObject.toString());
			anno.setAnnotatedBy(annotator);
			break;
		case WebAnnotationFields.SERIALIZED_AT:
			anno.setSerializedAt(TypeUtils
					.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.SERIALIZED_BY:
			Agent serializer = parseSerializer(valueObject);
			serializer.setInputString(valueObject.toString());
			anno.setSerializedBy(serializer);
			break;
		case WebAnnotationFields.MOTIVATION:
			anno.setMotivatedBy((String) valueObject);
			break;
		case WebAnnotationFields.BODY:
			Body body = parseBody(valueObject);
			body.setInputString(valueObject.toString());
			anno.setBody(body);
			break;
		case WebAnnotationFields.TARGET:
			// TODO: move the parsing of ID and target in the upper method to
			// avoid redundant computations
			Target target = parseTarget(valueObject);
			target.setInputString(valueObject.toString());
			anno.setTarget(target);
			break;
		case (WebAnnotationFields.OA + ":" + WebAnnotationFields.EQUIVALENT_TO):
			anno.setEquivalentTo((String) valueObject);
			break;
		default:
			break;
		}

		// if (valueObject instanceof JSONObject) {
		// JSONObject jsonValue = (JSONObject) valueObject;
		// String jsonValueStr = jsonValue.toString();
		// String jsonValueNormalized = normalize(jsonValueStr);
		//
		// JsonLdProperty jlp = new JsonLdProperty(property);
		// String mapString = jsonValueNormalized.substring(1,
		// jsonValueNormalized.length() - 1); // remove braces
		// JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
		//
		// if(JsonLd.CONTEXT.equals(property)){
		// parseContext(jld, valueObject);
		// return;
		// }
		//
		//
		// if (!mapString.contains("}")) {
		// /**
		// * The property is a single map - without complex objects inside
		// */
		// jlpv = parseJsonLdPropertyValue(mapString);
		// jlp.addValue(jlpv);
		// } else {
		// Map<String, String> propMap =
		// (Map<String, String>) convertToMapAndList(jsonValue,
		// jld.getNamespacePrefixMap());
		// List<String> keyList = new ArrayList<String>();
		// keyList.addAll(propMap.keySet());
		// Iterator<?> it = propMap.entrySet().iterator();
		// while (it.hasNext()) {
		// Map.Entry pairs = (Map.Entry)it.next();
		// String key = "\"" + pairs.getKey().toString() + "\"";
		// int nextPos = findNextKey(key, keyList, mapString);
		// int startPos = mapString.indexOf(key) + key.length() + 1; // +1 for
		// ':'
		// String value = mapString.substring(startPos, nextPos);
		// if (value.lastIndexOf(",") == value.length() - 1) {
		// value = value.substring(0, value.length() - 1);
		// }
		// value = value.substring(1, value.length() - 1); // remove braces
		// key = key.replace("\"", "");
		// if (!value.contains(",")) {
		// if (!value.contains("\":\"")) {
		// /**
		// * simple key value pair
		// */
		// jlpv.getValues().put(key, value);
		// } else {
		// /**
		// * complex value with ':'
		// */
		// JsonLdProperty subProperty = new JsonLdProperty(key);
		// JsonLdPropertyValue sub_jlpv = new JsonLdPropertyValue();
		// Map<String, String> propMap2 = splitToMap(value);
		// Iterator<?> it2 = propMap2.entrySet().iterator();
		// while (it2.hasNext()) {
		// Map.Entry pairs2 = (Map.Entry)it2.next();
		// String key2 = pairs2.getKey().toString().replace("\"",
		// "").replace("{", "").replace("}", "");
		// String value2 = pairs2.getValue().toString();//.replace("\"",
		// "").replace("{", "").replace("}", "");
		// if (value2.length() < 2) {
		// value2 = "";
		// }
		// sub_jlpv.getValues().put(key2, value2);
		// subProperty.addValue(sub_jlpv);
		// jlpv.putProperty(subProperty);
		// }
		// }
		// } else {
		// if (value.contains(":") && value.contains("euType") &&
		// !key.equals("selector")) { // for the euType
		// // if (value.contains(":") && value.contains("#")) { // for the
		// euType
		// jlpv.getValues().put(key, value);
		// } else {
		// JsonLdProperty subProperty = new JsonLdProperty(key);
		// JsonLdPropertyValue sub_jlpv = parseJsonLdPropertyValue(value);
		// subProperty.addValue(sub_jlpv);
		// jlpv.putProperty(subProperty);
		// }
		// }
		// }
		// jlp.addValue(jlpv);
		// }
		// subject.putProperty(jlp);
		// } else if (valueObject instanceof JSONArray) {
		// JSONArray arrayValue = (JSONArray) valueObject;
		// String jsonValueStr = arrayValue.toString();
		// String jsonValueNormalized = normalize(jsonValueStr);
		// JsonLdProperty jlp = new JsonLdProperty(property);
		// String arrayString = jsonValueNormalized.substring(1,
		// jsonValueNormalized.length() - 1);
		// String[] propArray = splitToArray(arrayString);
		// Iterator<String> itr = Arrays.asList(propArray).iterator();
		// while (itr.hasNext()) {
		// String propString = normalizeArrayString(itr.next());
		// JsonLdPropertyValue jlpv = parseJsonLdPropertyValue(propString);
		// jlp.addValue(jlpv);
		// }
		// subject.putProperty(jlp);
		// } else if (valueObject instanceof String) {
		// // JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
		// // JsonLdProperty jlp = new JsonLdProperty(property);
		//
		// String stringValue = (String) valueObject;
		// subject.putProperty(property, unCURIE(stringValue, jld
		// .getNamespacePrefixMap()));
		// // jlpv.getValues().put(property, unCURIE(stringValue, jld
		// // .getNamespacePrefixMap()));
		// // jlp.addValue(jlpv);
		// } else {
		// // JsonLdPropertyValue jlpv = new JsonLdPropertyValue();
		// // JsonLdProperty jlp = new JsonLdProperty(property);
		//
		// subject.putProperty(property, valueObject);
		// // jlpv.getValues().put(property, (String) valueObject);
		// // jlp.addValue(jlpv);
		// }
	}

	private Agent parseSerializer(Object valueObject) throws JsonParseException {
		return parseAgent(AgentTypes.SOFTWARE, valueObject);
	}

	private Agent parseAgent(AgentTypes defaultAgentType, Object valueObject)
			throws JsonParseException {
		if(valueObject instanceof String)
			return parseAgent(defaultAgentType, (String) valueObject);
		else if (valueObject instanceof JSONObject)
			return parseAgent((JSONObject)valueObject);
		else
			throw new JsonParseException("unsupported agent deserialization for: " + valueObject);
	}

	private Agent parseAnnotator(Object valueObject) throws JsonParseException {
		return parseAgent(AgentTypes.PERSON, valueObject);
	}

	private Target parseTarget(Object valueObject) throws JsonParseException {
		if(valueObject instanceof String)
			return parseTarget(TargetTypes.WEB_PAGE.name(), (String) valueObject);
		else if (valueObject instanceof JSONObject)
			return parseTarget((JSONObject)valueObject);
		else if (valueObject instanceof JSONArray)
			return parseTarget((JSONArray)valueObject);
		else
			throw new JsonParseException("unsupported target+- deserialization for: " + valueObject);
	}

	private Target parseTarget(String defaultType, String valueObject) {
		Target target = TargetObjectFactory.getInstance()
				.createModelObjectInstance(defaultType);
		//already done through constructor 
		//target.setTargetType(TargetTypes.TEXT.name());
		target.setValue((String) valueObject);
		target.setHttpUri(valueObject);
		return target;
		
	}

	private Target parseTarget(JSONArray valueObject) throws JsonParseException {
		Target target = TargetObjectFactory.getInstance()
				.createModelObjectInstance(TargetTypes.WEB_PAGE.name());
		try {
			for (int i=0; i < valueObject.length(); i++) 
				target.addValue(valueObject.getString(i));
		} catch (JSONException e) {
			throw new JsonParseException("unsupported target deserialization for json represetnation: " + valueObject + " " + e.getMessage());
		}
//		throw new JsonParseException("Multiple target deserialization is not supported yet: " + valueObject);
		return target;
	}
	
	private Target parseTarget(JSONObject jo) throws JsonParseException {
		try {
			return parseTarget(jo.get(WebAnnotationFields.TARGET));
		} catch (JSONException e) {
			throw new JsonParseException("cannot parse target", e);
		}
	}

	private AnnotationId parseId(Object valueObject, JSONObject jo)
			throws JsonParseException {
		AnnotationId annoId = null;
		if (valueObject instanceof String) {
			// not supported anymore. It will be sent through the equivalent to
			//annoId = parseIdFromUri((String) valueObject, jo);
		} else if (valueObject instanceof JSONObject) {
			annoId = parseIdFromJson((JSONObject) valueObject);
		} else {
			throw new JsonParseException("Cannot parse ID value: "
					+ valueObject);
		}
		return annoId;
	}

	private AnnotationId parseIdFromJson(JSONObject valueObject) {
		// TODO Auto-generated method stub
		return null;
	}

	/** not supported anymore. It will be sent through the equivalent to */
//	@Deprecated
//	private AnnotationId parseIdFromUri(String valueObject, JSONObject jo)
//			throws JsonParseException {
//		// TODO: check if already implemented or if can be moved in
//		// AnnotationIdDeserializer
//		String parts[] = valueObject.split("/");
//		Long annotationNr = Long.valueOf(parts[parts.length - 1]);
//		String provider = parts[parts.length - 2];
//		BaseAnnotationId annoId = new BaseAnnotationId();
//		annoId.setProvider(provider);
//		annoId.setAnnotationNr(annotationNr);
//
//		Target target = parseTarget(jo);
////		String resourceId = getIdHelper().extractResoureIdFromHttpUri(
////				target.getHttpUri());
//
////		annoId.setResourceId(resourceId);
//		return annoId;
//	}

	private Agent parseAgent(AgentTypes type, String valueObject) {
		Agent agent = AgentObjectFactory.getInstance()
				.createModelObjectInstance(type.name());
		agent.setHomepage(valueObject);
		agent.setName(valueObject);
		// agent.setName((String) valueObject);
		return agent;
	}

	private Agent parseAgent(JSONObject valueObject) throws JsonParseException {
		try {
			String webType = (String) valueObject
					.get(WebAnnotationFields.AT_TYPE);
			String[] parts = webType.split(":");
			String agentType = parts[parts.length - 1];

			Agent agent = AgentObjectFactory.getInstance()
					.createModelObjectInstance(agentType);
//			agent.addType(webType);
			agent.setType(webType);

			agent.setOpenId(valueObject.getString(WebAnnotationFields.AT_ID));

			// agent.setHomepage(valueObject);
			agent.setName(valueObject.getString(WebAnnotationFields.NAME));

			// agent.setName((String) valueObject);
			return agent;
		} catch (JSONException e) {
			throw new JsonParseException("cannot parse agent", e);
		}
	}

	private Body parseBody(Object valueObject) throws JsonParseException {
			if(valueObject instanceof String)
				return parseBody(BodyTypes.TAG.name(), (String) valueObject);
			else if (valueObject instanceof JSONObject)
				return parseBody((JSONObject)valueObject);
			else if (valueObject instanceof JSONArray)
				return parseBody((JSONArray)valueObject);
			else
				throw new JsonParseException("unsupported body deserialization for: " + valueObject);
	}

	private Body parseBody(String defaultType, String valueObject) {
		
		Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
				defaultType);
		body.addType(defaultType);
		body.setValue((String) valueObject);
		return body;
	}

	private Body parseBody(JSONObject valueObject) throws JsonParseException {
		Body body = BodyObjectFactory.getInstance().createModelObjectInstance(BodyTypes.TAG.name());
		try {
			@SuppressWarnings("rawtypes")
			Iterator iterator = ((JSONObject) valueObject).keys(); 
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				Object value = ((JSONObject) valueObject).get(key); 
				switch (key) {
				case WebAnnotationFields.AT_TYPE:
					if (value.getClass().equals(JSONArray.class)) {
						for (int i=0; i < ((JSONArray) value).length(); i++) 
							body.addType(((JSONArray) value).getString(i));
					}
					else
						body.addType(value.toString());
					body.setInternalType(BodyTypes.TAG.name());
					break;
				case WebAnnotationFields.CHARS:
					body.setValue(value.toString());
					break;
				case WebAnnotationFields.DC_LANGUAGE:
					body.setLanguage(value.toString());
					break;
				case WebAnnotationFields.FORMAT:
					body.setContentType(value.toString());
					break;
				default:
					break;
				}
			}
		} catch (JSONException e) {
			throw new JsonParseException("unsupported body deserialization for json represetnation: " + valueObject + " " + e.getMessage());
		}
		
		
//		throw new JsonParseException("unsupported body deserialization for json represetnation: " + valueObject);
		
//		Body body = BodyObjectFactory.getInstance().createModelObjectInstance(
//				BodyTypes.TAG.name());
//		body.setBodyType(BodyTypes.TAG.name());
//		body.setValue((String) valueObject);
		return body;
	}
	
	private Body parseBody(JSONArray valueObject) throws JsonParseException {
		Body body = BodyObjectFactory.getInstance()
				.createModelObjectInstance(BodyTypes.TAG.name());
		try {
			for (int i=0; i < valueObject.length(); i++) 
				body.addValue(valueObject.getString(i));
		} catch (JSONException e) {
			throw new JsonParseException("unsupported body deserialization for json represetnation: " + valueObject + " " + e.getMessage());
		}
		return body;
	}
	
	public AnnotationIdHelper getIdHelper() {
		if (idHelper == null)
			idHelper = new AnnotationIdHelper();
		return idHelper;
	}

}
