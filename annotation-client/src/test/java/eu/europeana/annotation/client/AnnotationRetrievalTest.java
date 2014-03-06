package eu.europeana.annotation.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationRetrievalTest {

	@Test
	public void getAnnotations(){
		//TODO: create object within the test and do not rely on the objects stored in the database
		AnnotationRetrieval retrievalApi = new AnnotationRetrievalApi();
		List<Annotation> results = retrievalApi.getAnnotations("testCollection", "testObject");
		assertNotNull(results);
		assertTrue(results.size() > 0);
		Gson gson = new Gson();
		
		for (Annotation annotation : results) {
			System.out.println(gson.toJson(annotation));
			
		}
		
	}
	
	@Test(expected = TechnicalRuntimeException.class)
	public void getAnnotationError(){
		AnnotationRetrieval retrievalApi = new AnnotationRetrievalApi();
		retrievalApi.getAnnotation("testCollection", "testObject", -1);		
	}
	
	@Test
	public void getAnnotation(){
		//TODO: create object within the test and do not rely on the objects stored in the database
		AnnotationRetrieval retrievalApi = new AnnotationRetrievalApi();
		List<Annotation> results = retrievalApi.getAnnotations("testCollection", "testObject");
		
		if(results.isEmpty()){
			System.out.println("No objects found in the database, test skipped");
			return;
		}
		
		Annotation anno = results.get(0);
		Annotation annotation = retrievalApi.getAnnotation(anno.getEuropeanaId(), anno.getAnnotationNr());
		
		assertNotNull(annotation);
		assertEquals(anno.getType(), annotation.getType());
		
		
	}
}
