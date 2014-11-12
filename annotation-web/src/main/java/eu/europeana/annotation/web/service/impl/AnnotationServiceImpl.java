package eu.europeana.annotation.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;
import eu.europeana.corelib.logging.Log;
import eu.europeana.corelib.logging.Logger;

public class AnnotationServiceImpl implements AnnotationService {

	@Autowired
	AnnotationConfiguration configuration;
	
	@Autowired
	PersistentAnnotationService mongoPersistance;
	
	@Autowired
	SolrAnnotationService solrService;
	
	
	@Log
	private Logger log;
	
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

//	protected HttpSolrServer getSolrServer() {
//		return solrServer;
//	}
//
//	public void setSolrServer(HttpSolrServer solrServer) {
//		this.solrServer = solrServer;
//	}

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
	public Annotation createAnnotation(Annotation newAnnotation) {
		 // store in mongo database
		Annotation res =  getMongoPersistance().store(newAnnotation);
		// add solr indexing here
//        try {
//    		SolrTagImpl solrBeanImpl = new SolrTagImpl();
//    		solrBeanImpl.setCreator(newAnnotation.getMotivatedBy());
//    		solrBeanImpl.setLanguage(newAnnotation.getType());
//    		solrBeanImpl.setLabel(newAnnotation.getType());
//
//        	SolrInputDocument solrDocument = new SolrInputDocument();
//    		solrDocument = new SolrBeanFieldInput().createSolrBeanFields(solrBeanImpl,
//    	    		solrDocument = new SolrBeanFieldInput().createSolrBeanFields(newAnnotation.,
//    				solrDocument);
//        	SolrBeanCreator.create(solrDocument, newAnnotation);
//            getSolrServer().add(solrDocument);
//        } catch (SolrServerException ex) {
//            Logger.getLogger(SolrDocumentHandler.class.getName());//.log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(SolrDocumentHandler.class.getName());//.log(Level.SEVERE, null, ex);
//        }
       try{
    	   SolrAnnotation indexedAnnotation = copyIntoSolrAnnotation(res);
    	   getSolrService().store(indexedAnnotation);
       }catch(Exception e){
    	   
    	   //TODO: implement appropriate exception handling ... the annotation was stored correctly into the Mongo, but it was not indexed yet.
    	   throw new RuntimeException(e);
       }
		
       return res;
	}

	private SolrAnnotation copyIntoSolrAnnotation(Annotation annotation) {
		
		SolrAnnotation res = null;
		
  		SolrAnnotationImpl solrAnnotationImpl = new SolrAnnotationImpl();
  		solrAnnotationImpl.setAnnotatedBy(annotation.getAnnotatedBy());
  		solrAnnotationImpl.setHasBody(annotation.getHasBody());
  		solrAnnotationImpl.setAnnotatedAt(annotation.getAnnotatedAt());
  		solrAnnotationImpl.setAnnotatedByString(annotation.getAnnotatedBy().getName());
  		solrAnnotationImpl.setHasTarget(annotation.getHasTarget());
  		solrAnnotationImpl.setAnnotationId(annotation.getAnnotationId());
  		solrAnnotationImpl.setLabel(annotation.getHasBody().getValue());
  		solrAnnotationImpl.setLanguage(annotation.getHasBody().getLanguage());
  		solrAnnotationImpl.setMotivatedBy(annotation.getMotivatedBy());
  		solrAnnotationImpl.setSerializedAt(annotation.getSerializedAt());
  		solrAnnotationImpl.setSerializedBy(annotation.getSerializedBy());
  		solrAnnotationImpl.setStyledBy(annotation.getStyledBy());

        res = solrAnnotationImpl;

        return res;
	}

	@Override
	public Annotation updateAnnotation(Annotation annotation) {
		Annotation res = annotation;
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

}
