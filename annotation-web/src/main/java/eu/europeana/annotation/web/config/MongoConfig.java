package eu.europeana.annotation.web.config;

import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.corelib.db.wrapper.ApiMongoConnector;

/**
 * This mongo config crates a org.mongodb.morphia.Datastore instead of a dev.morphia.Datastore
 * because the created mongo dao objects are based on the NosqlDaoImpl class from the commons-api
 * which uses the given Datastore type.
 * 
 * @author StevaneticS
 */
@Configuration
@PropertySource(
    value = {"classpath:config/annotation.properties", "classpath:config/annotation.user.properties"},
    ignoreResourceNotFound = true)
public class MongoConfig {

  @Value("${mongodb.annotation.connectionUrl:}")
  private String mongoConnectionUrl;

  @Value("${mongodb.annotation.truststore:}")
  private String mongoTrustStore;

  @Value("${mongodb.annotation.truststorepass:}")
  private String mongoTrustStorePass;

  private static final String MODEL_PACKAGE = "eu.europeana.annotation.definitions";
  
  //
  private ApiMongoConnector mongoConnector;
  
  @Bean(AnnotationConfiguration.BEAN_ANNO_MONGO_STORE)
  public Datastore createDataStore() {
    return getMongoConnector().createDatastore(mongoConnectionUrl, mongoTrustStore, mongoTrustStorePass, -1, MODEL_PACKAGE );
  }

  @Bean("annotationMongoConnector")
  protected ApiMongoConnector getMongoConnector() {
    if(mongoConnector == null) {
      return new ApiMongoConnector();
    }
    return mongoConnector;
  }
}
