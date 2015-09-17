package eu.europeana.annotation.web.service.impl;

import java.util.Properties;

import eu.europeana.annotation.web.service.AnnotationConfiguration;

public class AnnotationConfigurationImpl implements AnnotationConfiguration{

	private Properties annotationProperties;
	
	@Override
	public String getComponentName() {
		return "annotation";
	}

	@Override
	public boolean isIndexingEnabled() {
		String value = getAnnotationProperties().getProperty(ANNOTATION_INDEXING_ENABLED);
		return Boolean.valueOf(value);
	}

	public Properties getAnnotationProperties() {
		return annotationProperties;
	}

	public void setAnnotationProperties(Properties annotationProperties) {
		this.annotationProperties = annotationProperties;
	}

}
