package eu.europeana.annotation.definitions.model.impl;

public interface AnnotationDeletion {

    void setTimestamp(Long timestamp);

    Long getTimestamp();

    void setResourceId(String resourceId);

    String getResourceId();

    void setAnnotaionId(String annotaionId);

    String getAnnotaionId();

}