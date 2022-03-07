package eu.europeana.annotation.client;

import java.util.List;

import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;

public interface WebAnnotationAuxilaryMethodsApi {

    public List<String> getDeleted(String motivation, String from, String to, int page, int limit);

}
