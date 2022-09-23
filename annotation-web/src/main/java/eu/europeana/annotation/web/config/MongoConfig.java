package eu.europeana.annotation.web.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import eu.europeana.annotation.config.AnnotationConfiguration;

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

  private static final Logger logger = LogManager.getLogger(MongoConfig.class);

  @Value("${mongodb.annotation.host}")
  private String host;

  @Value("${mongodb.annotation.port}")
  private int port;

  @Value("${mongo.max.idle.time.millisec: 10000}")
  private long mongoMaxIdleTimeMillisec;

  @Value("${mongodb.annotation.databasename}")
  private String annoDatabase;
  
  @Value("${mongodb.annotation.username}")
  private String username;
  
  @Value("${mongodb.annotation.password}")
  private String password;
  
  @Bean(AnnotationConfiguration.BEAN_ANNO_MONGO_STORE)
  public Datastore createDataStore() {
    logger.info("Configuring annotation database: {}", annoDatabase);
    MongoClient client = null;
    if(!username.isBlank() && !password.isBlank()) {
      //creating the credentials to authenticate to a mongo server (the "admin" db is the one where the user is saved)
      MongoCredential credential = MongoCredential.createCredential(username, "admin", password.toCharArray());
      List<ServerAddress> hosts = new ArrayList<>();
      hosts.add(new ServerAddress(host, port));
      MongoClientOptions options = MongoClientOptions.builder().connectionsPerHost(10000)
          .heartbeatConnectTimeout(10000)
          .heartbeatFrequency(10000)
          .build();
      client = new MongoClient(hosts, credential, options);
    }
    else {
      List<ServerAddress> hosts = new ArrayList<>();
      hosts.add(new ServerAddress(host, port));
      client = new MongoClient(hosts);
    }
    Morphia morphia = new Morphia();
    return morphia.createDatastore(client, annoDatabase);
  }

}
