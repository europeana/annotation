package eu.europeana.annotation.utils.parse;

import java.net.MalformedURLException;
import java.net.URL;
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

import eu.europeana.annotation.definitions.exception.AnnotationAttributeInstantiationException;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.factory.impl.AgentObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.AnnotationObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.BodyObjectFactory;
import eu.europeana.annotation.definitions.model.factory.impl.TargetObjectFactory;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.definitions.model.vocabulary.AgentTypes;
import eu.europeana.annotation.definitions.model.vocabulary.BodyTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

/**
 * Replaced by EuropeanaAnnotationLd
 * 
 * @author GordeaS
 *
 */
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

	public Annotation parseAnnotation(MotivationTypes motivationType, String jsonLdString) throws JsonParseException {
		Annotation annotaion = null;

		JSONObject jo;
		try {
			jo = parseJson(jsonLdString);
		} catch (JSONException e) {
			throw new JsonParseException("Cannot parse json string: " + jsonLdString, e);
		}
		try {
			annotaion = createAnnotationInstance(motivationType, jo);
		} catch (RuntimeException e) {
			throw new AnnotationValidationException("cannot instantiate Annotation! " + e.getMessage(), e);
		}
		parseJsonObject(jo, annotaion, 1, null);

		return annotaion;
	}

	protected Annotation createAnnotationInstance(MotivationTypes motivationType, JSONObject jo)
			throws JsonParseException {

		MotivationTypes verifiedMotivationType = null;
		String motivation;

		// search motivation in json
		try {
			motivation = jo.getString(WebAnnotationFields.MOTIVATION).trim();
		} catch (JSONException e) {
			// if not found use provided motivation type
			motivation = null;
			verifiedMotivationType = motivationType;
		}

		// motivation is mandatory
		boolean invalidMotivation = false;

		if (motivationType == null && motivation == null)
			invalidMotivation = true;

		// validate json motivation
		if (motivation != null) {
			verifiedMotivationType = MotivationTypes.getType(motivation);
			// incorrect motivation value
			if (verifiedMotivationType == null || MotivationTypes.UNKNOWN.equals(verifiedMotivationType))
				invalidMotivation = true;
			// motivation doesn't match
			else if (motivationType != null && !motivationType.equals(verifiedMotivationType))
				invalidMotivation = true;
		}

		if (invalidMotivation)
			throw new AnnotationValidationException(AnnotationValidationException.ERROR_INVALID_MOTIVATION
					+ "\nmotivation type:" + motivationType + "\nparsed object: " + jo.toString());

		// by now the verified annotation was set with the valid
		return AnnotationObjectFactory.getInstance().createAnnotationInstance(verifiedMotivationType);
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
	private void parseJsonObject(JSONObject jo, Annotation annoLd, int bnodeCount, String profile)
			throws JsonParseException {

		try {
			if (jo.has(JsonLdCommon.CONTEXT)) {

				Object context = jo.get(JsonLdCommon.CONTEXT);
				if (context instanceof String) {
					// default context, no namespace ...
				} else {
					JSONObject contextObject = (JSONObject) context;
					// parse namespaces
					for (int i = 0; i < contextObject.names().length(); i++) {
						String name = contextObject.names().getString(i).toLowerCase();
						addNamespacePrefix(contextObject.getString(name), name);
					}
				}

				jo.remove(JsonLdCommon.CONTEXT);
			}

			// // If there is a local profile specified for this subject, we
			// // use that one. Otherwise we assign the profile given by the
			// // parameter.
			// if (jo.has(JsonLdCommon.PROFILE)) {
			// // String localProfile = unCURIE(jo
			// // .getString(JsonLdCommon.PROFILE), getNamespacePrefixMap());
			// // profile = localProfile;
			// // jo.remove(JsonLdCommon.PROFILE);
			// }
			// subject.setProfile(profile);

			if (jo.names() != null && jo.names().length() > 0) {
				for (int i = 0; i < jo.names().length(); i++) {
					String property = jo.names().getString(i);
					handleProperty(annoLd, jo, property);
				}
			}

		} catch (JSONException e) {
			logger.error("There were JSON problems when parsing the JSON-LD String", e);
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
		Iterator iterator = ((JSONObject) valueObject).keys(); iterator.hasNext();) {
			key = (String) iterator.next();
			namespacePrefixMap.put(key, ((JSONObject) valueObject).getString(key));
		}
		// jld.setNamespacePrefixMap(namespaces);
	}

	// @SuppressWarnings({ "rawtypes", "unchecked" })
	private void handleProperty(Annotation anno, JSONObject jo, String property)
			throws JSONException, JsonParseException {

		Object valueObject = jo.get(property);
		AnnotationId annotationId;

		// TODO: improve to use WAPropEnum instead of constants, and reduce
		// redundancy related to @id vs. id
		switch (property) {
		case WebAnnotationFields.TYPE:
			anno.setType((String) valueObject);
			break;
		case WebAnnotationFields.ID:
			annotationId = parseId(valueObject, jo);
			anno.setAnnotationId(annotationId);
			break;
		case WebAnnotationFields.CREATED:
			anno.setCreated(TypeUtils.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.CREATOR:
			Agent annotator = parseAnnotator(valueObject);
			annotator.setInputString(valueObject.toString());
			anno.setCreator(annotator);
			break;
		case WebAnnotationFields.GENERATED:
			anno.setGenerated(TypeUtils.convertStrToDate((String) valueObject));
			break;
		case WebAnnotationFields.GENERATOR:
			Agent serializer = parseSerializer(valueObject);
			serializer.setInputString(valueObject.toString());
			anno.setGenerator(serializer);
			break;
		case WebAnnotationFields.MOTIVATION:
			anno.setMotivation((String) valueObject);
			break;
		case WebAnnotationFields.BODY_TEXT:
			Body bodyText = parseBodyText(anno.getMotivationType(), valueObject.toString());
			bodyText.setInputString(serializeBodyText(valueObject.toString()));
			anno.setBody(bodyText);
			break;
		case WebAnnotationFields.BODY:
			Body body = parseBody(anno.getMotivationType(), valueObject);
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
		case (WebAnnotationFields.EQUIVALENT_TO):
			anno.setEquivalentTo((String) valueObject);
			break;
		default:
			break;
		}

	}

	protected String serializeBodyText(String valueObject) {
		return "\""+ WebAnnotationFields.BODY_TEXT + "\":\"" + valueObject.toString() + "\"";
	}

	private Agent parseSerializer(Object valueObject) throws JsonParseException {
		return parseAgent(AgentTypes.SOFTWARE, valueObject);
	}

	private Agent parseAgent(AgentTypes defaultAgentType, Object valueObject) throws JsonParseException {
		if (valueObject instanceof String)
			return parseAgent(defaultAgentType, (String) valueObject);
		else if (valueObject instanceof JSONObject)
			return parseAgent((JSONObject) valueObject);
		else
			throw new JsonParseException("unsupported agent deserialization for: " + valueObject);
	}

	private Agent parseAnnotator(Object valueObject) throws JsonParseException {
		return parseAgent(AgentTypes.PERSON, valueObject);
	}

	private Target parseTarget(Object valueObject) throws JsonParseException {
		if (valueObject instanceof String)
			return parseTarget(TargetTypes.WEB_PAGE.name(), (String) valueObject);
		else if (valueObject instanceof JSONObject)
			return parseTarget((JSONObject) valueObject);
		else if (valueObject instanceof JSONArray)
			return parseTarget((JSONArray) valueObject);
		else
			throw new JsonParseException("unsupported target+- deserialization for: " + valueObject);
	}

	private Target parseTarget(String defaultType, String valueObject) {
		Target target = TargetObjectFactory.getInstance().createModelObjectInstance(defaultType);
		target.setValue((String) valueObject);
		target.setResourceId(
				getIdHelper().buildResourseId(getIdHelper().extractResoureIdPartsFromHttpUri((String) valueObject)));
		target.setHttpUri(valueObject);
		return target;

	}

	private Target parseTarget(JSONArray valueObject) throws JsonParseException {
		Target target = TargetObjectFactory.getInstance().createModelObjectInstance(TargetTypes.WEB_PAGE.name());
		try {
			for (int i = 0; i < valueObject.length(); i++) {
				String val = valueObject.getString(i);
				target.addValue(val);
				String resourceId = getIdHelper().buildResourseId(getIdHelper().extractResoureIdPartsFromHttpUri(val));
				target.addResourceId(resourceId);
			}
		} catch (JSONException e) {
			throw new JsonParseException("unsupported target deserialization for json represetnation: " + valueObject
					+ " " + e.getMessage());
		}
		return target;
	}

	private Target parseTarget(JSONObject jo) throws JsonParseException {
		try {
			return parseTarget(jo.get(WebAnnotationFields.TARGET));
		} catch (JSONException e) {
			throw new JsonParseException("cannot parse target", e);
		}
	}

	private AnnotationId parseId(Object valueObject, JSONObject jo) throws JsonParseException {
		AnnotationId annoId = null;
		if (valueObject instanceof String) {
			annoId = parseIdFromUri((String) valueObject, jo);
		} else if (valueObject instanceof JSONObject) {
			// annoId = parseIdFromJson((JSONObject) valueObject);
			throw new JsonParseException("Cannot parse ID value: " + valueObject);
		} else {
			throw new JsonParseException("Cannot parse ID value: " + valueObject);
		}
		return annoId;
	}

	private AnnotationId parseIdFromUri(String valueObject, JSONObject jo) throws JsonParseException {
		// TODO: check if already implemented or if can be moved in
		// AnnotationIdDeserializer
		String parts[] = valueObject.split("/");
		String identifier = parts[parts.length - 1];
		String provider = parts[parts.length - 2];
		final int SLASHES = 2;
		int baseUrlLength = valueObject.length() - provider.length() - identifier.length() - SLASHES;
		String baseUrl = valueObject.substring(0, baseUrlLength);

		AnnotationId annoId = new BaseAnnotationId();
		annoId.setProvider(provider);
		annoId.setIdentifier(identifier);
		annoId.setBaseUrl(baseUrl);

		return annoId;
	}

	private Agent parseAgent(AgentTypes type, String valueObject) {
		Agent agent = AgentObjectFactory.getInstance().createModelObjectInstance(type.name());
		agent.setHomepage(valueObject);
		agent.setName(valueObject);
		return agent;
	}

	private Agent parseAgent(JSONObject valueObject) throws JsonParseException {
		try {

			String webType = parseTypeValue(valueObject);
			AgentTypes agentType = AgentTypes.getByJsonValue(webType);
			Agent agent = AgentObjectFactory.getInstance().createObjectInstance(agentType);
			// agent.setType(webType);

			// TODO: consider using the WAPropEnum
			if (valueObject.has(WebAnnotationFields.ID))
				agent.setOpenId(valueObject.getString(WebAnnotationFields.ID));

			// agent.setHomepage(valueObject);
			if (valueObject.has(WebAnnotationFields.NAME))
				agent.setName(valueObject.getString(WebAnnotationFields.NAME));

			return agent;
		} catch (JSONException e) {
			throw new JsonParseException("cannot parse agent", e);
		}
	}

	protected String parseTypeValue(JSONObject valueObject) throws JSONException {
		String webType = null;
		if (valueObject.has(WebAnnotationFields.TYPE))
			webType = (String) valueObject.get(WebAnnotationFields.TYPE);
		return webType;
	}

	private Body parseBody(MotivationTypes motivation, Object valueObject) throws JsonParseException {

		if (valueObject instanceof String) {
			String stringValue = (String)valueObject;
			//the input string must be an URL .. semantic tagging
			if(!isUrl(stringValue))
				throw new AnnotationAttributeInstantiationException(WebAnnotationFields.BODY,
					"Invalid representation body=\"<text>\". Text bodies must be formated using TextualBody types!");
			return parseBody(stringValue, motivation);
		
		} else if (valueObject instanceof JSONObject)
			return parseBody((JSONObject) valueObject, motivation);
		else if (valueObject instanceof JSONArray)
			return parseBody((JSONArray) valueObject, motivation);
		else
			throw new JsonParseException("unsupported body deserialization for: " + valueObject);
	}

	private boolean isUrl(String stringValue) {
		try{
			new URL(stringValue);
			return true;
		}catch(Exception e){
			return false;	
		}
	}

	private Body parseBodyText(MotivationTypes motivation, String bodyText) throws JsonParseException {
		Body body = parseBody(bodyText, motivation);
		// add "bodyText" implications
		body.setContentType(WebAnnotationFields.MIME_TYPE_TEXT_PLAIN);
		body.addType(WebAnnotationFields.BODY_CLASS_TEXTUAL_BODY);
		
		return body;
	}

	protected BodyTypes guesBodyInternalType(MotivationTypes motivation, String value) {

		switch (motivation) {
		case LINKING:
			return BodyTypes.LINK;

		case TAGGING:
			return guesBodyTagSubType(value);

		default:
			break;
		}
		throw new AnnotationAttributeInstantiationException(
				"Cannot find appropriate body type with MotivationType: " + motivation);
	}

	private BodyTypes guesBodyTagSubType(String value) {
		// TODO: improve this .. similar check is done in validation.. these two
		// places should be merged.
		try {
			new URL(value);
			return BodyTypes.SEMANTIC_TAG;		
		} catch (MalformedURLException e) {
			return BodyTypes.TAG;
		}

	}

	private Body parseBody(String valueObject, MotivationTypes motivation) {

		BodyTypes bodyType = guesBodyInternalType(motivation, valueObject);

		Body body = BodyObjectFactory.getInstance().createObjectInstance(bodyType);
		body.setValue(valueObject);
		return body;
	}

	private Body parseBody(JSONObject valueObject, MotivationTypes motivation) throws JsonParseException {
		// by now both plaintag and semantictag are specific resources. However,
		// the usage of SemanticTag should be implemented in the future
		BodyTypes bodyType = guesBodyInternalType(valueObject, motivation);
		Body body = BodyObjectFactory.getInstance().createObjectInstance(bodyType);

		try {
			@SuppressWarnings("rawtypes")
			Iterator iterator = ((JSONObject) valueObject).keys();
			while (iterator.hasNext()) {
				String key = (String) iterator.next();
				Object value = ((JSONObject) valueObject).get(key);
				switch (key) {
				case WebAnnotationFields.TYPE:
					// if (value.getClass().equals(JSONArray.class)) {
					// for (int i = 0; i < ((JSONArray) value).length(); i++)
					// body.addType(((JSONArray) value).getString(i));
					// } else
					body.addType(value.toString());
					body.setInternalType(bodyType.name());
					break;

				case WebAnnotationFields.LANGUAGE:
					body.setLanguage(value.toString());
					break;
				case WebAnnotationFields.TEXT:
					body.setValue(value.toString());
					//add implications of TEXT field
					body.addType(WebAnnotationFields.BODY_CLASS_TEXTUAL_BODY);
					break;
				case WebAnnotationFields.FORMAT:
					body.setContentType(value.toString());
					break;
				case WebAnnotationFields.SOURCE:
					if (value instanceof String)
						body.setSource(value.toString());
					else
						throw new JsonParseException(
								"source as internet resource is not supported in this version of the parser!");
					break;
				case WebAnnotationFields.ID:
					// need to align with target.
					// body.setValue(value.toString());
					body.setHttpUri(value.toString());
					break;

				case WebAnnotationFields.PURPOSE:
					body.setPurpose(value.toString());
					break;
				default:
					break;
				}
			}
		} catch (JSONException e) {
			throw new JsonParseException(
					"unsupported body deserialization for json represetnation: " + valueObject + " " + e.getMessage());
		}

		return body;
	}

	private BodyTypes guesBodyInternalType(JSONObject valueObject, MotivationTypes motivation) {
		switch (motivation) {
		case LINKING:
			return BodyTypes.LINK;
		case TAGGING:
			// simple resource (semantic) tag - extended
			// specific resource - minimal or extended;
			// in any case SemanticTag
			// support both @id and id in input
			if (valueObject.has(WebAnnotationFields.ID) || valueObject.has(WebAnnotationFields.SOURCE))
				return BodyTypes.SEMANTIC_TAG;
			else if (valueObject.has(WebAnnotationFields.TEXT))
				return BodyTypes.TAG;

		default:
			break;

		}

		throw new AnnotationAttributeInstantiationException(
				"Cannot find appropriate body type with MotivationType: " + motivation);

	}

	private Body parseBody(JSONArray valueObject, MotivationTypes motivation) throws JsonParseException {

		BodyTypes bodyType = null;
		Body body = null;
		try {
			for (int i = 0; i < valueObject.length(); i++) {
				String val = valueObject.getString(i);

				// initialize body object
				if (i == 0) {
					bodyType = guesBodyInternalType(motivation, val);
					body = BodyObjectFactory.getInstance().createObjectInstance(bodyType);
				}

				body.addValue(val);
				String resourceId = getIdHelper().buildResourseId(getIdHelper().extractResoureIdPartsFromHttpUri(val));
				body.addResourceId(resourceId);
			}
		} catch (JSONException e) {
			throw new JsonParseException(
					"unsupported body deserialization for json represetnation: " + valueObject + " " + e.getMessage());
		}
		return body;
	}

	public AnnotationIdHelper getIdHelper() {
		if (idHelper == null)
			idHelper = new AnnotationIdHelper();
		return idHelper;
	}

}
