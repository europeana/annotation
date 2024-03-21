package eu.europeana.annotation.client;

import java.util.List;

public interface WebAnnotationAuxilaryMethodsApi {

    public List<String> getDeleted(String motivation, String from, String to, int page, int limit);

}
