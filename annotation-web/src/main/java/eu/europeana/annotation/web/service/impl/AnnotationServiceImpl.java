package eu.europeana.annotation.web.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.stanbol.commons.exception.JsonParseException;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.exception.AnnotationValidationException;
import eu.europeana.annotation.definitions.exception.ProviderValidationException;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.Provider;
import eu.europeana.annotation.definitions.model.StatusLog;
import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.concept.Concept;
import eu.europeana.annotation.definitions.model.impl.BaseStatusLog;
import eu.europeana.annotation.definitions.model.utils.AnnotationBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationStates;
import eu.europeana.annotation.definitions.model.vocabulary.IdGenerationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.mongo.model.internal.PersistentWhitelist;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.mongo.service.PersistentConceptService;
import eu.europeana.annotation.mongo.service.PersistentProviderService;
import eu.europeana.annotation.mongo.service.PersistentStatusLogService;
import eu.europeana.annotation.mongo.service.PersistentTagService;
import eu.europeana.annotation.mongo.service.PersistentWhitelistService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.AnnotationStateException;
import eu.europeana.annotation.solr.exceptions.StatusLogServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.SolrTagService;
import eu.europeana.annotation.solr.vocabulary.SolrAnnotationConst;
import eu.europeana.annotation.utils.JsonUtils;
import eu.europeana.annotation.utils.parse.AnnotationLdParser;
import eu.europeana.annotation.web.exception.request.ParamValidationException;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.annotation.web.service.authentication.AuthenticationService;

public class AnnotationServiceImpl implements AnnotationService {

	@Resource
	AnnotationConfiguration configuration;

	@Resource
	PersistentAnnotationService mongoPersistance;

	@Resource
	PersistentTagService mongoTagPersistence;

	@Resource
	PersistentProviderService mongoProviderPersistance;

	@Resource
	PersistentConceptService mongoConceptPersistence;

	@Resource
	PersistentWhitelistService mongoWhitelistPersistence;

	@Resource
	PersistentStatusLogService mongoStatusLogPersistence;

	@Resource
	SolrAnnotationService solrService;

	@Resource
	SolrTagService solrTagService;
	
	@Resource
	AuthenticationService authenticationService;

	AnnotationBuilder annotationBuilder;

	Logger logger = Logger.getLogger(getClass());


	public AuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	public AnnotationBuilder getAnnotationHelper() {
		if (annotationBuilder == null)
			annotationBuilder = new AnnotationBuilder();
		return annotationBuilder;
	}

	@Override
	public String getComponentName() {
		return configuration.getComponentName();
	}

	protected AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	protected PersistentAnnotationService getMongoPersistence() {
		return mongoPersistance;
	}

	public void setMongoPersistance(PersistentAnnotationService mongoPersistance) {
		this.mongoPersistance = mongoPersistance;
	}

	public PersistentTagService getMongoTagPersistence() {
		return mongoTagPersistence;
	}

	public void setMongoTagPersistance(PersistentTagService mongoTagPersistence) {
		this.mongoTagPersistence = mongoTagPersistence;
	}

	public PersistentProviderService getMongoProviderPersistence() {
		return mongoProviderPersistance;
	}

	public void setMongoProviderPersistance(PersistentProviderService mongoProviderPersistance) {
		this.mongoProviderPersistance = mongoProviderPersistance;
	}

	public PersistentConceptService getMongoConceptPersistence() {
		return mongoConceptPersistence;
	}

	public void setMongoConceptPersistance(PersistentConceptService mongoConceptPersistence) {
		this.mongoConceptPersistence = mongoConceptPersistence;
	}

	public PersistentWhitelistService getMongoWhitelistPersistence() {
		return mongoWhitelistPersistence;
	}

	public void setMongoWhitelistPersistance(PersistentWhitelistService mongoWhitelistPersistence) {
		this.mongoWhitelistPersistence = mongoWhitelistPersistence;
	}

	public PersistentStatusLogService getMongoStatusLogPersistence() {
		return mongoStatusLogPersistence;
	}

	public void setMongoStatusLogPersistance(PersistentStatusLogService mongoStatusLogPersistence) {
		this.mongoStatusLogPersistence = mongoStatusLogPersistence;
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String resourceId) {
		return getMongoPersistence().getAnnotationList(resourceId);
	}

	@Override
	public List<? extends Annotation> getFilteredAnnotationList(String resourceId, String startOn, String limit,
			boolean isDisabled) {
		return getMongoPersistence().getFilteredAnnotationList(resourceId, null, startOn, limit, isDisabled);
	}


	// @Override
	// public Annotation getAnnotationById(String provider,
	// String identifier) {
	// return getMongoPersistence().find(provider, identifier);
	//
	// }

	@Override
	public  List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException {
		return null;
		//return getSolrService().search(query);
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query, String startOn, String limit)
			throws AnnotationServiceException {
		return null;
		//return getSolrService().search(query, startOn, limit);
	}

	@Override
	public Map<String, Integer> searchAnnotations(String[] qf, List<String> queries) throws AnnotationServiceException {
		return getSolrService().queryFacetSearch(SolrAnnotationConst.ALL_SOLR_ENTRIES, qf, queries);
	}

	@Override
	public List<? extends SolrTag> searchTags(String query) throws TagServiceException {
		return getSolrTagService().search(query);
	}

	@Override
	public List<? extends StatusLog> searchStatusLogs(String query, String startOn, String limit)
			throws StatusLogServiceException {
		return getMongoStatusLogPersistence().getFilteredStatusLogList(query, startOn, limit);
	}

	@Override
	public List<? extends SolrTag> searchTags(String query, String startOn, String limit) throws TagServiceException {
		return getSolrTagService().search(query, startOn, limit);
	}

	@Override
	public Annotation parseAnnotation(String annotationJsonLdStr) {

		/**
		 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser
		 * -> JsonLd object
		 */
		AnnotationLd parsedAnnotationLd = null;
		JsonLd parsedJsonLd = null;
		try {
			parsedJsonLd = JsonLdParser.parseExt(annotationJsonLdStr);

			/**
			 * convert JsonLd to AnnotationLd. JsonLd object -> AnnotationLd
			 * object
			 */
			parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			String errorMessage = "Cannot Parse JSON-LD input! ";
			getLogger().error(errorMessage, e);
		}

		/**
		 * AnnotationLd object -> Annotation object.
		 */
		return parsedAnnotationLd.getAnnotation();
	}

	private Logger getLogger() {
		return logger;
	}

	@Override
	public Annotation parseAnnotationLd(MotivationTypes motivationType, String annotationJsonLdStr) throws JsonParseException {

		/**
		 * parse JsonLd string using JsonLdParser. JsonLd string -> JsonLdParser
		 * -> JsonLd object
		 */
		AnnotationLdParser europeanaParser = new AnnotationLdParser();
		return europeanaParser.parseAnnotation(motivationType, annotationJsonLdStr);
	}

	@Override
	public Provider storeProvider(Provider newProvider) {

		// must have registered id generation type.
		validateProvider(newProvider);

		// store in mongo database
		Provider res = getMongoProviderPersistence().store(newProvider);

		return res;
	}

	/**
	 * This method validates Provider object.
	 * 
	 * @param newProvider
	 */
	private void validateProvider(Provider newProvider) {

		if (newProvider.getIdGeneration() == null)
			throw new ProviderValidationException(ProviderValidationException.ERROR_NOT_NULL_ID_GENERATION);

		if (StringUtils.isEmpty(IdGenerationTypes.isRegisteredAs(newProvider.getIdGeneration())))
			throw new ProviderValidationException(ProviderValidationException.ERROR_NOT_STANDARDIZED_ID_GENERATION);
	}

	@Override
	public List<? extends Provider> getProviderList(String idGeneration) {
		return getMongoProviderPersistence().getProviderList(idGeneration);
	}

	@Override
	public List<? extends Provider> getFilteredProviderList(String idGeneration, String startOn, String limit) {
		return getMongoProviderPersistence().getFilteredProviderList(idGeneration, startOn, limit);
	}

	@Override
	public Provider updateProvider(Provider provider) {
		Provider res = getMongoProviderPersistence().update(provider);
		return res;
	}

	@Override
	public void deleteProvider(String name, String idGeneration) {
		getMongoProviderPersistence().remove(name, idGeneration);
	}

	@Override
	public Concept storeConcept(Concept newConcept) {

		// must have registered id generation type.
		// validateConcept(newConcept);

		// store in mongo database
		Concept res = getMongoConceptPersistence().store(newConcept);

		return res;
	}

	@Override
	public Concept updateConcept(Concept concept) {
		Concept res = getMongoConceptPersistence().update(concept);
		return res;
	}

	@Override
	public void deleteConcept(String url) {
		getMongoConceptPersistence().remove(url);
	}

	@Override
	public Concept getConceptByUrl(String url) {
		return getMongoConceptPersistence().findByUrl(url);
	}

    
    /**
     * Whitelist methods
     * @throws ParamValidationException 
     */
//TODO: move the administration methods to own service class
	@Override
	public Whitelist storeWhitelist(Whitelist newWhitelist) throws ParamValidationException {

		// store in mongo database
		return getMongoWhitelistPersistence().store(newWhitelist);
	}

	@Override
	public Whitelist updateWhitelist(Whitelist whitelist) {
		return getMongoWhitelistPersistence().update(whitelist);
	}

	@Override
	public void deleteWhitelistEntry(String url) {
		getMongoWhitelistPersistence().removeByUrl(url);
	}

	@Override
	public Whitelist getWhitelistByUrl(String url) {
		return getMongoWhitelistPersistence().findByUrl(url);
	}

	public void deleteAllWhitelistEntries() {
		getMongoWhitelistPersistence().removeAll();
	}
	

	public List<? extends Whitelist> loadWhitelistFromResources() throws ParamValidationException{
		List<? extends Whitelist> res = new ArrayList<Whitelist>();
		
		/**
		 * load property with the path to the default whitelist JSON file
		 */
		String whitelistPath = getConfiguration().getDefaultWhitelistResourcePath();
		
		/**
		 * load whitelist from resources in JSON format
		 */
		List<Whitelist> defaultWhitelist = new ArrayList<Whitelist>();
		URL whiteListFile = getClass().getResource(whitelistPath);
		
		defaultWhitelist = JsonUtils.toWhitelistObjectList(
				whiteListFile.getPath().substring(1));
		
		/**
		 *  store whitelist objects in database
		 */
		Iterator<Whitelist> itrDefault = defaultWhitelist.iterator();
		while (itrDefault.hasNext()) {
			storeWhitelist(itrDefault.next());
		}
		
		/**
		 *  retrieve whitelist objects
		 */
		res = getAllWhitelistEntries();

		return res;
	}
	
	public List<? extends Whitelist> getAllWhitelistEntries() {
		
		/**
		 *  retrieve all whitelist objects
		 */
		return getMongoWhitelistPersistence().getAll();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
	 * europeana.annotation.definitions.model.Annotation)
	 */
	@Override
	public Annotation storeAnnotation(Annotation newAnnotation) {
		return storeAnnotation(newAnnotation, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#storeAnnotation(eu.
	 * europeana.annotation.definitions.model.Annotation, boolean)
	 */
	@Override
	public Annotation storeAnnotation(Annotation newAnnotation, boolean indexing) {

		// must have annotaionId with resourceId and provider.
		validateAnnotationId(newAnnotation);

		// store in mongo database
		Annotation res = getMongoPersistence().store(newAnnotation);

		if (indexing) {
			// add solr indexing here
			try {
				getSolrService().store(res);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName())
						.warn("The annotation was stored correctly into the Mongo, but it was not indexed yet. ", e);
				// throw new RuntimeException(e);
			}

			// check if the tag is already indexed
			try {
				//TODO : enable+ refactor when semantic tagging is in place 
//				SolrTag indexedTag = copyIntoSolrTag(res.getBody());
//				getSolrTagService().findOrStore(indexedTag);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName())
						.warn("The annotation was stored correctly into the Mongo, but the Body tag was not indexed yet. ", e);
			}

			// save the time of the last SOLR indexing
			updateLastSolrIndexingTime(res);
		}

		return res;
	}

	/**
	 * This method validates AnnotationId object.
	 * 
	 * @param newAnnotation
	 */
	private void validateAnnotationId(Annotation newAnnotation) {

		if (newAnnotation.getAnnotationId() == null)
			throw new AnnotationValidationException("Annotaion.AnnotationId must not be null!");

		// if (newAnnotation.getAnnotationId().getResourceId() == null)
		// throw new AnnotationValidationException(
		// "Annotaion.AnnotationId.resourceId must not be null!");

		if (newAnnotation.getAnnotationId().getProvider() == null)
			throw new AnnotationValidationException("Annotaion.AnnotationId.provider must not be null!");
	}

	
	

	

	/**
	 * This method converts Body object in SolrTag object.
	 * 
	 * @param tag
	 *            The body object
	 * @return the SolrTag object
	 */
	// private SolrTag copyPersistentTagIntoSolrTag(PersistentTag tag) {
	//
	// SolrTag res = null;
	//
	// SolrTagImpl solrTagImpl = new SolrTagImpl();
	// if (StringUtils.isNotBlank(((PlainTagBody) tag).getTagId())) {
	// solrTagImpl.setId(((PlainTagBody) tag).getTagId());
	// }
	// solrTagImpl.setTagType(tag.getTagType());
	// solrTagImpl.setValue(tag.getValue());
	// solrTagImpl.setLanguage(tag.getLanguage());
	// solrTagImpl.setContentType(tag.getContentType());
	// solrTagImpl.setHttpUri(tag.getHttpUri());
	// solrTagImpl.setMultilingual(tag.getMultilingual());
	//
	// res = solrTagImpl;
	//
	// return res;
	// }

	@Override
	public Annotation updateAnnotation(Annotation annotation) {
		
		Annotation res = getMongoPersistence().update(annotation);
	
		if(getConfiguration().isIndexingEnabled())
			reindexAnnotation(res);
		
		return res;
	}

	protected void reindexAnnotation(Annotation res) {
		try {
			getSolrService().update(res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		// check if the tag is already indexed
		try {
			getSolrTagService().update(res);
		} catch (Exception e) {
			Logger.getLogger(getClass().getName())
					.warn("The annotation was updated correctly in the Mongo, but the Body tag was not updated yet. "
							, e);
		}

		// save the time of the last SOLR indexing
		updateLastSolrIndexingTime(res);
	}

	private void updateLastSolrIndexingTime(Annotation res) {
		try {
			getMongoPersistence().updateIndexingTime(res.getAnnotationId());
		} catch (Exception e) {
			Logger.getLogger(getClass().getName())
					.warn("The time of the last SOLR indexing could not be saved. " , e);
		}
	}

	@Override
	public Annotation updateAnnotationStatus(Annotation annotation) {

		Annotation res = getMongoPersistence().updateStatus(annotation);

//		try {
//			//TODO: status is not in the index now. Enable when available
//			//getSolrService().update(res);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}

		return res;
	}

	@Override
	// public void deleteAnnotation(String resourceId, String provider, Long
	// annotationNr) {
	public void deleteAnnotation(AnnotationId annoId) {
		try {
			//TODO: completely delete from mongo and solr
			//getMongoPersistence().delete(annoId);
			getSolrService().delete(annoId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// getMongoPersistence().remove(resourceId, provider, annotationNr);
		getMongoPersistence().remove(annoId);
	}

	@Override
	// public void indexAnnotation(String resourceId, String provider, Long
	// annotationNr) {
	public void indexAnnotation(AnnotationId annoId) {
		try {
			// Annotation res = getMongoPersistence().find(resourceId, provider,
			// annotationNr);
			Annotation res = getMongoPersistence().find(annoId);
			getSolrService().delete(res.getAnnotationId());
			getSolrService().store(res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Annotation disableAnnotation(AnnotationId annoId) {
		try {
			//disable annotation
			Annotation res = getMongoPersistence().find(annoId);
			return disableAnnotation(res);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Annotation disableAnnotation(Annotation annotation) {
		Annotation res;
		try {
			annotation.setDisabled(true);
			res = getMongoPersistence().update(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if(getConfiguration().isIndexingEnabled())
			removeFromIndex(annotation);
		
		return res;
	}

	protected void removeFromIndex(Annotation annotation) {
		try{
			getSolrService().delete(annotation.getAnnotationId());
		}catch(Exception e){
			logger.error("Cannot remove annotation from solr index: " + annotation.getAnnotationId().toUri(), e);
		}
	}

	public SolrAnnotationService getSolrService() {
		return solrService;
	}

	public void setSolrService(SolrAnnotationService solrService) {
		this.solrService = solrService;
	}

	public SolrTagService getSolrTagService() {
		return solrTagService;
	}

	public void setSolrTagService(SolrTagService solrTagService) {
		this.solrTagService = solrTagService;
	}

	@Override
	public void deleteTag(String tagId) {
		try {
			Annotation res = getMongoPersistence().findByTagId(tagId);
			if (res == null) {
				// PersistentTag persistentTag =
				// getMongoTagPersistence().findByID(tagId);
				// SolrTag indexedTag =
				// copyPersistentTagIntoSolrTag(persistentTag);
				SolrTag solrTag = getSolrTagService().search(tagId).get(0);
				getSolrTagService().delete(solrTag);
				getMongoTagPersistence().remove(tagId);
			} else {
				throw new TagServiceException(
						"Tag with ID: '" + tagId + "' can't be removed since it is referenced by annotation '"
								+ res.getAnnotationId().toString() + "'.");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<? extends Annotation> getAnnotationListByProvider(String resourceId, String provider) {
		return getMongoPersistence().getAnnotationListByProvider(resourceId, provider);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByTarget(String target) {
		return getMongoPersistence().getAnnotationListByTarget(target);
	}

	@Override
	public List<? extends Annotation> getAnnotationListByResourceId(String resourceId) {
		return getMongoPersistence().getAnnotationListByResourceId(resourceId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.annotation.web.service.AnnotationService#existsInDb(eu.
	 * europeana.annotation.definitions.model.AnnotationId)
	 */
	public boolean existsInDb(AnnotationId annoId) {
		boolean res = false;
		try {
			Annotation dbRes = getMongoPersistence().find(annoId);
			if (dbRes != null)
				res = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#existsProviderInDb(
	 * eu.europeana.annotation.definitions.model.Provider)
	 */
	public boolean existsProviderInDb(Provider provider) {
		boolean res = false;
		try {
			Provider dbRes = getMongoProviderPersistence().find(provider.getName(), provider.getIdGeneration());
			if (dbRes != null)
				res = true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return res;
	}

	/**
	 * This method initializes AnnotationId dependent on provider.
	 * 
	 * @param newAnnotation
	 * @return Annotation object initialized with AnnotationId
	 */
	// public AnnotationId initializeAnnotationId(Annotation newAnnotation) {
	// if (StringUtils.isNotEmpty(newAnnotation.getSameAs())
	// && newAnnotation.getSameAs().contains(WebAnnotationFields.HISTORY_PIN)) {
	// MongoAnnotationId annotationId = new MongoAnnotationId();
	// String[] arrValue =
	// newAnnotation.getSameAs().split(WebAnnotationFields.SLASH);
	// if (arrValue.length >=
	// WebAnnotationFields.MIN_HISTORY_PIN_COMPONENT_COUNT) {
	// String resourceId = new
	// AnnotationControllerHelper().extractResourceId(newAnnotation);
	// if (StringUtils.isNotEmpty(resourceId))
	// annotationId.setResourceId(resourceId);
	// annotationId.setProvider(WebAnnotationFields.HISTORY_PIN);
	// //the external id of the annotation is found in the last element of the
	// url
	// String annotationNrStr = arrValue[arrValue.length - 1];
	// if (StringUtils.isNotEmpty(annotationNrStr))
	// annotationId.setAnnotationNr(Integer.parseInt(annotationNrStr));
	// }
	// }
	//
	// // set default provider if sameAs field is empty
	// if (StringUtils.isEmpty(newAnnotation.getSameAs())
	// || StringUtils.isEmpty(res.getAnnotationId().getProvider())) {
	// String provider = WebAnnotationFields.WEB_ANNO;
	// res.getAnnotationId().setProvider(provider);
	// }
	//
	//
	// return newAnnotation;
	// }

	public void logAnnotationStatusUpdate(String user, Annotation annotation) {
		// store in mongo database
		StatusLog statusLog = new BaseStatusLog();
		statusLog.setUser(user);
		statusLog.setStatus(annotation.getStatus());
		long currentTimestamp = System.currentTimeMillis();
		statusLog.setDate(currentTimestamp);
		statusLog.setAnnotationId(annotation.getAnnotationId());
		getMongoStatusLogPersistence().store(statusLog);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.annotation.web.service.AnnotationService#checkVisibility(eu.
	 * europeana.annotation.definitions.model.Annotation, java.lang.String)
	 */
	public void checkVisibility(Annotation annotation, String user) throws AnnotationStateException {
//		Annotation res = null;
//		res = getMongoPersistence().find(annotation.getAnnotationId().getProvider(),
//				annotation.getAnnotationId().getIdentifier());
		if (annotation.isDisabled())
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE, AnnotationStates.DISABLED);

//		if (annotation.
//				StringUtils.isNotEmpty(user) && !user.equals("null") && !res.getAnnotatedBy().getName().equals(user))
//			throw new AnnotationStateException("Given user (" + user + ") does not match to the 'annotatedBy' user ("
//					+ res.getAnnotatedBy().getName() + ").");
		//TODO update when the authorization concept is specified
		if(annotation.isPrivate() && !annotation.getAnnotatedBy().getOpenId().equals(user))
			throw new AnnotationStateException(AnnotationStateException.MESSAGE_NOT_ACCESSIBLE, AnnotationStates.PRIVATE);

	}

	@Override
	public void validateAnnotationId(AnnotationId annoId) throws ParamValidationException {
		switch (annoId.getProvider()) {
		case WebAnnotationFields.PROVIDER_HISTORY_PIN:
			if (annoId.getIdentifier() == null || Long.parseLong(annoId.getIdentifier()) < 1)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NULL, 
						WebAnnotationFields.PROVIDER +"/"+ WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_PUNDIT:
			if (annoId.getIdentifier() == null || Long.parseLong(annoId.getIdentifier()) < 1)
				throw new ParamValidationException(ParamValidationException.MESSAGE_IDENTIFIER_NULL, 
						WebAnnotationFields.PROVIDER +"/"+ WebAnnotationFields.IDENTIFIER, annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_BASE:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(
						ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,  
						WebAnnotationFields.PROVIDER +"/"+ WebAnnotationFields.IDENTIFIER,  annoId.toUri());
			break;
		case WebAnnotationFields.PROVIDER_WEBANNO:
			if (annoId.getIdentifier() != null)
				throw new ParamValidationException(
						ParamValidationException.MESSAGE_IDENTIFIER_NOT_NULL,  
						WebAnnotationFields.PROVIDER +"/"+ WebAnnotationFields.IDENTIFIER,  annoId.toUri());
			break;
		default:
			throw new ParamValidationException(WebAnnotationFields.INVALID_PROVIDER, WebAnnotationFields.PROVIDER, annoId.getProvider());
		}

	}

	@Override
	public Annotation getAnnotationById(AnnotationId annoId) {
		return getMongoPersistence().find(annoId);
		//return getAnnotationById(annoId.getBaseUrl(), annoId.getProvider(), annoId.getIdentifier());
	}

	protected boolean validateResource(String url) throws ParamValidationException {
		
		String domainName;
		try {
			domainName = getMongoWhitelistPersistence().getDomainName(url);
			Set<String> domains = getMongoWhitelistPersistence().getWhitelistDomains();
			if (!domains.contains(domainName))
				throw new ParamValidationException(ParamValidationException.MESSAGE_INVALID_PARAMETER_VALUE, "target.value", url);
		} catch (URISyntaxException e) {
			throw new ParamValidationException(ParamValidationException.MESSAGE_URL_NOT_VALID, "target.value", url);
		}
		
		return true;
	}

	@Override
	public void validateWebAnnotation(Annotation webAnnotation) throws ParamValidationException {
		//TODO: change to switch
		if(MotivationTypes.LINKING.equals(webAnnotation.getMotivationType())){
			//validate target URLs against whitelist 
			if(webAnnotation.getTarget().getValue() != null)
				validateResource(webAnnotation.getTarget().getValue());
				
			if(webAnnotation.getTarget().getValues()!= null)
			for(String url : webAnnotation.getTarget().getValues()){
				validateResource(url);
			}
		}
			
	}
}
