package eu.europeana.annotation.client.integration.search;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.resource.TagResource;


/**
 * TODO: enable when the search functionality will be enabled
 * 
 * @author Sergiu Gordea @ait
 */
@Ignore
public class AnnotationSearchApiTest {

	public static String VALUE     = "Vlad";
	public static String START_ON  = "0";
	public static String LIMIT     = "10";
	public static String FIELD     = "multilingual";
	public static String LANGUAGE  = "ro";
	
	
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

	@Test
	public void searchTag() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends TagResource> tagList = 
				annotationSearchApi.searchTags(VALUE);
		
		assertNotNull(tagList);
		assertTrue(tagList.size() > 0);
		assertTrue(tagList.get(0).getLabel().contains(VALUE));
	}

	@Test
	public void searchTagByLimit() throws Exception {

		AnnotationSearchApiImpl annotationSearchApi = new AnnotationSearchApiImpl();
		
		List<? extends TagResource> tagList = 
				annotationSearchApi.searchTags(VALUE, START_ON, LIMIT, null, null);
		
		assertNotNull(tagList);
		assertTrue(tagList.size() > 0);
	}
	
}
