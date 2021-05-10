package eu.europeana.annotation.web.service.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.utils.UriUtils;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.api.common.config.I18nConstants;
import eu.europeana.corelib.definitions.edm.entity.Address;

public abstract class BaseAnnotationValidator {

    protected abstract AnnotationConfiguration getConfiguration();

    protected abstract boolean validateResource(String value) throws ParamValidationException;

    /**
     * This method validates entity body.
     * 
     * @param body The entity body
     * @throws ParamValidationException
     * @throws PropertyValidationException
     */
    protected void validateAgentBody(Body body) throws ParamValidationException, PropertyValidationException {
    Assert.notNull(body, "The body field should not be null.");
	if (body.getType() == null || !(body.getType().size() == 1)) {
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "agent.body.type" });

	} else if (!ResourceTypes.AGENT.hasJsonValue(body.getType().get(0))) {
	    // only full text resources accepted
	    throw new PropertyValidationException(I18nConstants.INVALID_PROPERTY_VALUE,
		    I18nConstants.INVALID_PROPERTY_VALUE, new String[] { "agent.body.type" });
	}

	validateAgentEntityBody(body);
    }

    /**
     * Semantic tagging with entity descriptions (edm:Agent) has mandatory field
     * skos:prefLabel and one of the following fields:
     * rdaGr2:professionOrOccupation, edm:begin, rdaGr2:dateOfBirth, edm:end,
     * rdaGr2:dateOfDeath, rdaGr2:placeOfDeath, rdaGr2:placeOfBirth
     * 
     * @param body
     * @throws ParamValidationException
     * @throws PropertyValidationException
     */
    protected void validateAgentEntityBody(Body body) throws ParamValidationException, PropertyValidationException {
    Assert.notNull(body, "The body field should not be null.");	
	EdmAgent agent = (EdmAgent) ((EdmAgentBody) body).getAgent();

	// check mandatory field prefLabel
	if (agent.getPrefLabel() == null)
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "agent.body.prefLabel" });

	// check mandatory field type
	if (body.getType() == null || StringUtils.isBlank(body.getType().get(0)))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "agent.body.type" });

	// check mandatory field - one of the professionOrOccupation, begin,
	// dateOfBirth, end, dateOfDeath
	// placeOfBirth, placeOfDeath
	// if (agent.getPlaceOfBirth() == null && agent.getPlaceOfDeath() == null
	// && agent.getDateOfDeath() == null && agent.getDateOfDeath() == null)
	// throw new
	// ParamValidationException(ParamValidationException.MESSAGE_MISSING_MANDATORY_FIELD,
	// I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
	// new String[] { "agent.body.fields",
	// "missing one of the professionOrOccupation, begin, dateOfBirth, end,
	// dateOfDeath, placeOfBirth, placeOfDeath" });
    }

    protected void validateGeoTag(Body body) throws ParamValidationException {
    Assert.notNull(body, "The body field should not be null.");
	if (!(body instanceof PlaceBody))
	    throw new ParamValidationException(I18nConstants.INVALID_PROPERTY_VALUE,
		    I18nConstants.INVALID_PROPERTY_VALUE,
		    new String[] { "tag.body.type", ResourceTypes.PLACE.toString() });

	Place place = ((PlaceBody) body).getPlace();

	if (StringUtils.isEmpty(place.getLatitude()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.latitude" });

	if (StringUtils.isEmpty(place.getLongitude()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.longitude" });

    }

    protected void validateTagWithSpecificResource(Body body) throws ParamValidationException {
    Assert.notNull(body, "The body field should not be null.");
	// check mandatory fields
	if (StringUtils.isBlank(body.getInternalType().toString()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.type" });
	if (StringUtils.isBlank(body.getSource()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.source" });

	// source must be an URL
	if (!UriUtils.isUrl(body.getSource()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
		    I18nConstants.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
		    new String[] { "tag.format", body.getSource() });

	// id is not a mandatory field but if exists it must be an URL
	if (body.getHttpUri() != null && !UriUtils.isUrl(body.getHttpUri()))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_ID_FORMAT,
		    I18nConstants.MESSAGE_INVALID_TAG_ID_FORMAT,
		    new String[] { "tag.body.httpUri", body.getHttpUri() });
    }

    /**
     * The "language", "edmRights" and "value" of the transcribing body are
     * mandatory and "source" becomes mandatory as soon as you have a "scope" in the
     * target
     * 
     * @param body
     * @throws ParamValidationException
     * @throws PropertyValidationException
     * @throws RequestBodyValidationException
     */
    protected void validateTranscriptionWithFullTextResource(Body body)
	    throws ParamValidationException, PropertyValidationException, RequestBodyValidationException {
	// the body type shouldn't be null at this stage
	validateFullTextResource(body);

	// check mandatory field edmRights
	if (StringUtils.isBlank(body.getEdmRights())) {
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "transcription.body.edmRights" });
	}
	validateEdmRights(body);
    }

    private void validateFullTextResource(Body body) throws PropertyValidationException {
    Assert.notNull(body, "The body field should not be null.");
	if (body.getType() == null || !(body.getType().size() == 1)) {
	    // (external) Type is mandatory
	    // temporarily commented out to verify if type is mandatory
	    // throw new
	    // PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
	    // I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {
	    // "transcription.body.type" });

	} else if (!ResourceTypes.FULL_TEXT_RESOURCE.hasJsonValue(body.getType().get(0))) {
	    // only full text resources accepted
	    throw new PropertyValidationException(I18nConstants.INVALID_PROPERTY_VALUE,
		    I18nConstants.INVALID_PROPERTY_VALUE,
		    new String[] { "transcription.body.type", ResourceTypes.FULL_TEXT_RESOURCE.getJsonValue() });
	}
	// check mandatory field language
	if (StringUtils.isBlank(body.getLanguage())) {
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "transcription.body.language" });
	}

	// check mandatory field value
	if (StringUtils.isBlank(body.getValue())) {
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "transcription.body.value" });
	}
    }

    /**
     * @param body
     * @throws ParamValidationException
     * @throws PropertyValidationException
     */
    protected void validateVcardAddressBody(Body body) throws ParamValidationException, PropertyValidationException {
	// check mandatory fields
	validateSemanticTagVcardAddressBody(body);
    }

    /**
     * Semantic tagging with vcard:Address type has mandatory fields: "Address",
     * "streetAddress", "locality", "countryName" The "type" field should have value
     * "Address"
     * 
     * @param body
     * @throws ParamValidationException
     * @throws PropertyValidationException
     */
    protected void validateSemanticTagVcardAddressBody(Body body)
	    throws ParamValidationException, PropertyValidationException {
    Assert.notNull(body, "The body field should not be null.");
	// check type
	if (body.getType() == null || !(body.getType().size() == 1)) {
	    // (external) Type is mandatory
	    // temporarily commented out to verify if type is mandatory
	    // throw new
	    // PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
	    // I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {
	    // "vcardAddress.body.type" });
	} else if (!ResourceTypes.VCARD_ADDRESS.hasJsonValue(body.getType().get(0))) {
	    // only full text resources accepted
	    throw new PropertyValidationException(I18nConstants.INVALID_PROPERTY_VALUE,
		    I18nConstants.INVALID_PROPERTY_VALUE, new String[] { "vcardAddress.body.type" });
	}

	Address address = ((VcardAddressBody) body).getAddress();

	// check mandatory field streetAddress
	if (StringUtils.isBlank(address.getVcardStreetAddress()))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.address.streetAddress" });

	// check mandatory field locality
	if (StringUtils.isBlank(address.getVcardLocality()))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.address.locality" });

	// check mandatory field countryName
	if (StringUtils.isBlank(address.getVcardCountryName()))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.address.countryName" });

	// check mandatory field type
	if (body.getType() == null || StringUtils.isBlank(body.getType().get(0)))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.address.type" });
    }

    /**
     * For describing annotations: "value" and "language" within the "body" are
     * mandatory.
     * 
     * @param body
     * @param isLanguageMandatory Flag for the cases when language is not mandatory
     * @throws ParamValidationException
     */
    protected void validateTextualBody(Body body, boolean isLanguageMandatory) throws ParamValidationException {
    Assert.notNull(body, "The body field should not be null.");
	// check mandatory field value
	if (StringUtils.isBlank(body.getValue()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.value" });

	// check mandatory field language
	if (isLanguageMandatory && StringUtils.isBlank(body.getLanguage()))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.language" });

	// check type
	if (body.getType() == null || !ResourceTypes.EXTERNAL_TEXT.hasJsonValue(body.getType().get(0)))
	    throw new ParamValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "tag.body.type" });
    }

    protected void validateTagWithValue(Body body) throws ParamValidationException {
    Assert.notNull(body, "The body field should not be null.");
	String value = body.getValue();

	value = value.trim();
	// remove leading and end quotes
	if (value.startsWith("\"")) {
	    int secondPosition = 1;
	    value = value.substring(secondPosition);
	    value = value.trim();
	}

	if (value.endsWith("\"")) {
	    int secondLastPosition = value.length() - 1;
	    value = value.substring(0, secondLastPosition);
	    value = value.trim();
	}

	// reset the tag value with the trimmed value
	body.setValue(value);

	int MAX_TAG_LENGTH = 64;

	if (UriUtils.isUrl(value))
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_SIMPLE_TAG,
		    I18nConstants.MESSAGE_INVALID_SIMPLE_TAG, new String[] { value });
	else if (value.length() > MAX_TAG_LENGTH)
	    throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_TAG_SIZE,
		    I18nConstants.MESSAGE_INVALID_TAG_SIZE, new String[] { String.valueOf(value.length()) });
    }

    protected void validateSemanticTagUrl(Body body) {
	// TODO Add whitelist based validation here

    }

    public void validateWebAnnotation(Annotation webAnnotation)
	    throws ParamValidationException, RequestBodyValidationException, PropertyValidationException {

	// validate canonical to be an absolute URI
	if (webAnnotation.getCanonical() != null) {
	    try {
		URI cannonicalUri = URI.create(webAnnotation.getCanonical());
		if (!cannonicalUri.isAbsolute())
		    throw new ParamValidationException("The canonical URI is not absolute:",
			    I18nConstants.ANNOTATION_VALIDATION,
			    new String[] { WebAnnotationFields.CANONICAL, webAnnotation.getCanonical() });
	    } catch (IllegalArgumentException e) {
		throw new ParamValidationException("Error when validating canonical URI:",
			I18nConstants.ANNOTATION_VALIDATION,
			new String[] { WebAnnotationFields.CANONICAL, webAnnotation.getCanonical() }, e);
	    }
	}

	// validate via to be valid URL(s)
	if (webAnnotation.getVia() != null) {
	    if (webAnnotation.getVia() instanceof String[]) {
		for (String via : webAnnotation.getVia()) {
		    if (!(UriUtils.isUrl(via)))
			throw new ParamValidationException("This is not a valid URL:",
				I18nConstants.ANNOTATION_VALIDATION, new String[] { WebAnnotationFields.VIA, via });
		}
	    }
	}

	switch (webAnnotation.getMotivationType()) {
	case LINKING:
	    // validate target URLs against whitelist
	    if (webAnnotation.getTarget() != null) {
		if (webAnnotation.getTarget().getValue() != null)
		    validateResource(webAnnotation.getTarget().getValue());

		if (webAnnotation.getTarget().getValues() != null)
		    for (String url : webAnnotation.getTarget().getValues()) {
			validateResource(url);
		    }
	    }
	    break;
	case DESCRIBING:
	    validateDescribing(webAnnotation);
	case TAGGING:
	    validateTag(webAnnotation);
	    break;
	case TRANSCRIBING:
	    validateTranscription(webAnnotation);
	    break;
	case SUBTITLING:
	    validateSubtitleOrCaption(webAnnotation);
	    break;
	case CAPTIONING:
	    validateSubtitleOrCaption(webAnnotation);
	    break;
	default:
	    break;
	}
    }

    public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException {
    Assert.notNull(annoId, "The annotation id field should not be null.");
	if (annoId.getIdentifier() != null)
	    throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,
		    I18nConstants.MESSAGE_IDENTIFIER_NOT_NULL,
		    new String[] { WebAnnotationFields.IDENTIFIER, annoId.toRelativeUri() });
    }

    /**
     * This method verifies if provided right is in a list of valid licenses
     * 
     * @param webAnnotation The right provided in the input from annotation object
     * @return true if provided right is in a list of valid licenses
     * @throws ParamValidationException
     * @throws RequestBodyValidationException
     */
    protected void validateEdmRights(Body body) throws ParamValidationException, RequestBodyValidationException {
    Assert.notNull(body, "The body field should not be null.");
	// if rights are provided, check if it belongs to the valid license list
	String rightsClaim = body.getEdmRights();
	String licence = null;
	// remove version from the right and get licenses
	char PATH_DELIMITER = '/';
	long delimiterCount = rightsClaim.chars().filter(ch -> ch == PATH_DELIMITER).count();

	if (delimiterCount != 6 || !rightsClaim.endsWith("" + PATH_DELIMITER)) {
	    // wrong format, max 6 (including the / after version, for )
	    throw new RequestBodyValidationException(body.getInputString(), I18nConstants.ANNOTATION_INVALID_RIGHTS,
		    new String[] { rightsClaim });
	} else {
	    // remove last /
	    licence = rightsClaim.substring(0, rightsClaim.length() - 1);
	    // remove version, but preserve last /
	    int versionStart = licence.lastIndexOf(PATH_DELIMITER) + 1;
	    licence = licence.substring(0, versionStart);
	}
	Set<String> rights = getConfiguration().getAcceptedLicenceses();
	if (!rights.contains(licence))
	    throw new RequestBodyValidationException(body.getInputString(), I18nConstants.INVALID_PARAM_VALUE,
		    new String[] { "body.edmRights", rightsClaim });

    }

    /**
     * Validation of simple tags.
     * 
     * Pre-processing: Trim spaces. If the tag is encapsulated by double or single
     * quotes, remove these.
     *
     * Validation rules: A maximum of 64 characters is allowed for the tag. Tags
     * cannot be URIs, tags which start with http://, ftp:// or https:// are not
     * allowed.
     *
     * Examples of allowed tags: black, white, "black and white" (will become tag:
     * black and white)
     *
     * @param webAnnotation
     * @throws PropertyValidationException
     */
    protected void validateTag(Annotation webAnnotation) throws ParamValidationException, PropertyValidationException {
	// webAnnotation.
	Body body = webAnnotation.getBody();
	Assert.notNull(body, "The body field should not be null.");
	// TODO: the body type shouldn't be null at this stage
	if (body.getType() != null && body.getType().contains(WebAnnotationFields.SPECIFIC_RESOURCE)) {
	    validateTagWithSpecificResource(body);
	} else if (BodyInternalTypes.isSemanticTagBody(body.getInternalType())) {
	    validateSemanticTagUrl(body);
	} else if (BodyInternalTypes.isAgentBodyTag(body.getInternalType())) {
	    validateAgentBody(body);
	} else if (BodyInternalTypes.isGeoTagBody(body.getInternalType())) {
	    validateGeoTag(body);
	} else if (BodyInternalTypes.isVcardAddressTagBody(body.getInternalType())) {
	    validateVcardAddressBody(body);
	} else {
	    validateTagWithValue(body);
	}
    }

    /**
     * This method validates describing annotation.
     * 
     * @param webAnnotation
     * @throws ParamValidationException
     */
    protected void validateDescribing(Annotation webAnnotation) throws ParamValidationException {
	Body body = webAnnotation.getBody();
	Assert.notNull(body, "The body field should not be null.");
	if (body.getType() != null && !ResourceTypes.EXTERNAL_TEXT.hasJsonValue(body.getType().get(0))) {
	    validateTextualBody(body, true);
	}
    }

    /**
     * Validation of transcribing.
     * 
     * @param webAnnotation
     * @throws RequestBodyValidationException
     * @throws PropertyValidationException
     */
    protected void validateTranscription(Annotation webAnnotation)
	    throws ParamValidationException, RequestBodyValidationException, PropertyValidationException {
	validateTranscriptionWithFullTextResource(webAnnotation.getBody());

	// validate target
	// TODO consider moving to validateSpecificResource method
	// "source" becomes mandatory as soon as you have a "scope" in the target
	if (webAnnotation.getTarget() != null && !StringUtils.isBlank(webAnnotation.getTarget().getScope())
		&& StringUtils.isBlank(webAnnotation.getTarget().getSource()))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "transcription.target.source" });
    }

    /**
     * Validation of subtitle.
     * 
     * @param webAnnotation
     * @throws RequestBodyValidationException
     * @throws PropertyValidationException
     */
    protected void validateSubtitleOrCaption(Annotation webAnnotation)
	    throws ParamValidationException, RequestBodyValidationException, PropertyValidationException {

	// validate body
	Body body = webAnnotation.getBody();
	validateFullTextResource(body);
	// check mandatory field body.format (please note that this field is saved in the "contentType" field of the given Resource)
	if (StringUtils.isBlank(body.getContentType())) {
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "subtitle.body.format" });
	}
	// check if the body.format field has a valid value
	boolean result = getConfiguration().getAnnotationSubtitlesFormats().contains(body.getContentType());
	if (!result) {
	    throw new PropertyValidationException(I18nConstants.ANNOTATION_INVALID_SUBTITLES_FORMATS,
		    I18nConstants.ANNOTATION_INVALID_SUBTITLES_FORMATS, new String[] { body.getContentType() });
	}
	
	// validate target
	// TODO consider moving to validateSpecificResource method
	// "source" becomes mandatory as soon as you have a "scope" in the target
	if (webAnnotation.getTarget() != null && !StringUtils.isBlank(webAnnotation.getTarget().getScope())
		&& StringUtils.isBlank(webAnnotation.getTarget().getSource()))
	    throw new PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
		    I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] { "transcription.target.source" });
    }

}
