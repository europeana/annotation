package eu.europeana.annotation.config;

import java.util.Properties;

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

	@Override
	public boolean isProductionEnvironment() {
		// TODO Auto-generated method stub
		return VALUE_ENVIRONMENT_PRODUCTION.equals(getEnvironment());
	}

	@Override
	public String getEnvironment() {
		return getAnnotationProperties().getProperty(ANNOTATION_ENVIRONMENT);
	}

	@Override
	public String getAnnotationBaseUrl() {
		String key = ANNOTATION_ENVIRONMENT + "." + getEnvironment() + "." + SUFFIX_BASEURL; 
		return getAnnotationProperties().getProperty(key);
	}

	public String getDefaultWhitelistResourcePath() {
		return getAnnotationProperties().getProperty(DEFAULT_WHITELIST_RESOURCE_PATH);
	}

	public String getValidationApi() {
		return getAnnotationProperties().getProperty(VALIDATION_API);
	}

	public String getValidationAdminApiKey() {
		return getAnnotationProperties().getProperty(VALIDATION_ADMIN_API_KEY);
	}

	public String getValidationAdminSecretKey() {
		return getAnnotationProperties().getProperty(VALIDATION_ADMIN_SECRET_KEY);
	}

	public int getMaxPageSize(String profile) {
		String key = PREFIX_MAX_PAGE_SIZE + profile;
		return Integer.parseInt(getAnnotationProperties().getProperty(key));
	}

	@Override
	public long getApiKeyCachingTime() {
		return Long.parseLong(getAnnotationProperties().getProperty(API_KEY_CACHING_TIME));
	}
	
}
