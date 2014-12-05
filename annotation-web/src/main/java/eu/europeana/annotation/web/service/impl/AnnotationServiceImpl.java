package eu.europeana.annotation.web.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.solr.model.internal.SolrAnnotation;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;
import eu.europeana.annotation.solr.service.SolrAnnotationService;
import eu.europeana.annotation.web.service.AnnotationConfiguration;
import eu.europeana.annotation.web.service.AnnotationService;

public class AnnotationServiceImpl implements AnnotationService {

	@Autowired
	AnnotationConfiguration configuration;
	
	@Autowired
	PersistentAnnotationService mongoPersistance;
	
	@Autowired
	SolrAnnotationService solrService;
	
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

        res = solrAnnotationImpl;

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
	
	
	/**
	 * JSONLD part
	 */
	
	
//	@RequestMapping(value = { "/{collectionId}/{recordId}.jsonld", "/{collectionId}/{recordId}.json-ld" }, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ModelAndView recordJSONLD(
//			@PathVariable String collectionId, 
//			@PathVariable String recordId,
//			@RequestParam(value = "wskey", required = true) String wskey,
//			@RequestParam(value = "format", required = false, defaultValue="compacted") String format,
//			@RequestParam(value = "callback", required = false) String callback, 
//			HttpServletRequest request, HttpServletResponse response) {
//
//		response.setCharacterEncoding("UTF-8");
//
//		String europeanaObjectId = "/" + collectionId + "/" + recordId;
//
//		String jsonld = null;
//
//		Annotation bean = null;
//		try {
//			bean = getAnnotationById(collectionId, Integer.valueOf(recordId));
//			if (bean == null) {
//				bean = solrService.searchById(europeanaObjectId).get(0);
//			}
//		} catch (AnnotationServiceException e) {
//			Logger.getLogger(getClass().getName()).error(ExceptionUtils.getFullStackTrace(e));
//		}
//
//		if (bean != null) {
//			String rdf = toRDF(bean);
//			jsonld = convertRdfToJsonld(format, rdf);
//		} else {
//			Logger.getLogger(getClass().getName()).info(HttpServletResponse.SC_NOT_FOUND);
//		}
//
//		return JsonUtils.toJson(jsonld, callback);
//	}


	/* (non-Javadoc)
	 * @see eu.europeana.annotation.web.service.AnnotationService#convertRdfToJsonld(java.lang.String, java.lang.String)
	 */
//	public String convertRdfToJsonld(String format, String rdf) {
//		String jsonld = "";
//		try {
//			Model modelResult = ModelFactory.createDefaultModel().read(IOUtils.toInputStream(rdf), "", "RDF/XML");
//			JenaRDFParser parser = new JenaRDFParser();
//			Object raw = JSONLD.fromRDF(modelResult, parser);
//			if (StringUtils.equalsIgnoreCase(format, "compacted")) {
//				raw = JSONLD.compact(raw, getJsonContext(), new Options());
//			} else if (StringUtils.equalsIgnoreCase(format, "flattened")) {
//				raw = JSONLD.flatten(raw);
//			} else if (StringUtils.equalsIgnoreCase(format, "normalized")) {
//				raw = JSONLD.normalize(raw);
//			}
//			jsonld = JSONUtils.toString(raw);
//		} catch (JSONLDProcessingError e) {
//			Logger.getLogger(getClass().getName()).error(e.getMessage(), e);
//			Logger.getLogger(getClass().getName()).info(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}
//		return jsonld;
//	}

//	@RequestMapping(value = "/{collectionId}/{recordId}.rdf", produces = "application/rdf+xml")
//	public ModelAndView recordRdf(@PathVariable String collectionId, @PathVariable String recordId,
//			@RequestParam(value = "wskey", required = true) String wskey, HttpServletResponse response) {
//		
//		response.setCharacterEncoding("UTF-8");
//
//		Map<String, Object> model = new HashMap<String, Object>();
//		model.put("error", "");
//
//		String europeanaObjectId = "/" + collectionId + "/" + recordId;
//
//		Annotation bean = null;
//		try {
//			bean = getAnnotationById(collectionId, Integer.valueOf(recordId));
//			if (bean == null) {
//				bean = solrService.searchById(europeanaObjectId).get(0);
//			}
//		} catch (AnnotationServiceException e) {
//			Logger.getLogger(getClass().getName()).error(ExceptionUtils.getFullStackTrace(e));
//		}
//
//		if (bean != null) {
//			model.put("record", toRDF(bean));
//		} else {
//			response.setStatus(404);
//			model.put("error", "Non-existing record identifier");
//		}
//
//		return new ModelAndView("rdf", model);
//	}

//	private Object getJsonContext() {
//		InputStream in = this.getClass().getResourceAsStream("/jsonld/context.jsonld");
//		try {
//			return JSONUtils.fromInputStream(in);
//		} catch (IOException e) {
//			Logger.getLogger(getClass().getName()).error(e.getMessage(), e);
//			e.printStackTrace();
//		} finally {
//			IOUtils.closeQuietly(in);
//		}
//		return null;
//	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.annotation.web.service.AnnotationService#toRDF(eu.europeana.annotation.definitions.model.Annotation, boolean)
	 */
//	public synchronized String toRDF(Annotation bean) {

//		RDF rdf = new RDF();
//		String type = bean.getType();
		
//		private String annotationId_string;
//		private String resourceId;
//		private String label;
//		private String tag_type;
//		private String http_uri;
//		private String language;
//		
//		public AnnotationId getAnnotationId();
//
//		public abstract String getType();
//
//		public abstract void setSerializedBy(Agent serializedBy);
//
//		public abstract Agent getSerializedBy();
//
//		public abstract void setStyledBy(Style styledBy);
//
//		public abstract Style getStyledBy();
//
//		public abstract void setMotivatedBy(String motivatedBy);
//
//		public abstract String getMotivatedBy();
//
//		public abstract MotivationTypes getMotivationType();
//
//		public abstract void setHasTarget(Target hasTarget);
//
//		public abstract Target getHasTarget();
//
//		public abstract void setHasBody(Body hasBody);
//
//		public abstract Body getHasBody();
//
//		public abstract void setAnnotatedBy(Agent annotatedBy);
//
//		public abstract Date getAnnotatedAt();
//		
//		public abstract Date getSerializedAt();


//		appendCHO(rdf, fullBean.getProvidedCHOs());
//		appendAggregation(rdf, fullBean.getAggregations());
//		appendProxy(rdf, fullBean.getProxies(), type);
//		appendEuropeanaAggregation(rdf, fullBean);
//		appendAgents(rdf, bean.getAnnotatedBy()); // Agent
//		appendConcepts(rdf, fullBean.getConcepts());
//		appendPlaces(rdf, fullBean.getPlaces());
//		appendTimespans(rdf, fullBean.getTimespans());
//		appendLicenses(rdf, fullBean.getLicenses());
//		IMarshallingContext marshallingContext;
//		try {
//			IBindingFactory bfact = null;
//			if (bfact == null) {
//				bfact = BindingDirectory.getFactory(RDF.class);
//			}
//			marshallingContext = bfact.createMarshallingContext();
//			ByteArrayOutputStream out = new ByteArrayOutputStream();
//			marshallingContext.setOutput(out, null);
//			marshallingContext.marshalDocument(rdf, "UTF-8", true);
//			return out.toString("UTF-8");
//		} catch (JiBXException e) {
//			Logger.getLogger(getClass().getName()).warn(e.getClass().getSimpleName() + "  " + e.getMessage());
//		} catch (UnsupportedEncodingException e) {
//			Logger.getLogger(getClass().getName()).warn(e.getClass().getSimpleName() + "  " + e.getMessage());
//		}
//		return null;
//	}

	

}
