package eu.europeana.annotation.web.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.cloud.CloudFoundryVcapEnvironmentPostProcessor;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

public class VcapAnnotationPropertyLoaderListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent>{

	public final static String VCAP = "vcap.services.";
	public final static String USERNAME = ".credentials.username";
	public final static String DATABASE = ".credentials.db";
//	public final static String HOSTS = ".credentials.hosts";
	public final static String HOST = ".credentials.host";
	public final static String PORT = ".credentials.port";
	
	public final static String PASSWORD = ".credentials.password";
	public final static String PROP_TIMESTAMP = "annotation.properties.timestamp";
	

	private final static String MONGO_SERVICE = "mongo_service";
	File propertiesFileTemplate = null;
	File propertiesFile = null;
	
	Properties originalProperties;

	private ConfigurableEnvironment env;
	
	Logger logger = Logger.getLogger(getClass()); 

	public VcapAnnotationPropertyLoaderListener() {
		this(new StandardServletEnvironment(), null, null);
	}
	
	public VcapAnnotationPropertyLoaderListener(ConfigurableEnvironment servletEnv, File propertiesFile, File propertiesFileTemplate) {
		super();
		this.propertiesFileTemplate = propertiesFileTemplate;
		this.propertiesFile = propertiesFile;
		env=servletEnv;
		onApplicationEvent(null);
	}
	
	
	
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		
		String mongoServiceName;
		try {
			mongoServiceName = getMongoServiceName();
		} catch (Throwable th) {
			logger.error("Cannot read mongo service name!" + th.getMessage());
			th.printStackTrace();
			return;
		}
		
		if(env != null && env.getSystemEnvironment() != null && StringUtils.isNotBlank(mongoServiceName) && env.getSystemEnvironment().get(mongoServiceName) != null){
			//process environment variables and flatten json structure
			CloudFoundryVcapEnvironmentPostProcessor postProcessor = new CloudFoundryVcapEnvironmentPostProcessor();
			postProcessor.postProcessEnvironment(env, null);
			
			//generate updated annotation.properties file
			updateAnnotationProperties(mongoServiceName);
		}
	}

	protected String getMongoServiceName() throws FileNotFoundException, IOException {
		originalProperties = loadProperties();
		String mongoServiceName = originalProperties.getProperty("annotation.environment.vcap.mongoservice", MONGO_SERVICE);
		if(StringUtils.isNotBlank(mongoServiceName))
			logger.info("Loading VCAP properties for mongo service: " + mongoServiceName);
		
		return mongoServiceName;
	}

	protected File getPropertiesFileTemplate() {
		String templateFileName = "annotation.properties.vcap.template";
		
		if(propertiesFileTemplate == null){
			propertiesFileTemplate = getConfigFile(templateFileName);
		}
		return propertiesFileTemplate;
	}

	protected File getPropertiesFile() {
		String fileName = "annotation.properties";
		
		if(propertiesFile == null){
			propertiesFile = getConfigFile(fileName);
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

	
	
	public void updateAnnotationProperties(String mongoServiceName) {
		logger.info("Updating annotation properties for mongo service: " + mongoServiceName) ;
		try {
			
			Properties serverProperties = loadProperties();
			
			updateProps(serverProperties, mongoServiceName);
				
			writePropsToFile(serverProperties, getPropertiesFile());			
			
		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("Cannot update annotation properties! ", e1) ;
		}
	}

	protected Properties loadProperties() throws IOException, FileNotFoundException {
		Properties props = new Properties();

		File annotationPropertiesFile = getPropertiesFile();
		loadProperties(annotationPropertiesFile, props);
		
		// Load properties from template if regular property file doesn't exist
		if(!annotationPropertiesFile.exists()){
			File propertiesTemplate = getPropertiesFileTemplate();
			logger.info("Load configuration properties from template: " + propertiesTemplate.getAbsolutePath());
			props.load(new FileInputStream(propertiesTemplate));
		}
		return props;
	}

	protected void loadProperties(File annotationPropertiesFile, Properties props)
			throws IOException, FileNotFoundException {
		if(annotationPropertiesFile.exists()){
			FileInputStream inStream = new FileInputStream(getPropertiesFile());
			props.load(inStream);
			
			if(inStream != null){
				try{
					inStream.close();
				}catch(Throwable th){
					logger.warn("Cannot close input stream for properties file. " + annotationPropertiesFile.getAbsolutePath(), th);
				}
			}
		}
	}

	protected void updateProps(Properties props, String mongoServiceName) {
		
		String mongoDb = env.getSystemEnvironment().get(mongoServiceName)
				.toString();
		logger.info("Configured mongo service" +  mongoDb);
		
		
		String mongoDatabase = VCAP + mongoDb + DATABASE;
		String mongoUserName = VCAP + mongoDb + USERNAME;
		String mongoPassword = VCAP + mongoDb + PASSWORD;
		String mongoHost = VCAP + mongoDb + HOST;
		String mongoPort = VCAP + mongoDb + PORT;

		logger.info("mongodb.annotation.dbname: " +  env.getProperty(mongoDatabase));
		logger.info("mongodb.annotation.username: " +  env.getProperty(mongoUserName));
		logger.info("mongodb.annotation.password: " + env.getProperty(mongoPassword));
		logger.info("mongodb.annotation.host: " + env.getProperty(mongoHost));
		logger.info("mongodb.annotation.port: " + env.getProperty(mongoPort));
		
		props.put("mongodb.annotation.dbname", env.getProperty(mongoDatabase));
		props.put("mongodb.annotation.username", env.getProperty(mongoUserName));
		props.put("mongodb.annotation.password", env.getProperty(mongoPassword));
		props.put("mongodb.annotation.host", env.getProperty(mongoHost));
		props.put("mongodb.annotation.port", env.getProperty(mongoPort));
		
		//props.put(AnnotationConfiguration.ANNOTATION_ENVIRONMENT, AnnotationConfiguration.VALUE_ENVIRONMENT_TEST);
	}

	protected void writePropsToFile(Properties props, File annotationPropertiesFile) throws IOException {
		//overwrite existing file by setting append to false
		logger.warn("The configuration file already exists. The configuration file will be overwritten: " + annotationPropertiesFile.getAbsolutePath());
		FileUtils.writeStringToFile(
				annotationPropertiesFile,
				"\n### generated configurations ###\n", false);
		
		if(props.containsKey(PROP_TIMESTAMP))
			props.remove(PROP_TIMESTAMP);
		
		for (Entry<Object, Object> entry : props.entrySet()) {
			FileUtils.writeStringToFile(
					annotationPropertiesFile,
					entry.getKey().toString() + "=" + entry.getValue().toString() + "\n", true);
		}
		
		//update timestamp
		FileUtils.writeStringToFile(annotationPropertiesFile,
				PROP_TIMESTAMP + "=" + System.currentTimeMillis() + "\n",
				true);
	}
	
}
