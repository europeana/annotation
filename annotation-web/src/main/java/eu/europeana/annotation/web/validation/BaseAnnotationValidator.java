package eu.europeana.annotation.web.validation;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.xml.sax.SAXParseException;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.Address;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.agent.impl.EdmAgent;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.PlaceBody;
import eu.europeana.annotation.definitions.model.body.impl.EdmAgentBody;
import eu.europeana.annotation.definitions.model.body.impl.VcardAddressBody;
import eu.europeana.annotation.definitions.model.entity.Place;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.resource.selector.RDFStatementSelector;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;
import eu.europeana.annotation.definitions.model.vocabulary.ResourceTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.fulltext.exception.MediaTypeValidationException;
import eu.europeana.annotation.fulltext.subtitles.SubtitleHandler;
import eu.europeana.annotation.fulltext.transcription.TranscriptionFormatValidator;
import eu.europeana.annotation.fulltext.transcription.XmlValidationErrorCollector;
import eu.europeana.annotation.utils.GeneralUtils;
import eu.europeana.annotation.web.exception.request.ParamValidationI18NException;
import eu.europeana.annotation.web.exception.request.PropertyValidationException;
import eu.europeana.annotation.web.exception.request.RequestBodyValidationException;
import eu.europeana.annotation.web.model.vocabulary.UserRoles;
import eu.europeana.annotation.web.service.authorization.AnnotationAuthorizationUtils;
import eu.europeana.annotation.web.service.impl.SearchApiClient;
import eu.europeana.api.common.config.I18nConstantsAnnotation;
import eu.europeana.api.commons.definitions.config.i18n.I18nConstants;
import eu.europeana.api.commons.oauth2.model.impl.EuropeanaApiCredentials;

public abstract class BaseAnnotationValidator {

  private static final String TARGET_SOURCE = "target.source";

  private static final String TARGET_SCOPE = "target.scope";
  
  private static final String TARGET_SELECTOR = "target.selector";
  
  private static final String TARGET_SELECTOR_HAS_PREDICATE = "target.selector.hasPredicate";
  
  private static final String TARGET_SELECTOR_HAS_SUBJECT = "target.selector.hasSubject";
  
  private static final String TARGET_SELECTOR_REFINED_BY_EXACT = "target.selector.refinedBy.exact";

  private static final String BODY_EDM_RIGHTS = "body.edmRights";

  private static final String TARGET = "target";

  private static final String BODY_FORMAT = "body.format";

  private static final String BODY_VALUE = "body.value";

  private static final String BODY = "body";

  private static final String TRANSCRIPTION_BODY_VALUE = "transcription.body.value";

  private static final String TRANSCRIPTION_BODY_LANGUAGE = "transcription.body.language";

  private static final String TRANSCRIPTION_BODY_FORMAT = "transcription.body.format";

  private static final String BODY_SOURCE = "body.source";

  private static final String BODY_TYPE = "body.type";

  private static final String TAG_BODY_LONGITUDE = "tag.body.longitude";

  private static final String TAG_BODY_LATITUDE = "tag.body.latitude";

  private static final String AGENT_BODY_PREF_LABEL = "agent.body.prefLabel";


  @Resource(name = "subtitleHandler")
  private SubtitleHandler subtitleHandler;

  @Resource(name = "mediaFormatValidator")
  private TranscriptionFormatValidator mediaFormatValidator;

  protected abstract AnnotationConfiguration getConfiguration();

  protected boolean validateLinkingAgainstWhitelist(String value)
      throws ParamValidationI18NException {
    // enforce subclasses to overwrite implementation when needed
    return value == null;
  }


  /**
   * This method validates entity body.
   * 
   * @param body The entity body
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  void validateAgentBody(Body body)
      throws ParamValidationI18NException, PropertyValidationException {
    if (body.getType() == null || body.getType().size() != 1) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_TYPE});

    } else if (!ResourceTypes.AGENT.hasJsonValue(body.getType().get(0))) {
      // only full text resources accepted
      throw new PropertyValidationException(I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          I18nConstantsAnnotation.INVALID_PROPERTY_VALUE, new String[] {BODY_TYPE});
    }

    validateAgentEntityBody(body);
  }

  /**
   * Semantic tagging with entity descriptions (edm:Agent) has mandatory field skos:prefLabel and
   * one of the following fields: rdaGr2:professionOrOccupation, edm:begin, rdaGr2:dateOfBirth,
   * edm:end, rdaGr2:dateOfDeath, rdaGr2:placeOfDeath, rdaGr2:placeOfBirth
   * 
   * @param body
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  void validateAgentEntityBody(Body body) throws PropertyValidationException {
    EdmAgent agent = (EdmAgent) ((EdmAgentBody) body).getAgent();

    // check mandatory field prefLabel
    if (agent.getPrefLabel() == null)
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {AGENT_BODY_PREF_LABEL});

    // check mandatory field type
    if (body.getType() == null || StringUtils.isBlank(body.getType().get(0)))
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_TYPE});

  }

  void validateGeoTag(Body body) throws ParamValidationI18NException {
    if (!(body instanceof PlaceBody))
      throw new ParamValidationI18NException(I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          new String[] {BODY_TYPE, ResourceTypes.PLACE.toString()});

    Place place = ((PlaceBody) body).getPlace();

    if (StringUtils.isEmpty(place.getLatitude())) {
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TAG_BODY_LATITUDE});
    }

    if (StringUtils.isEmpty(place.getLongitude())) {
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TAG_BODY_LONGITUDE});
    }

  }

  void validateTagWithSpecificResource(Body body) throws ParamValidationI18NException {
    // check mandatory fields
    if (StringUtils.isBlank(body.getInternalType()))
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_TYPE});
    if (StringUtils.isBlank(body.getSource()))
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_SOURCE});

    validateSpecificResource(body, BODY, false);
  }

  private void validateSpecificResource(SpecificResource resource, String fieldName,
      boolean isScopeMandatory) throws ParamValidationI18NException {
    // source must be an URL
    if (resource.getSource() == null || !GeneralUtils.isUrl(resource.getSource())) {
      throw new ParamValidationI18NException(
          ParamValidationI18NException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
          I18nConstantsAnnotation.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
          new String[] {fieldName + ".source", resource.getSource()});
    }

    // id is not a mandatory field but if exists it must be an URL
    if (resource.getHttpUri() != null && !GeneralUtils.isUrl(resource.getHttpUri())) {
      throw new ParamValidationI18NException(
          ParamValidationI18NException.MESSAGE_INVALID_TAG_ID_FORMAT,
          I18nConstantsAnnotation.MESSAGE_INVALID_TAG_ID_FORMAT,
          new String[] {fieldName + ".httpUri", resource.getHttpUri()});
    }

    if (resource.getScope() == null) {
      // scope exists
      if (isScopeMandatory) {
        throw new ParamValidationI18NException(
            I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
            I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
            new String[] {fieldName + ".scope"});
      }
    } else {
      if (!GeneralUtils.isUrl(resource.getScope())) {
        throw new ParamValidationI18NException(
            ParamValidationI18NException.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
            I18nConstantsAnnotation.MESSAGE_INVALID_TAG_SPECIFIC_RESOURCE,
            new String[] {fieldName + ".scope", resource.getScope()});
      }
    }
  }

  /**
   * The "language", "edmRights" and "value" of the transcribing body are mandatory and "source"
   * becomes mandatory as soon as you have a "scope" in the target
   * 
   * @param body
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   * @throws RequestBodyValidationException
   */
  protected void validateTranscriptionBodyWithFullTextResource(Body body, Target target,
      Authentication authentication) throws ParamValidationI18NException,
      PropertyValidationException, RequestBodyValidationException {
    // the body type shouldn't be null at this stage
    validateFullTextResource(body);

    // check mandatory field edmRights
    validateEdmRights(body, target, authentication);

    // validate format compliance
    String bodyFormat = body.getContentType();
    // check mandatory field format
    if (StringUtils.isBlank(bodyFormat)) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TRANSCRIPTION_BODY_FORMAT});
    }

    validateTranscriptionBody(body);
  }

  private void validateTranscriptionBody(Body body) throws PropertyValidationException {
    String mimeType = body.getContentType();
    try {
      XmlValidationErrorCollector xmlErrorCollector =
          mediaFormatValidator.validateMediaFormat(body.getValue(), mimeType);
      if (xmlErrorCollector != null && xmlErrorCollector.hasErrors()) {
        throw new PropertyValidationException(
            I18nConstantsAnnotation.ANNOTATION_INVALID_MEDIA_FORMAT,
            I18nConstantsAnnotation.ANNOTATION_INVALID_MEDIA_FORMAT,
            new String[] {xmlErrorCollector.getValidationErrors()});
      }
    } catch (MediaTypeValidationException e) {
      HttpStatus responseStatus =
          isXmlParseException(e) ? HttpStatus.BAD_REQUEST : HttpStatus.UNSUPPORTED_MEDIA_TYPE;
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_TRANSCRIPTION_VALIDATION_ERROR,
          I18nConstantsAnnotation.ANNOTATION_TRANSCRIPTION_VALIDATION_ERROR,
          new String[] {mimeType}, responseStatus, e);
    }
  }

  private boolean isXmlParseException(MediaTypeValidationException e) {
    return e.getCause() != null && e.getCause() instanceof SAXParseException;
  }

  private void validateFullTextResource(Body body) throws PropertyValidationException {
    if (body.getType() == null || body.getType().size() != 1) {
      // (external) Type is mandatory
      // temporarily commented out to verify if type is mandatory
      // throw new
      // PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
      // I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {
      // "transcription.body.type" });

    } else if (!ResourceTypes.FULL_TEXT_RESOURCE.hasJsonValue(body.getType().get(0))) {
      // only full text resources accepted
      throw new PropertyValidationException(I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          new String[] {BODY_TYPE, ResourceTypes.FULL_TEXT_RESOURCE.getJsonValue()});
    }
    // check mandatory field language
    if (StringUtils.isBlank(body.getLanguage())) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TRANSCRIPTION_BODY_LANGUAGE});
    }

    // check mandatory field value
    if (StringUtils.isBlank(body.getValue())) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TRANSCRIPTION_BODY_VALUE});
    }
  }

  /**
   * @param body
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  protected void validateVcardAddressBody(Body body)
      throws ParamValidationI18NException, PropertyValidationException {
    // check mandatory fields
    validateSemanticTagVcardAddressBody(body);
  }

  /**
   * Semantic tagging with vcard:Address type has mandatory fields: "Address", "streetAddress",
   * "locality", "countryName" The "type" field should have value "Address"
   * 
   * @param body
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  void validateSemanticTagVcardAddressBody(Body body) throws PropertyValidationException {

    // check type
    if (body.getType() == null || body.getType().size() != 1) {
      // (external) Type is mandatory
      // temporarily commented out to verify if type is mandatory
      // throw new
      // PropertyValidationException(I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD,
      // I18nConstants.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {
      // "vcardAddress.body.type" });
    } else if (!ResourceTypes.VCARD_ADDRESS.hasJsonValue(body.getType().get(0))) {
      // only full text resources accepted
      throw new PropertyValidationException(I18nConstantsAnnotation.INVALID_PROPERTY_VALUE,
          I18nConstantsAnnotation.INVALID_PROPERTY_VALUE, new String[] {"vcardAddress.body.type"});
    }

    Address address = ((VcardAddressBody) body).getAddress();

    // check mandatory field streetAddress
    if (StringUtils.isBlank(address.getVcardStreetAddress()))
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {"tag.body.address.streetAddress"});

    // check mandatory field locality
    if (StringUtils.isBlank(address.getVcardLocality()))
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {"tag.body.address.locality"});

    // check mandatory field countryName
    if (StringUtils.isBlank(address.getVcardCountryName()))
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {"tag.body.address.countryName"});

    // check mandatory field type
    if (body.getType() == null || StringUtils.isBlank(body.getType().get(0)))
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {"tag.body.address.type"});
  }

  /**
   * For describing annotations: "value" and "language" within the "body" are mandatory.
   * 
   * @param body
   * @param isLanguageMandatory Flag for the cases when language is not mandatory
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  protected void validateTextualBody(Body body, boolean isLanguageMandatory)
      throws ParamValidationI18NException, PropertyValidationException {
    // check mandatory field value
    if (StringUtils.isBlank(body.getValue()))
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {"tag.body.value"});

    // check mandatory field language
    if (isLanguageMandatory && StringUtils.isBlank(body.getLanguage()))
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {"tag.body.language"});

    // check type
    if (body.getType() == null || !isTextualBodyType(body)) {
      throw new ParamValidationI18NException(
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_TYPE});
    }
  }

  private boolean isTextualBodyType(Body body) {
    return ResourceTypes.EXTERNAL_TEXT.hasJsonValue(body.getType().get(0))
        || ResourceTypes.TEXTUAL_BODY.hasJsonValue(body.getType().get(0));
  }

  void validateTagWithValue(Body body) throws ParamValidationI18NException {
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

    int MaxTagLength = 64;

    if (GeneralUtils.isUrl(value))
      throw new ParamValidationI18NException(
          ParamValidationI18NException.MESSAGE_INVALID_SIMPLE_TAG,
          I18nConstantsAnnotation.MESSAGE_INVALID_SIMPLE_TAG, new String[] {value});
    else if (value.length() > MaxTagLength)
      throw new ParamValidationI18NException(ParamValidationI18NException.MESSAGE_INVALID_TAG_SIZE,
          I18nConstantsAnnotation.MESSAGE_INVALID_TAG_SIZE,
          new String[] {String.valueOf(value.length())});
  }

  /**
   * 
   * @param webAnnotation
   * @throws ParamValidationI18NException
   * @throws RequestBodyValidationException
   * @throws PropertyValidationException
   */
  public void validateWebAnnotation(Annotation webAnnotation, Authentication authentication)
      throws ParamValidationI18NException, RequestBodyValidationException,
      PropertyValidationException {

    // validate canonical to be an absolute URI
    if (webAnnotation.getCanonical() != null) {
      try {
        URI cannonicalUri = URI.create(webAnnotation.getCanonical());
        if (!cannonicalUri.isAbsolute())
          throw new ParamValidationI18NException("The canonical URI is not absolute:",
              I18nConstantsAnnotation.ANNOTATION_VALIDATION,
              new String[] {WebAnnotationFields.CANONICAL, webAnnotation.getCanonical()});
      } catch (IllegalArgumentException e) {
        throw new ParamValidationI18NException("Error when validating canonical URI:",
            I18nConstantsAnnotation.ANNOTATION_VALIDATION,
            new String[] {WebAnnotationFields.CANONICAL, webAnnotation.getCanonical()}, e);
      }
    }

    // validate via to be valid URL(s)
    if (webAnnotation.getVia() != null && webAnnotation.getVia() instanceof String[]) {
      for (String via : webAnnotation.getVia()) {
        if (!(GeneralUtils.isUrl(via)))
          throw new ParamValidationI18NException("This is not a valid URL:",
              I18nConstantsAnnotation.ANNOTATION_VALIDATION,
              new String[] {WebAnnotationFields.VIA, via});
      }
    }

    switch (webAnnotation.getMotivationType()) {
      case LINKING:
    	  validateLinking(webAnnotation);
    	  break;
      case DESCRIBING:
    	  validateDescribing(webAnnotation);
    	  break;
      case TAGGING:
    	  validateTag(webAnnotation);
    	  break;
      case TRANSCRIBING:
      case TRANSLATING:
    	  validateTranscriptionOrTranslation(webAnnotation, authentication);
    	  break;        
      case SUBTITLING:
      case CAPTIONING: 
    	  validateSubtitleOrCaption(webAnnotation, authentication);
    	  break;
      case LINKFORCONTRIBUTING:
    	  validateLinkForContributing(webAnnotation);
    	  break;
      case HIGHLIGHTING:
          validateDebias(webAnnotation);
          break;        
      default:
        break;
    }
  }

  /**
   * This method verifies if provided right is in a list of valid licenses
   * 
   * @param webAnnotation The right provided in the input from annotation object
   * @return true if provided right is in a list of valid licenses
   * @throws ParamValidationI18NException
   * @throws RequestBodyValidationException
   * @throws PropertyValidationException
   */
  void validateEdmRights(Body body, Target target, Authentication authentication)
      throws RequestBodyValidationException, PropertyValidationException {
    // rights are mandatory
    if (StringUtils.isBlank(body.getEdmRights())) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_EDM_RIGHTS});
    }

    // skip license verification for publisher
    if (AnnotationAuthorizationUtils.hasRole(authentication, UserRoles.publisher.getName())) {
      return;
    }
    // if rights are provided, check if it belongs to the valid license list
    String licence = extractMainLicence(body.getEdmRights());
    Set<String> rights = getConfiguration().getAcceptedLicenceses();
    if (rights.contains(licence) || isContentOwner(target, authentication)) {
      // open license are valid
      // content owners can provide own license
      return; 
    } 
    
    //otherwise, license is invalid
    throw new RequestBodyValidationException(body.getInputString(),
          I18nConstants.INVALID_PARAM_VALUE, new String[] {BODY_EDM_RIGHTS, body.getEdmRights()});
  }

  private String extractMainLicence(@NonNull String rightsClaim){
    // remove version from the right and get licenses
    char PathDelimiter = '/';
    long delimiterCount = rightsClaim.chars().filter(ch -> ch == PathDelimiter).count();

    if (delimiterCount < 6 || !rightsClaim.endsWith("" + PathDelimiter)) {
      //proprietary licence format, not a creative commons 
      return rightsClaim;
    } else {
      // remove last /
      String licence = rightsClaim.substring(0, rightsClaim.length() - 1);
      // remove version, but preserve last /
      int versionStart = licence.lastIndexOf(PathDelimiter) + 1;
      return licence.substring(0, versionStart);
    }
  }

  private boolean isContentOwner(Target target, Authentication authentication)
      throws PropertyValidationException {
    EuropeanaApiCredentials apiCred = ((EuropeanaApiCredentials) authentication.getCredentials());
      
    try {
      return getSearchApiClient().isRecordsContentProvider(target.getResourceId(),
          apiCred.getAffiliation());
    } catch (IOException e) {
      throw new PropertyValidationException(I18nConstantsAnnotation.SEARCH_API_ACCESS,
          I18nConstantsAnnotation.SEARCH_API_ACCESS, new String[] {target.getResourceId()},
          HttpStatus.BAD_GATEWAY, e);
    }
  }

  protected abstract SearchApiClient getSearchApiClient();

  /**
   * Validation of simple tags.
   * 
   * Pre-processing: Trim spaces. If the tag is encapsulated by double or single quotes, remove
   * these.
   *
   * Validation rules: A maximum of 64 characters is allowed for the tag. Tags cannot be URIs, tags
   * which start with http://, ftp:// or https:// are not allowed.
   *
   * Examples of allowed tags: black, white, "black and white" (will become tag: black and white)
   *
   * @param webAnnotation
   * @throws PropertyValidationException
   */
  protected void validateTag(Annotation webAnnotation)
      throws ParamValidationI18NException, PropertyValidationException {
    // webAnnotation.
    Body body = webAnnotation.getBody();
    validateBodyExists(webAnnotation.getBody());
    if (body.getType() != null && body.getType().contains(WebAnnotationFields.SPECIFIC_RESOURCE)) {
      validateTagWithSpecificResource(body);
    } else if (BodyInternalTypes.isSemanticTagBody(body.getInternalType())) {
      // implement when needed validateSemanticTagUrl
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

  protected void validateLinking(Annotation webAnnotation)
      throws ParamValidationI18NException, PropertyValidationException {

    validateTargetFields(webAnnotation.getTarget());

    // validate target URLs against whitelist
    if (webAnnotation.getTarget().get(0).getValue() != null) {
      validateLinkingAgainstWhitelist(webAnnotation.getTarget().get(0).getValue());
    } else if (webAnnotation.getTarget().get(0).getValues() != null) {
      for (String url : webAnnotation.getTarget().get(0).getValues()) {
        validateLinkingAgainstWhitelist(url);
      }
    }
  }

  /**
   * This method validates describing annotation.
   * 
   * @param webAnnotation
   * @throws ParamValidationI18NException
   * @throws PropertyValidationException
   */
  protected void validateDescribing(Annotation webAnnotation)
      throws ParamValidationI18NException, PropertyValidationException {
    Body body = webAnnotation.getBody();
    validateBodyExists(webAnnotation.getBody());
    if (body.getType() != null
        && !ResourceTypes.EXTERNAL_TEXT.hasJsonValue(body.getType().get(0))) {
      validateTextualBody(body, true);
    }

    validateTargetFields(webAnnotation.getTarget());
  }

  /**
   * Validation of transcriptions.
   * 
   * @param webAnnotation
   * @throws RequestBodyValidationException
   * @throws PropertyValidationException
   */
  protected void validateTranscriptionOrTranslation(Annotation webAnnotation, Authentication authentication)
      throws ParamValidationI18NException, RequestBodyValidationException,
      PropertyValidationException {
    validateBodyExists(webAnnotation.getBody());
    validateTranscriptionBodyWithFullTextResource(webAnnotation.getBody(),
        webAnnotation.getTarget().get(0), authentication);
    // validate target
    validateTargetFields(webAnnotation.getTarget());
  }
  
  protected void validateDebias(Annotation webAnnotation) throws PropertyValidationException {
	  validateBodyExists(webAnnotation.getBody());
	  // validate targets
	  validateMultipleTargets(webAnnotation.getTarget());
  }  

  /**
   * Validation of subtitle.
   * 
   * @param webAnnotation
   * @throws RequestBodyValidationException
   * @throws PropertyValidationException
   */
  protected void validateSubtitleOrCaption(Annotation webAnnotation, Authentication authentication)
      throws ParamValidationI18NException, RequestBodyValidationException,
      PropertyValidationException {

    // validate body
    Body body = webAnnotation.getBody();
    validateBodyExists(body);
    validateFullTextResource(body);
    validateSubtitleBody(body);

    // validate target
    validateTargetFields(webAnnotation.getTarget());

    // check mandatory field edmRights
    validateEdmRights(body, webAnnotation.getTarget().get(0), authentication);
  }

  protected void validateLinkForContributing(Annotation webAnnotation)
      throws RequestBodyValidationException, PropertyValidationException {

    validateBodyExists(webAnnotation.getBody());

    // validate that body url starts with the https
    String bodyUrl = webAnnotation.getBody().getValue();
    if (bodyUrl == null) {
      bodyUrl = webAnnotation.getBody().getHttpUri();
    }

    if (bodyUrl == null || !GeneralUtils.urlStartsWithHttps(bodyUrl)) {
      throw new RequestBodyValidationException(I18nConstants.INVALID_PARAM_VALUE,
          I18nConstants.INVALID_PARAM_VALUE, new String[] {
              "body.id or body.value is mandatory and must use https protocol: ", bodyUrl});
    }

    if (webAnnotation.getTarget() == null) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET});
    }

    // specific resources not allowed
    String scope = webAnnotation.getTarget().get(0).getScope();
    String source = webAnnotation.getTarget().get(0).getSource();
    if (scope != null || source != null) {
      throw new RequestBodyValidationException(I18nConstants.INVALID_PARAM_VALUE,
          I18nConstants.INVALID_PARAM_VALUE,
          new String[] {"target.scope and target.source are not allowed for this annotation type: ",
              "[ scope: " + scope + ", source: " + source + "]"});
    }

    // target.id/httpUri is mandatory, however the check for specific resources takes precedence
    if (StringUtils.isBlank(webAnnotation.getTarget().get(0).getHttpUri())) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET});
    }

    // validate base URLs
    validateTargetFields(webAnnotation.getTarget());
  }

  private void validateSubtitleBody(Body body) throws PropertyValidationException {
    // check mandatory field body.format (please note that this field is saved in the "contentType"
    // field of the given Resource)
    if (StringUtils.isBlank(body.getContentType())) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY_FORMAT});
    }
    // check if the body.format field has a valid value
    if (!subtitleHandler.hasSubtitleFormat(body.getContentType())) {
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_INVALID_SUBTITLES_FORMATS,
          I18nConstantsAnnotation.ANNOTATION_INVALID_SUBTITLES_FORMATS, new String[] {BODY_FORMAT});
    }
    // check if the body.value is valid
    try {
      subtitleHandler.parseSubtitle(body.getValue(), body.getContentType());
    } catch (Throwable e) {
      // FileFormatException | IOException are known exceptions but also runtime exceptions might be
      // thrown
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_INVALID_SUBTITLES_VALUE,
          I18nConstantsAnnotation.ANNOTATION_INVALID_SUBTITLES_VALUE, new String[] {BODY_VALUE}, e);
    }
  }

  private void validateBodyExists(Body body) throws PropertyValidationException {
    // the body must either have a value or an httpUri which is set from the id field from the body
    // json input
    // if (body == null || (body.getHttpUri() == null && body.getValue() == null)) {
    if (body == null) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {BODY});
    }
  }
  
  private void validateMultipleTargets(List<Target> targets) throws PropertyValidationException {
	  if(targets==null || targets.isEmpty()) {
	      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
	              I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET});		  
	  }
	  /* for now only the specific resource can be in the multiple targets,
	   * so the same check as in the validateTargetFields method 
	   */
	  for(Target target : targets) {
		  if (target.getSource()!=null || target.getScope()!=null || target.getSelector()!=null) {
			  validateTargetSpecificResource(target);
		  }
	  }
  }

  private void validateTargetFields(List<Target> targets) throws PropertyValidationException {
    // validates that the target value/values/scope (depending on what provided) has a valid base
    // url
    // target is mandatory field
    if (targets == null) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET});
    }

    for(Target target : targets) {
		if (target.getValue() != null) {
		  // validate simple target
		  validateTargetSimpleValue(target);
		} else if (target.getValues() != null) {
		  // validate multiple target values
		  validateTargetMultipleValues(target);
		} else if (target.getSource()!=null || target.getScope()!=null || target.getSelector()!=null) {
		  // validate target for specific resource
		  validateTargetSpecificResource(target);
		}
    }
  }

  private void validateTargetSpecificResource(Target target) throws PropertyValidationException {
    // source must both be present in the target, and beside it (scope or selector) as well
    if (target.getSource() == null) {
      throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
          new String[] {TARGET_SOURCE});
    }
    if (target.getScope()==null && target.getSelector()==null) {
        throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
            I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
            new String[] {TARGET_SCOPE + " or " + TARGET_SELECTOR});
    }
    
    //validate target selectors
    if(target.getSelector()!=null) {
    	validateTargetSelectors(target.getSelector());
    }

    if (target.getScope()!=null && !target.getScope().contains(getConfiguration().getAnnoItemDataEndpoint())) {
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
          I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
          new String[] {getConfiguration().getAnnoItemDataEndpoint()});
    }
    // target.source must be a valid url
    if (!GeneralUtils.isUrl(target.getSource())) {
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_INVALID_URL,
          I18nConstantsAnnotation.ANNOTATION_INVALID_URL, new String[] {TARGET_SOURCE});
    }
  }
  
  private void validateTargetSelectors(Selector selector) throws PropertyValidationException {
	  if(WebAnnotationFields.RDF_STATEMENT_SELECTOR.equals(selector.getSelectorType())) {
		  RDFStatementSelector rdfStatSel=(RDFStatementSelector) selector;
		  if(StringUtils.isBlank(rdfStatSel.getHasPredicate())) {
			  throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
					  I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET_SELECTOR_HAS_PREDICATE});			  
		  }
		  
		  //the exact field in the refinedBy field is mandatory
		  if(rdfStatSel.getRefinedBy()!=null) {
			  if(rdfStatSel.getRefinedBy().getExact()==null) {
				  throw new PropertyValidationException(I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD,
						  I18nConstantsAnnotation.MESSAGE_MISSING_MANDATORY_FIELD, new String[] {TARGET_SELECTOR_REFINED_BY_EXACT});			  				  
			  }
		  }
		  
		  //if hasSubject exists, it must be a URI
		  if(! StringUtils.isBlank(rdfStatSel.getHasSubject())) {
			  if(! GeneralUtils.isUrl(rdfStatSel.getHasSubject())) {
			      throw new PropertyValidationException(I18nConstantsAnnotation.ANNOTATION_INVALID_URL,
			    		  I18nConstantsAnnotation.ANNOTATION_INVALID_URL, new String[] {TARGET_SELECTOR_HAS_SUBJECT});
			  }
		  }
	  }	  
  }  

  private void validateTargetMultipleValues(Target target) throws PropertyValidationException {
    for (String targetValue : target.getValues()) {
      if (!targetValue.contains(getConfiguration().getAnnoItemDataEndpoint()))
        throw new PropertyValidationException(
            I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
            I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
            new String[] {getConfiguration().getAnnoItemDataEndpoint()});

    }
  }

  private void validateTargetSimpleValue(Target target) throws PropertyValidationException {
    if (!target.getValue().contains(getConfiguration().getAnnoItemDataEndpoint()))
      throw new PropertyValidationException(
          I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
          I18nConstantsAnnotation.ANNOTATION_INVALID_TARGET_BASE_URL,
          new String[] {getConfiguration().getAnnoItemDataEndpoint()});
  }
}
