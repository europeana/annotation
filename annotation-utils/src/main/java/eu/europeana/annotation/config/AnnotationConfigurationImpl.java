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

	
	public int getMaxPageSize(String profile) {
		String key = PREFIX_MAX_PAGE_SIZE + profile;
		return Integer.parseInt(getAnnotationProperties().getProperty(key));
	}

	
	public String getJwtTokenSignatureKey() {
	    return getAnnotationProperties().getProperty(KEY_APIKEY_JWTTOKEN_SIGNATUREKEY);
	}
	
	@Override
	public String getAuthorizationApiName() {
		return getAnnotationProperties().getProperty(AUTHORIZATION_API_NAME);
	}

	@Override
	public String getTranscriptionsLicenses() {
		return getAnnotationProperties().getProperty(TRANSCRIPTIONS_LICENSES);
	}
}
