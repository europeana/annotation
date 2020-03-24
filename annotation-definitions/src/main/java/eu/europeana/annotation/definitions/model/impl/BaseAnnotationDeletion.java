package eu.europeana.annotation.definitions.model.impl;

public class BaseAnnotationDeletion implements AnnotationDeletion {
    String annotaionId;
    String resourceId;
    Long timestamp;
    
    @Override
    public String getAnnotaionId() {
        return annotaionId;
    }
    @Override
    public void setAnnotaionId(String annotaionId) {
        this.annotaionId = annotaionId;
    }
    @Override
    public String getResourceId() {
        return resourceId;
    }
    @Override
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    @Override
    public Long getTimestamp() {
        return timestamp;
    }
    @Override
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
