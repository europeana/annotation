package eu.europeana.annotation.web.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

public abstract class BasePropertyLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

	private static final String VCAP_SERVICES = "VCAP_SERVICES";
	public final static String VCAP_SERVICES_PREFIX = "vcap.services.";
	public final static String MONGO_SERVICE = "mongo_service";
	public final static String CREDENTIALS_USERNAME = ".credentials.username";
	public final static String CREDENTIALS_DB = ".credentials.db";
	//public final static String CREDENTIALS_DB = ".credentials.db";
	
	//public final static String DATABASE = ".credentials.db";
	// public final static String HOSTS = ".credentials.hosts";
	public final static String CREDENTIALS_HOST = ".credentials.host";
	public final static String CREDENTIALS_PORT = ".credentials.port";
	public final static String CREDENTIALS_URI = ".credentials.uri";

	public final static String CREDENTIALS_PASSWORD = ".credentials.password";
	public final static String PROP_TIMESTAMP = "annotation.properties.timestamp";

	protected ConfigurableEnvironment env;
	boolean loadOriginalFromTemplate = false;
	
	String mongoService;
	String mongoDatabaseKey;
	String mongoUserNameKey;
	String mongoPasswordKey;
	String mongoHostKey;
	String mongoPortKey;
	String connectionUriKey;

	File propertiesFileTemplate = null;
	File propertiesFile = null;
	
	Properties originalProperties;

	Logger logger = LogManager.getLogger(getClass());

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		
		String vcapProvider;
		try {
			vcapProvider = getVcapProvider();
		} catch (Throwable th) {
			logger.warn("Cannot read VcapProvider!" + th.getMessage());
			th.printStackTrace();
			return;
		}
		
		String mongoServiceName;
		try {
			mongoServiceName = getMongoServiceName(vcapProvider);
		} catch (Throwable th) {
			logger.warn("Cannot read mongo service name!" + th.getMessage());
			th.printStackTrace();
			return;
		}
	
		
		if(isValidVcapEnvironment(vcapProvider, mongoServiceName)){
			//process environment variables and flatten json structure
			CloudFoundryVcapEnvironmentPostProcessor postProcessor = new CloudFoundryVcapEnvironmentPostProcessor();
			postProcessor.postProcessEnvironment(getEnv(), null);
			
			if(logger.isDebugEnabled()){
				printSystemEnvironmentKeys();
			}
			
			buildMongoServiceKeys(mongoServiceName);
			
			//generate updated annotation.properties file
			updateAnnotationProperties(vcapProvider, mongoServiceName);
		}else{
			
		}
	}

	protected void printSystemEnvironmentKeys() {
		Set<String> keys = getEnv().getSystemEnvironment().keySet();
		StringBuilder builder = new StringBuilder("Available System environment properties (keys): ");
		for (String key : keys) {
			builder.append(key).append(", ");
		}
		
		logger.debug(builder.toString());
		
		Object vcapServices = getEnv().getSystemEnvironment().get(VCAP_SERVICES);
		logger.debug("VcapServices: " + vcapServices);
				
		if(getEnv().getSystemEnvironment().get(getMongoServiceName(null)) != null){
			logger.debug("Found variables for service: " + getMongoServiceName(null));
		}
	}

	public abstract void updateAnnotationProperties(String vcapProvider, String mongoServiceName);
	
	protected void buildMongoServiceKeys(String mongoServiceName) {
		
		mongoService = (String) env.getSystemEnvironment().get(mongoServiceName);
		logger.info("Configured mongo service: " +  mongoService);
		
		mongoDatabaseKey = buildDbNamePropKey(mongoService);
		mongoUserNameKey = buildDbUserNamePropKey(mongoService);
		mongoPasswordKey = buildDBPassPropKey(mongoService);
		mongoHostKey = buildDbHostPropKey(mongoService);
		mongoPortKey = buildDbPortPropKey(mongoService);
		connectionUriKey = buildDbConnectionUrlPropKey(mongoService);
	}

	protected boolean isValidVcapEnvironment(String vcapProvider, String mongoServiceName){
		return getEnv() != null && getEnv().getSystemEnvironment() != null
				&& StringUtils.isNotBlank(mongoServiceName) && env.getSystemEnvironment().get(mongoServiceName) != null;
	}
	
	protected String getMongoServiceName(String vcapProvider){
		return MONGO_SERVICE;
	}
	
	protected String getVcapProvider() throws FileNotFoundException, IOException {
		String vcapProvider = getOriginalProperties().getProperty("annotation.environment.vcap.provider");
		if(StringUtils.isNotBlank(vcapProvider))
			logger.info("Loading VCAP properties for provider: " + vcapProvider);
		
		return vcapProvider;
	}
	
	protected Properties getOriginalProperties() throws IOException, FileNotFoundException {
		
		if(originalProperties == null){
			originalProperties = new Properties();
			loadProperties(originalProperties);			
		}
		
		return originalProperties;
	}

	protected void loadProperties(Properties props) throws IOException, FileNotFoundException {
		//load by default from 
		boolean loadFromFile = !loadOriginalFromTemplate; 
		if(loadFromFile && getPropertiesFile().exists())
			loadProperties(getPropertiesFile(), props);
		else if(getPropertiesFileTemplate().exists())
			loadProperties(getPropertiesFileTemplate(), props);
	}

	public ConfigurableEnvironment getEnv() {
		return env;
	}

	protected String buildDbConnectionUrlPropKey(String mongoService) {
		return VCAP_SERVICES_PREFIX + mongoService + CREDENTIALS_URI;
	}

	protected String buildDbPortPropKey(String mongoDb) {
		return VCAP_SERVICES_PREFIX + mongoDb + CREDENTIALS_PORT;
	}

	protected String buildDbHostPropKey(String mongoDb) {
		return VCAP_SERVICES_PREFIX + mongoDb + CREDENTIALS_HOST;
	}

	protected String buildDBPassPropKey(String mongoDb) {
		return VCAP_SERVICES_PREFIX + mongoDb + CREDENTIALS_PASSWORD;
	}

	protected String buildDbUserNamePropKey(String mongoDb) {
		return VCAP_SERVICES_PREFIX + mongoDb + CREDENTIALS_USERNAME;
	}

	protected String buildDbNamePropKey(String mongoDb) {
		return VCAP_SERVICES_PREFIX + mongoDb + CREDENTIALS_DB;
	}

//	protected Properties loadProperties() throws IOException, FileNotFoundException {
//		
//		Properties props = new Properties();
//	
//		if(getPropertiesFile().exists())
//			loadProperties(getPropertiesFile(), props);
//		else if(getPropertiesFileTemplate().exists())	
//			loadProperties(getPropertiesFileTemplate(), props);
//		
//		return props;
//	}

	protected void loadProperties(File annotationPropertiesFile, Properties props) throws IOException, FileNotFoundException {
		
		
		if(annotationPropertiesFile.exists()){
			logger.debug("Loading annotationProperties from file: " + annotationPropertiesFile.getAbsolutePath());
			FileInputStream inStream = new FileInputStream(annotationPropertiesFile);
			props.load(inStream);
			
			if(inStream != null){
				try{
					inStream.close();
				}catch(Throwable th){
					logger.warn("Cannot close input stream for properties file. " + annotationPropertiesFile.getAbsolutePath(), th);
				}
			}
		}else{
			logger.warn("Annotation properties file doesn't exist! " + annotationPropertiesFile.getAbsolutePath());		
		}
	}

	protected File getPropertiesFileTemplate() {
		String defaultTemplateFileName = "annotation.properties.vcap.template";
		
		if(propertiesFileTemplate == null){
			propertiesFileTemplate = getConfigFile(defaultTemplateFileName);
		}
		return propertiesFileTemplate;
	}

	protected File getPropertiesFile() {
		
		if(propertiesFile == null){
			//if not already set, initilize with the default filename
			propertiesFile = getConfigFile("annotation.properties");
		}
		return propertiesFile;
	}

	protected File getConfigFile(String filename) {
		ClassLoader c = getClass().getClassLoader();
		@SuppressWarnings("resource")
		URLClassLoader urlC = (URLClassLoader) c;
		URL[] urls = urlC.getURLs();
		String path = urls[0].getPath();
		return new File(path + "/config/"
				+ filename);
	}

	protected void writePropsToFile(Properties props, File annotationPropertiesFile) throws IOException {
		// overwrite existing file by setting append to false
		logger.warn("The configuration file already exists. The configuration file will be overwritten: "
				+ annotationPropertiesFile.getAbsolutePath());
		FileUtils.writeStringToFile(annotationPropertiesFile, "\n### generated configurations ###\n", false);
	
		if (props.containsKey(PROP_TIMESTAMP))
			props.remove(PROP_TIMESTAMP);
	
		for (Entry<Object, Object> entry : props.entrySet()) {
			FileUtils.writeStringToFile(annotationPropertiesFile,
					entry.getKey().toString() + "=" + entry.getValue().toString() + "\n", true);
		}
	
		// update timestamp
		FileUtils.writeStringToFile(annotationPropertiesFile, PROP_TIMESTAMP + "=" + System.currentTimeMillis() + "\n",
				true);
	}

	
	protected abstract void updateProps(Properties props, String vcapProvider, String mongoServiceName);

	
}
