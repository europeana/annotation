package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.solr.model.internal.SolrTag;


public class AnnotationSearchApiTest {

	public static String VALUE     = "Vlad";
	public static String START_ON  = "0";
	public static String LIMIT     = "10";
	public static String FIELD     = "multilingual";
	public static String LANGUAGE  = "en";
	
	
	@Test
	public void searchAnnotation() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends Annotation> annotationList = 
				annotationSearchApi.searchAnnotations(VALUE);
		
		assertNotNull(annotationList);
		assertTrue(annotationList.size() > 0);
	}

	@Test
	public void searchAnnotationByLimit() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends Annotation> annotationList = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, null, null);
		
		assertNotNull(annotationList);
		assertTrue(annotationList.size() > 0);
	}

	@Test
	public void searchAnnotationByLanguage() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends Annotation> annotationList = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, FIELD, LANGUAGE);
		
		assertNotNull(annotationList);
		assertTrue(annotationList.size() > 0);
	}

	//@Test
	public void searchTag() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends SolrTag> tagList = 
				annotationSearchApi.searchTags(VALUE);
		
		assertNotNull(tagList);
		assertTrue(tagList.size() > 0);
	}

	//@Test
	public void searchTagByLimit() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends SolrTag> tagList = 
				annotationSearchApi.searchTags(VALUE, START_ON, LIMIT, null, null);
		
		assertNotNull(tagList);
		assertTrue(tagList.size() > 0);
	}

	//@Test
	public void searchTagByLanguage() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends SolrTag> tagList = 
				annotationSearchApi.searchTags(VALUE, START_ON, LIMIT, FIELD, LANGUAGE);
		
		assertNotNull(tagList);
		assertTrue(tagList.size() > 0);
	}
	
}
