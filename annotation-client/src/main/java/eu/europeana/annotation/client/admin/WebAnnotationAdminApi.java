package eu.europeana.annotation.client.admin;

import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.model.result.AnnotationOperationResponse;

public interface WebAnnotationAdminApi {
	
	public ResponseEntity<String> deleteAnnotation(Integer id);

	public ResponseEntity<String> reindexOutdated();

}
