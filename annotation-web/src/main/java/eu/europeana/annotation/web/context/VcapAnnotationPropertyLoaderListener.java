package eu.europeana.annotation.web.context;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.support.StandardServletEnvironment;

public class VcapAnnotationPropertyLoaderListener extends BasePropertyLoaderListener {

	public final static String VCAP_PROVIDER_A9S = "a9s";
	public final static String VCAP_PROVIDER_PIVOTAL = "pivotal";

	public VcapAnnotationPropertyLoaderListener() {
		this(new StandardServletEnvironment(), null, null, false);
	}

	public VcapAnnotationPropertyLoaderListener(ConfigurableEnvironment servletEnv, File propertiesFile,
			File propertiesFileTemplate, boolean loadOriginalFromTemplate) {
		super();
		this.propertiesFileTemplate = propertiesFileTemplate;
		this.propertiesFile = propertiesFile;
		this.loadOriginalFromTemplate = loadOriginalFromTemplate;
		env = servletEnv;
		onApplicationEvent(null);
	}

	protected boolean isA9sProvider(String vcapProvider) {
		return VCAP_PROVIDER_A9S.equals(vcapProvider);
	}

	protected boolean isPivotalProvider(String vcapProvider) {
		return VCAP_PROVIDER_PIVOTAL.equals(vcapProvider);
	}

	public void updateAnnotationProperties(String vcapProvider, String mongoServiceName) {
		logger.info("Updating annotation properties for mongo service: " + vcapProvider + " - " + mongoServiceName);
		try {

			Properties serverProperties = new Properties();
			loadProperties(serverProperties);

			updateProps(serverProperties, vcapProvider, mongoServiceName);

			writePropsToFile(serverProperties, getPropertiesFile());

		} catch (Exception e1) {
			e1.printStackTrace();
			logger.error("Cannot update annotation properties! ", e1);
		}
	}

	@Override
	protected void updateProps(Properties props, String vcapProvider, String mongoServiceName) {

		if (isA9sProvider(vcapProvider))
			updateA9sProps(props);
		else if (isPivotalProvider(vcapProvider))
			updatePivotalProps(props);
	}

	protected void updatePivotalProps(Properties props) {
		logger.info("mongodb.annotation.connectionUrl: " + env.getProperty(connectionUriKey));
		String connectionUrl = env.getProperty(connectionUriKey);
		URI connectionUri;
		try {
			connectionUri = new URI(connectionUrl);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			logger.error("Cannot update annotation properties! ", e);
			return;
		}

		// optional
		if (StringUtils.isNotBlank(connectionUri.getUserInfo())) {

			String[] userInfo = connectionUri.getUserInfo().split(":");

			logger.info("mongodb.annotation.username: " + userInfo[0]);
			props.put("mongodb.annotation.username",  userInfo[0]);

			// optional
			if (userInfo.length > 1) {
				logger.info("mongodb.annotation.password: " + userInfo[1]);
				props.put("mongodb.annotation.password", userInfo[1]);
			}

		}

		// mandatory
		logger.info("mongodb.annotation.host: " + connectionUri.getHost());
		props.put("mongodb.annotation.host", connectionUri.getHost());

		// optional
		if (StringUtils.isNotBlank("" + connectionUri.getPort())) {
			logger.info("mongodb.annotation.port: " + "" + connectionUri.getPort());
			props.put("mongodb.annotation.port", "" + connectionUri.getPort());
		}

		// mandatory
		//eliminate "/" from the database name
		String databse = connectionUri.getPath().substring(1);
		logger.info("mongodb.annotation.dbname: " + databse);
		props.put("mongodb.annotation.dbname", databse);

		logger.info("mongodb.annotation.connectionUrl: " + connectionUrl);
		props.put("mongodb.annotation.connectionUrl", connectionUrl);
	}

	protected void updateA9sProps(Properties props) {
		StringBuilder builder = new StringBuilder("mongodb://");

		//env.getProperty(mongoHostKey)
		
		// optional
		if (StringUtils.isNotBlank(env.getProperty(mongoUserNameKey))) {
			logger.info("mongodb.annotation.username: " + env.getProperty(mongoUserNameKey));
			props.put("mongodb.annotation.username", env.getProperty(mongoUserNameKey));
			builder.append(env.getProperty(mongoUserNameKey));

			// optional
			logger.info("mongodb.annotation.password: " + env.getProperty(mongoPasswordKey));
			if (StringUtils.isNotBlank(env.getProperty(mongoPasswordKey))) {
				props.put("mongodb.annotation.password", env.getProperty(mongoPasswordKey));
				builder.append(":").append(env.getProperty(mongoPasswordKey));
			}

			// user:pass@
			builder.append("@");
		}

		// mandatory
		logger.info("mongodb.annotation.host: " + env.getProperty(mongoHostKey));
		props.put("mongodb.annotation.host", env.getProperty(mongoHostKey));
		builder.append(env.getProperty(mongoHostKey));

		// optional
		if (StringUtils.isNotBlank(env.getProperty(mongoPortKey))) {
			logger.info("mongodb.annotation.port: " + env.getProperty(mongoPortKey));
			props.put("mongodb.annotation.port", env.getProperty(mongoPortKey));
			builder.append(":").append(env.getProperty(mongoPortKey));
		}

		// mandatory
		logger.info("mongodb.annotation.dbname: " + env.getProperty(mongoDatabaseKey));
		props.put("mongodb.annotation.dbname", env.getProperty(mongoDatabaseKey));
		builder.append("/").append(env.getProperty(mongoDatabaseKey));

		logger.info("mongodb.annotation.connectionUrl: " + builder.toString());
		props.put("mongodb.annotation.connectionUrl", builder.toString());
	}

//	@Override
//	protected String getMongoServiceName(String vcapProvider) throws FileNotFoundException, IOException {
////		String defaultMongoServiceName = "";
////		if (isPivotalProvider(vcapProvider))
////			defaultMongoServiceName = MONGO_SERVICE_PIVOTAL;
////		if (isA9sProvider(vcapProvider))
////			defaultMongoServiceName = MONGO_SERVICE;
////
////		String mongoServiceName = getOriginalProperties().getProperty("annotation.environment.vcap.mongoservice",
////				defaultMongoServiceName);
////		logger.info("Loading VCAP properties for mongo service: " + vcapProvider + " - " + mongoServiceName);
//
//		
//	}

	@Override
	protected boolean isValidVcapEnvironment(String vcapProvider, String mongoServiceName) {

		if (isPivotalProvider(vcapProvider) || isA9sProvider(vcapProvider))
			return super.isValidVcapEnvironment(vcapProvider, mongoServiceName);

		return false;
	}
}