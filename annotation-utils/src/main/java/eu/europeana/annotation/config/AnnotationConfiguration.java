package eu.europeana.annotation.config;

public interface AnnotationConfiguration {

	public static final String ANNOTATION_INDEXING_ENABLED = "annotation.indexing.enabled";
	public static final String ANNOTATION_ENVIRONMENT = "annotation.environment";
	
	public static final String SUFFIX_BASEURL = "baseUrl";
	public static final String VALUE_ENVIRONMENT_PRODUCTION = "production";
	public static final String VALUE_ENVIRONMENT_TEST = "test";
	public static final String VALUE_ENVIRONMENT_DEVELOPMENT = "development";
	
	public static final String DEFAULT_WHITELIST_RESOURCE_PATH = "annotation.whitelist.default";
	
	
	public String getComponentName();
	
	/**
	 * uses annotation.indexing.enabled property
	 */
	public boolean isIndexingEnabled();
	
	/**
	 * checks annotation.environment=production property
	 */
	public boolean isProductionEnvironment();
	
	/**
	 * uses annotation.environment property
	 */
	public String getEnvironment();
	
	
	/**
	 * uses annotation.environment.{$environment}.baseUrl property
	 */
	public String getAnnotationBaseUrl();
	
	/**
	 * uses annotation.whitelist.default property
	 */
	public String getDefaultWhitelistResourcePath();
	
}
