package eu.europeana.annotation.web.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.cloudfoundry.VcapApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.web.context.support.StandardServletEnvironment;

public class VcapAnnotationPropertyLoaderListener extends VcapApplicationListener {

	public final static String VCAP = "vcap.services.";
	public final static String USERNAME = ".credentials.username";
	public final static String HOSTS = ".credentials.hosts";
	public final static String PASSWORD = ".credentials.password";
	public final static String PROP_TIMESTAMP = "annotation.properties.timestamp";
	

	private final static String MONGO_SERVICE = "mongo_service";
	File propertiesFileTemplate = null;
	File propertiesFile = null;

	private StandardServletEnvironment env;
	
	Logger logger = Logger.getLogger(getClass()); 

	public VcapAnnotationPropertyLoaderListener() {
		this(new StandardServletEnvironment(), null, null);
	}
	
	public VcapAnnotationPropertyLoaderListener(StandardServletEnvironment servletEnv, File propertiesFile, File propertiesFileTemplate) {
		super();
		this.propertiesFileTemplate = propertiesFileTemplate;
		this.propertiesFile = propertiesFile;
		env=servletEnv;
		this.onApplicationEvent(new ApplicationEnvironmentPreparedEvent(
				new SpringApplication(), new String[0], env));
	}
	
	
	
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		super.onApplicationEvent(event);

		if(env != null && env.getSystemEnvironment() != null && env.getSystemEnvironment().get(MONGO_SERVICE) != null)
			updateAnnotationProperties();
	}

	protected File getPropertiesFileTemplate() {
		String templateFileName = "annotation.properties.template";
		
		if(propertiesFileTemplate == null){
			getConfigFile(templateFileName);
		}
		return propertiesFileTemplate;
	}

	protected File getPropertiesFile() {
		String fileName = "annotation.properties";
		
		if(propertiesFile == null){
			getConfigFile(fileName);
		}
		return propertiesFile;
	}

	
	protected void getConfigFile(String filename) {
		ClassLoader c = getClass().getClassLoader();
		@SuppressWarnings("resource")
		URLClassLoader urlC = (URLClassLoader) c;
		URL[] urls = urlC.getURLs();
		String path = urls[0].getPath();
		propertiesFileTemplate = new File(path + "/config/"
				+ filename);
	}

	
	
	public void updateAnnotationProperties() {
	
		
		File annotationPropertiesFile = getPropertiesFile();
		File propertiesTemplate = getPropertiesFileTemplate();
		Properties props = new Properties();

		try {
			if(annotationPropertiesFile.exists()){
				logger.warn("The configuration file already exists. The configuration file will be overwritten: " + annotationPropertiesFile.getAbsolutePath());
				props.load(new FileInputStream(annotationPropertiesFile));
				annotationPropertiesFile.delete();
			}else{
				props.load(new FileInputStream(propertiesTemplate));
			}
		
			
			updateProps(props);
				
			writePropsToFile(props, annotationPropertiesFile);			
			
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error("Cannot update annotation properties! ", e1) ;
		}
	}

	protected void updateProps(Properties props) {
		String mongoDb = env.getSystemEnvironment().get(MONGO_SERVICE)
				.toString();
		String mongoUserName = VCAP + mongoDb + USERNAME;
		String mongoPassword = VCAP + mongoDb + PASSWORD;
		String mongoHosts = VCAP + mongoDb + HOSTS;

//				FileUtils.writeStringToFile(
//						annotationPropertiesFile,
//						"mongodb.annotation.username" + "="
//								+ env.getProperty(mongoUserName) + "\n", true);
		props.put("mongodb.annotation.username", env.getProperty(mongoUserName));
//				FileUtils.writeStringToFile(
//						annotationPropertiesFile,
//						"mongodb.annotation.password" + "="
//								+ env.getProperty(mongoPassword) + "\n", true);
		props.put("mongodb.annotation.password", env.getProperty(mongoPassword));
		
		logger.info(env.getProperty(mongoHosts));
		String[] hosts = env.getProperty(mongoHosts).replace('[', ' ')
				.replace("]", " ").split(",");
		//String mongoHost = "mongodb.annotation.host=";
		//String mongoPort = "mongodb.annotation.port=";
		String mongoHost = "";
		String mongoPort = "";
		
		for (String host : hosts) {
			mongoHost = mongoHost + host.split(":")[0].trim() + ",";
			mongoPort = mongoPort + host.split(":")[1].trim() + ",";
		}
//				FileUtils.writeStringToFile(annotationPropertiesFile,
//						mongoHost.substring(0, mongoHost.length() - 1) + "\n",
//						true);
//		FileUtils.writeStringToFile(annotationPropertiesFile,
//		mongoPort.substring(0, mongoPort.length() - 1) + "\n",
//		true);

		
		mongoHost = mongoHost.substring(0, mongoHost.length() - 1);
		mongoPort = mongoPort.substring(0, mongoPort.length() - 1);
		
		props.put("mongodb.annotation.host", mongoHost);
		props.put("mongodb.annotation.port", mongoPort);
	}

	protected void writePropsToFile(Properties props, File annotationPropertiesFile) throws IOException {
		FileUtils.writeStringToFile(
				annotationPropertiesFile,
				"\n### generated configurations ###\n", true);
		
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
	
//	 /**
//	   * Checks if a user-defined variable exists, and adds it to the properties file if it does
//	   * 
//	   * @param props
//	   * @param key
//	   * @throws IOException
//	   */
//	  protected void setHTTPProperty(Properties props, String key) throws IOException {
//	    String HTTP = "http://";
//
//	    if (env.containsProperty(key)) {
//	      props.setProperty(StringUtils.replaceChars(key, "_", "."), HTTP
//	          + env.getSystemEnvironment().get(key));
//	    }
//	  }
}
