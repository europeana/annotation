package eu.europeana.annotation.web.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.body.Body;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.solr.exceptions.TagServiceException;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationConst;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.model.internal.SolrTag;
import eu.europeana.annotation.solr.model.internal.SolrTagImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.solr.service.SolrTagService;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;

public class AnnotationServiceImpl implements AnnotationService {

	@Autowired
	AnnotationConfiguration configuration;
	
	@Autowired
	PersistentAnnotationService mongoPersistance;
	
	@Autowired
	SolrAnnotationService solrService;
	
	@Autowired
	SolrTagService solrTagService;
	
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

	protected PersistentAnnotationService getMongoPersistance() {
		return mongoPersistance;
	}

	public void setMongoPersistance(PersistentAnnotationService mongoPersistance) {
		this.mongoPersistance = mongoPersistance;
	}

	@Override
	public List<? extends Annotation> getAnnotationList(String resourceId) {
		
		return getMongoPersistance().getAnnotationList(resourceId);
	}
	
	@Override
	public Annotation getAnnotationById(String resourceId,
			int annotationNr) {
		return getMongoPersistance().find(resourceId, annotationNr);
		
	}

	@Override
	public List<? extends Annotation> searchAnnotations(String query) throws AnnotationServiceException {
		//try {
			return getSolrService().search(query);
//			return getSolrService().searchByTerm(query);
//		} catch (AnnotationServiceException e) {
//			Logger.getLogger(getClass().getName()).warn(e);
//			return null;
//		}		
	}

	@Override
	public Map<String, Integer> searchAnnotations(String [] qf, List<String> queries) throws AnnotationServiceException {
		//try {
			return getSolrService().queryFacetSearch(SolrAnnotationConst.ALL_SOLR_ENTRIES, qf, queries);
//		} catch (AnnotationServiceException e) {
//			Logger.getLogger(getClass().getName()).warn(e);
//			return null;
//		}		
	}

	@Override
	public List<? extends SolrTag> searchTags(
			String query) throws TagServiceException {
			return getSolrTagService().search(query);
//			return getSolrTagService().searchByTerm(query);
//		} catch (TagServiceException e) {
//			Logger.getLogger(getClass().getName()).warn(e);
//			return null;
//		}		
	}

	@Override
	public Annotation createAnnotation(Annotation newAnnotation) {
		
		// store in mongo database
		Annotation res =  getMongoPersistance().store(newAnnotation);

		// add solr indexing here
        try {
       	    SolrAnnotation indexedAnnotation = copyIntoSolrAnnotation(res);
       	    getSolrService().store(indexedAnnotation);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).warn(
        		   "The annotation was stored correctly into the Mongo, but it was not indexed yet. " + e);
//    	    throw new RuntimeException(e);
        }
        
        // check if the tag is already indexed 
        try {
       	    SolrTag indexedTag = copyIntoSolrTag(res.getBody());
       	    getSolrTagService().findOrStore(indexedTag);
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).warn(
        		   "The annotation was stored correctly into the Mongo, but the Body tag was not indexed yet. " + e);
        }
		
        // save the time of the last SOLR indexing
        try {
    	    getMongoPersistance().updateIndexingTime(res.getAnnotationId()); 	    
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).warn(
         		   "The time of the last SOLR indexing could not be saved. " + e);
        }
		
       return res;
	}

	private SolrAnnotation copyIntoSolrAnnotation(Annotation annotation) {
		
		SolrAnnotation res = null;
		
  		SolrAnnotationImpl solrAnnotationImpl = new SolrAnnotationImpl();
//  		solrAnnotationImpl.setType(annotation.getType()); 
  		solrAnnotationImpl.setAnnotationType(annotation.getType()); 
  		solrAnnotationImpl.setAnnotatedBy(annotation.getAnnotatedBy());
  		solrAnnotationImpl.setBody(annotation.getBody());
  		solrAnnotationImpl.setAnnotatedAt(annotation.getAnnotatedAt());
  		solrAnnotationImpl.setAnnotatedByString(annotation.getAnnotatedBy().getName());
  		solrAnnotationImpl.setTarget(annotation.getTarget());
  		solrAnnotationImpl.setAnnotationId(annotation.getAnnotationId());
  		solrAnnotationImpl.setLabel(annotation.getBody().getValue());
  		solrAnnotationImpl.setLanguage(annotation.getBody().getLanguage());
  		solrAnnotationImpl.setMotivatedBy(annotation.getMotivatedBy());
  		solrAnnotationImpl.setSerializedAt(annotation.getSerializedAt());
  		solrAnnotationImpl.setSerializedBy(annotation.getSerializedBy());
  		solrAnnotationImpl.setStyledBy(annotation.getStyledBy());
  		if (StringUtils.isNotBlank(annotation.getAnnotationId().toString())) {
  			solrAnnotationImpl.setAnnotationIdString(annotation.getAnnotationId().toString());
  		}
		if (StringUtils.isNotBlank(solrAnnotationImpl.getTagId())) {
			solrAnnotationImpl.setTagId(solrAnnotationImpl.getTagId());
		}

        res = solrAnnotationImpl;

        return res;
	}

	/**
	 * This method converts Body object in SolrTag object.
	 * @param tag The body object
	 * @return the SolrTag object
	 */
	private SolrTag copyIntoSolrTag(Body tag) {
		
		SolrTag res = null;
		
  		SolrTagImpl solrTagImpl = new SolrTagImpl();
		if (StringUtils.isNotBlank(((PlainTagBody) tag).getTagId())) {
			solrTagImpl.setId(((PlainTagBody) tag).getTagId());
		}
  		solrTagImpl.setTagType(tag.getBodyType());
  		solrTagImpl.setValue(tag.getValue());
  		solrTagImpl.setLanguage(tag.getLanguage());
  		solrTagImpl.setContentType(tag.getContentType());
  		solrTagImpl.setHttpUri(tag.getHttpUri());

        res = solrTagImpl;

        return res;
	}

	@Override
	public Annotation updateAnnotation(Annotation annotation) {
		
		Annotation res = getMongoPersistance().update(annotation);

		try {
    	    SolrAnnotation indexedAnnotation = copyIntoSolrAnnotation(annotation);
    	    getSolrService().update(indexedAnnotation);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
		
		return res;
	}

	@Override
	public void deleteAnnotation(String resourceId,
			int annotationNr) {
        try {
    		Annotation res =  getMongoPersistance().findByID(String.valueOf(annotationNr));
    	    SolrAnnotation indexedAnnotation = copyIntoSolrAnnotation(res);
    	    getSolrService().delete(indexedAnnotation);
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
		getMongoPersistance().remove(resourceId, annotationNr);
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


}
