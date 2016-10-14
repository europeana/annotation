package eu.europeana.annotation.client.integration.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.impl.TagResource;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;


/**
 * TODO: rewrite
 * 
 * @author Sergiu Gordea @ait
 * @author Sven Schlarb @ait
 */
public class AnnotationSearchApiTest {
	
	// http://localhost:8080/annotation/search?wskey=apidemo&query=*%3A*

	public static String VALUE     = "Vlad";
	public static String VALUE_ALL = "*:*";
	public static String START_ON  = "0";
	public static String LIMIT     = "10";
	public static String FIELD     = "multilingual";
	public static String LANGUAGE  = "ro";	
	
	
	@Test
	public void searchAnnotation() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE_ALL);
		
		assertNotNull("AnnotationPage must not be null", annotationPage);
		assertEquals("First page must be 0", annotationPage.getCurrentPage(), 0);
	}

//	@Test
	public void searchAnnotationByLimit() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, null, null);
		
		assertNotNull(annotationPage);
		//assertTrue(annotationList.size() > 0);
	}

//	@Test
	public void searchAnnotationByLanguage() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		AnnotationPage annotationPage = 
				annotationSearchApi.searchAnnotations(VALUE, START_ON, LIMIT, FIELD, LANGUAGE);
		
		assertNotNull(annotationPage);
		//assertTrue(annotationList.size() > 0);
	}
	
	
}
