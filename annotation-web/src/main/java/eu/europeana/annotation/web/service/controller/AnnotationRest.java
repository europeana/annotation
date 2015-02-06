package eu.europeana.annotation.web.service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.stanbol.commons.jsonld.JsonLd;
import org.apache.stanbol.commons.jsonld.JsonLdParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import eu.europeana.annotation.definitions.model.Annotation;
//import eu.europeana.annotation.definitions.model.factory.AbstractAnnotationFactory;
import eu.europeana.annotation.definitions.model.impl.AbstractAnnotation;
import eu.europeana.annotation.definitions.model.utils.TypeUtils;
import eu.europeana.annotation.jsonld.AnnotationLd;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.web.model.AnnotationOperationResponse;
import eu.europeana.annotation.web.model.AnnotationSearchResults;
import eu.europeana.annotation.web.model.TagOperationResponse;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.api2.utils.JsonUtils;

@Controller
public class AnnotationRest {

	@Autowired
	AnnotationConfiguration configuration;

	@Autowired
	private AnnotationService annotationService;

	AnnotationControllerHelper controllerHelper = new AnnotationControllerHelper();

	TypeUtils typeUtils = new TypeUtils();

	public TypeUtils getTypeUtils() {
		return typeUtils;
	}

	public AnnotationConfiguration getConfiguration() {
		return configuration;
	}

	protected AnnotationService getAnnotationService() {
		return annotationService;
	}

	public void setAnnotationService(AnnotationService annotationService) {
		this.annotationService = annotationService;
	}

	private String toResourceId(String collection, String object) {
		return "/"+ collection +"/" + object;
	}
	
	public void setConfiguration(AnnotationConfiguration configuration) {
		this.configuration = configuration;
	}

	public AnnotationControllerHelper getControllerHelper() {
		return controllerHelper;
	}
	
	@RequestMapping(value = "/annotations/component", method = RequestMethod.GET, produces = "text/*")
	@ResponseBody
	public String getComponentName() {
		return getConfiguration().getComponentName();
	}

	@RequestMapping(value = "/annotations/{collection}/{object}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView getAnnotationList(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {
		
		String resourceId = toResourceId(collection, object);
		List<? extends Annotation> annotations = getAnnotationService()
				.getAnnotationList(resourceId);
		
		AnnotationSearchResults<AbstractAnnotation> response = new AnnotationSearchResults<AbstractAnnotation>(
				apiKey, "/annotations/collection/object");
		response.items = new ArrayList<AbstractAnnotation>(annotations.size());

		AbstractAnnotation webAnnotation;
		for (Annotation annotation : annotations) {
			webAnnotation = getControllerHelper().copyIntoWebAnnotation(
					annotation, apiKey);
			response.items.add(webAnnotation);
		}
		response.itemsCount = response.items.size();
		response.totalResults = annotations.size();

		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{collection}/{object}/{annotationNr}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView getAnnotation(@PathVariable String collection,
			@PathVariable String object, @PathVariable Integer annotationNr,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile) {

		String resourceId = toResourceId(collection, object);
		
		Annotation annotation = getAnnotationService().getAnnotationById(
				resourceId, annotationNr);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/collection/object/annotationNr");

		if (annotation != null) {

			response.success = true;
			response.requestNumber = 0L;

			response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
					annotation, apiKey));
		}else{
			response.success = false;
			response.action = "get: /annotations/"+collection+"/"
					+object+"/"+annotationNr;
			
			response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
		}
		
		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/{collection}/{object}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotation(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "annotation", required = true) String jsonAnno) {

		Annotation webAnnotation = JsonUtils.toAnnotationObject(jsonAnno);
		Annotation persistantAnnotation = getControllerHelper()
				.copyIntoPersistantAnnotation(webAnnotation, apiKey);

		Annotation storedAnnotation = getAnnotationService().createAnnotation(
				persistantAnnotation);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "create:/annotations/collection/object/");
		response.success = true;
		response.requestNumber = 0L;

		response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
				storedAnnotation, apiKey));

		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/create/{collection}/{object}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView createAnnotationObject(@PathVariable String collection,
			@PathVariable String object,
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "annotation", required = true) String jsonAnno) {

		/**
		 * Set multilingual values.
		 */
		jsonAnno = JsonUtils.convertMultilingualFromJsonLdToSolrType(jsonAnno);
		
        /**
         * parse JsonLd string using JsonLdParser.
         * JsonLd string -> JsonLdParser -> JsonLd object
         */
        AnnotationLd parsedAnnotationLd = null;
        JsonLd parsedJsonLd = null;
        try {
        	parsedJsonLd = JsonLdParser.parseExt(jsonAnno);
        	
        	/**
        	 * convert JsonLd to AnnotationLd.
        	 * JsonLd object -> AnnotationLd object
        	 */
        	parsedAnnotationLd = new AnnotationLd(parsedJsonLd);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        /**
         * AnnotationLd object -> Annotation object.
         */
        Annotation webAnnotation = parsedAnnotationLd.getAnnotation();
		Annotation persistentAnnotation = getControllerHelper()
				.copyIntoPersistantAnnotation(webAnnotation);

		Annotation storedAnnotation = getAnnotationService().createAnnotation(
				persistentAnnotation);

		/**
		 * Convert PersistentAnnotation in Annotation.
		 */
		Annotation resAnnotation = controllerHelper
				.copyIntoWebAnnotation(storedAnnotation);

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "create:/annotations/create/collection/object/");
		response.success = true;
		response.requestNumber = 0L;

		response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
				resAnnotation, apiKey));

		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/annotations/search/{collection}/{object}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView searchAnnotationByField(@PathVariable String collection,
			@PathVariable String object, 
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "field", required = true) String field,
			@RequestParam(value = "language", required = true) String language,
			@RequestParam(value = "facet", required = false) String facet) {

		String resourceId = toResourceId(collection, object);
		query = getTypeUtils().removeTabs(query);
//		field = getTypeUtils().removeTabs(field);
//		language = getTypeUtils().removeTabs(language);
//		facet = getTypeUtils().removeTabs(facet);
		if (StringUtils.isNotEmpty(field)) {
			if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
				String prefix = "";
				if (field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
					prefix = SolrAnnotationConst.DEFAULT_LANGUAGE + SolrAnnotationConst.UNDERSCORE;
					if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
					}
				}
				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
			}
		} else {
			query = SolrAnnotationConst.ALL_SOLR_ENTRIES;
		}
		
		boolean withFacet = false;
		if (StringUtils.isNotEmpty(facet) && !facet.equals(SolrAnnotationConst.ALL)) {
			withFacet = true;
			if (SolrAnnotationConst.SolrAnnotationFields.contains(field)) {
				String prefix = "";
				if (field.equals(SolrAnnotationConst.SolrAnnotationFields.MULTILINGUAL.getSolrAnnotationField())) {
					prefix = SolrAnnotationConst.DEFAULT_LANGUAGE + SolrAnnotationConst.UNDERSCORE;
					if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
					}
				}
				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
			}
		}

		AnnotationOperationResponse response = new AnnotationOperationResponse(
				apiKey, "/annotations/search/collection/object/");

		if (!withFacet) {
			List<? extends Annotation> annotationList = getAnnotationService().getAnnotationByQuery(
					resourceId, query);

			if (annotationList != null && annotationList.size() > 0) {

				response.success = true;
				response.requestNumber = 0L;

				response.setAnnotation(getControllerHelper().copyIntoWebAnnotation(
						annotationList.get(0), apiKey));
			} else {
				response.success = false;
				response.action = "get: /annotations/"+collection+"/"
						+object+"/"+ query;
				
				response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
			}
		} else {
			List<String> queries = new ArrayList<String>();
			queries.add(SolrAnnotationConst.SolrAnnotationFields.LABEL.getSolrAnnotationField() 
					+ SolrAnnotationConst.DELIMETER
					+ SolrAnnotationConst.STAR);
			List<String> qfList = new ArrayList<String>();
			qfList.add(facet);
			String[] qf = qfList.toArray(new String[qfList.size()]);
			Map<String,Integer> annotationMap = getAnnotationService().getAnnotationByFacetedQuery(qf, queries);
			if (annotationMap != null && annotationMap.size() > 0) {

				response.success = true;
				response.requestNumber = 0L;
				response.action = JsonUtils.mapToStringExt(annotationMap);
			} else {
				response.success = false;
				response.action = "get: /annotations/"+collection+"/"
						+object+"/"+ query;
				
				response.error = AnnotationOperationResponse.ERROR_NO_OBJECT_FOUND;
			}
		}
				
		return JsonUtils.toJson(response, null);
	}

	@RequestMapping(value = "/tags/search/{collection}/{object}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ModelAndView searchTagByField(@PathVariable String collection,
			@PathVariable String object, 
			@RequestParam(value = "apiKey", required = false) String apiKey,
			@RequestParam(value = "profile", required = false) String profile,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "field", required = true) String field,
			@RequestParam(value = "language", required = true) String language) {

		String resourceId = toResourceId(collection, object);
		query = getTypeUtils().removeTabs(query);
//		field = getTypeUtils().removeTabs(field);
//		language = getTypeUtils().removeTabs(language);
		if (StringUtils.isNotEmpty(field)) {
			if (SolrAnnotationConst.SolrTagFields.contains(field)) {
				String prefix = "";
				if (field.equals(SolrAnnotationConst.SolrTagFields.MULTILINGUAL.getSolrTagField())) {
//					prefix = ".*" + SolrAnnotationConst.UNDERSCORE;
					prefix = SolrAnnotationConst.DEFAULT_LANGUAGE + SolrAnnotationConst.UNDERSCORE;
					if (SolrAnnotationConst.SolrAnnotationLanguages.contains(language)) {
						prefix = language.toUpperCase() + SolrAnnotationConst.UNDERSCORE;
					}
				}
				query = prefix + field + SolrAnnotationConst.DELIMETER + query;
			}
		} else {
			query = SolrAnnotationConst.ALL_SOLR_ENTRIES;
		}

		List<? extends SolrTag> tagList = getAnnotationService().getTagByQuery(
				resourceId, query);

		TagOperationResponse response = new TagOperationResponse(
				apiKey, "/tag/search/collection/object/");

		if (tagList != null && tagList.size() > 0) {

			response.success = true;
			response.requestNumber = 0L;

			response.setTag(tagList.get(0));
		} else {
			response.success = false;
			response.action = "get: /annotations/"+collection+"/"
					+object+"/"+ query;
			
			response.error = TagOperationResponse.ERROR_NO_OBJECT_FOUND;
		}
		
		return JsonUtils.toJson(response, null);
	}

}
