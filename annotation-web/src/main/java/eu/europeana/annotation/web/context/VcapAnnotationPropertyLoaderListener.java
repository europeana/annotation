package eu.europeana.annotation.web.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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

	private final static String MONGO_SERVICE = "mongo_service";
	File propertiesFile = null;

	private static StandardServletEnvironment env;
	
	Logger logger = Logger.getLogger(getClass()); 

	public VcapAnnotationPropertyLoaderListener() {
		this(new StandardServletEnvironment(), null);
	}
	
	public VcapAnnotationPropertyLoaderListener(StandardServletEnvironment servletEnv, File propertiesFile) {
		super();
		this.propertiesFile = propertiesFile;
		env=servletEnv;
		this.onApplicationEvent(new ApplicationEnvironmentPreparedEvent(
				new SpringApplication(), new String[0], env));
	}
	
	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		super.onApplicationEvent(event);

		updateProperties(getPropertiesFile());
	}

	private File getPropertiesFile() {
		if(propertiesFile == null){
			ClassLoader c = getClass().getClassLoader();
			@SuppressWarnings("resource")
			URLClassLoader urlC = (URLClassLoader) c;
			URL[] urls = urlC.getURLs();
			String path = urls[0].getPath();
			propertiesFile = new File(path + "/config"
					+ "/annotation.properties");
		}
		return propertiesFile;
	}

	public void updateProperties(File annotationPropertiesFile) {
	
		
		Properties props = new Properties();

		try {
			if(!annotationPropertiesFile.exists()){
				logger.error("Cannot find the annotation.properties at location: " + annotationPropertiesFile.getAbsolutePath());;
				return;
			}
		
			props.load(new FileInputStream(annotationPropertiesFile));
			String key_timestamp = "annotation.properties.timestamp";
			String timestamp = (String)props.get(key_timestamp);
			
			if (StringUtils.isEmpty(timestamp) || Long.parseLong(timestamp) < 0 ) {
				
				String mongoDb = env.getSystemEnvironment().get(MONGO_SERVICE)
						.toString();
				String mongoUserName = VCAP + mongoDb + USERNAME;
				String mongoPassword = VCAP + mongoDb + PASSWORD;
				String mongoHosts = VCAP + mongoDb + HOSTS;
				FileUtils.writeStringToFile(
						annotationPropertiesFile,
						"\n### generated configurations ###\n", true);
				FileUtils.writeStringToFile(
						annotationPropertiesFile,
						"mongodb.annotation.username" + "="
								+ env.getProperty(mongoUserName) + "\n", true);
				FileUtils.writeStringToFile(
						annotationPropertiesFile,
						"mongodb.annotation.password" + "="
								+ env.getProperty(mongoPassword) + "\n", true);
				
				logger.info(env.getProperty(mongoHosts));
				String[] hosts = env.getProperty(mongoHosts).replace('[', ' ')
						.replace("]", " ").split(",");
				String mongoHost = "mongodb.annotation.host=";
				String mongoPort = "mongodb.annotation.port=";
				for (String host : hosts) {
					mongoHost = mongoHost + host.split(":")[0].trim() + ",";
					mongoPort = mongoPort + host.split(":")[1].trim() + ",";
				}
				FileUtils.writeStringToFile(annotationPropertiesFile,
						mongoHost.substring(0, mongoHost.length() - 1) + "\n",
						true);
				FileUtils.writeStringToFile(annotationPropertiesFile,
						mongoPort.substring(0, mongoPort.length() - 1) + "\n",
						true);
				
				//update timestamp
				FileUtils.writeStringToFile(annotationPropertiesFile,
						key_timestamp + "=" + System.currentTimeMillis() + "\n",
						true);
				
				
				
//				if (env.containsProperty(API2CANONICALURL)) {
//					FileUtils.writeStringToFile(
//							annotationProperties,
//							StringUtils
//									.replaceChars(API2CANONICALURL, "_", ".")
//									+ "="
//									+ HTTP
//									+ env.getSystemEnvironment().get(
//											API2CANONICALURL) + "\n", true);
//				}
//				if (env.containsProperty(API2URL)) {
//					FileUtils.writeStringToFile(
//							annotationProperties,
//							StringUtils.replaceChars(API2URL, "_", ".") + "="
//									+ HTTP
//									+ env.getSystemEnvironment().get(API2URL)
//									+ "\n", true);
//				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			logger.error("Cannot update annotation properties! ", e1) ;
		}
	}
}
