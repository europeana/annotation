package eu.europeana.annotation.web.service;

import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public interface AnnotationService {

	public String getComponentName();
	
	public List<? extends Annotation> getAnnotationList(String resourceId);
	
	public Annotation createAnnotation(Annotation newAnnotation);
	
	public Annotation updateAnnotation(Annotation newAnnotation);
	
	public void deleteAnnotation(String resourceId,
			int annotationNr);
	
	public Annotation getAnnotationById(String europeanaId, int annotationNr);
	
	/**
	 * Search for annotations by the given text query.
	 * @param europeanaId
	 * @param query
	 * @return
	 */
	public List<? extends Annotation> getAnnotationByQuery(String europeanaId, String query);
	
	/**
	 * This method converts RDF string to JSONLD format
	 * @param format
	 * @param rdf
	 * @return JSONLD string
	 */
//	public String convertRdfToJsonld(String format, String rdf);

	/**
	 * This method converts serialized Annotation string to JSONLD format
	 * @param format
	 * @param rdf
	 * @return JSONLD string
	 */
//	public String convertAnnotationToJsonld(String format, String rdf);

	/**
	 * Convert an Annotation bean to a RDF String
	 * 
	 * @param bean
	 *            The Annotation to convert
	 * @return The resulting string in RDF-XML
	 */
//	public String toRDF(Annotation bean);
}
