package eu.europeana.annotation.mongo.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import eu.europeana.annotation.config.AnnotationConstants;
import eu.europeana.corelib.db.wrapper.ApiMongoConnector;

@Configuration
@PropertySource(
    value = {"classpath:annotation.properties"},
    ignoreResourceNotFound = true)
public class DataSourceConfig {

  private static final Logger logger = LogManager.getLogger(DataSourceConfig.class);

  @Value("${mongodb.annotation.connectionUrl}")
  private String hostUri;

  @Value("${mongodb.annotation.truststore}")
  private String trustStore;

  @Value("${mongodb.annotation.truststorepass}")
  private String trustStorePass;

  @Bean(AnnotationConstants.BEAN_ANNOTATION_DATA_STORE)
  public Datastore annotationDatastore() {
    logger.info("Configuring the annotation database.");
    ApiMongoConnector mongoConnector = new ApiMongoConnector();
    return mongoConnector.createDatastore(hostUri, trustStore, trustStorePass);
  }

}
