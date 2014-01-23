package eu.europeana.annotation.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

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
}
