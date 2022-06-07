package eu.europeana.annotation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.testcontainers.containers.output.ToStringConsumer;
import org.testcontainers.containers.output.WaitingConsumer;
import eu.auropeana.annotation.testutils.MongoContainer;
import eu.auropeana.annotation.testutils.SolrContainer;

//probably the @ComponentScan annotation is not necessary beside the @ContextConfiguration
//@ComponentScan(basePackageClasses = AnnotationBasePackageMapper.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-tests-context.xml"})
@DirtiesContext
@WebAppConfiguration
public abstract class AbstractIntegrationTest {
  private static final Logger logger = LogManager.getLogger(AbstractIntegrationTest.class);
  private static final MongoContainer MONGO_CONTAINER;
  private static final SolrContainer SOLR_CONTAINER;

  static {
    MONGO_CONTAINER =
        new MongoContainer("annotation_test")
            .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

    MONGO_CONTAINER.start();

    SOLR_CONTAINER =
        new SolrContainer("anno-up")
            .withLogConsumer(new WaitingConsumer().andThen(new ToStringConsumer()));

    SOLR_CONTAINER.start();
    
    String annoPropUpdated = "src/test/resources/config/annotation-update.properties";       
    BufferedWriter bwAnnoPropUpdated;
    try {
      bwAnnoPropUpdated = new BufferedWriter(new FileWriter(new File(annoPropUpdated)));
      bwAnnoPropUpdated.append("mongodb.annotation.connectionUrl=" + MONGO_CONTAINER.getConnectionUrl() + "\n");
      bwAnnoPropUpdated.append("mongodb.annotation.databasename=" + MONGO_CONTAINER.getAnnotationDb() + "\n");
      bwAnnoPropUpdated.append("solr.annotation.url=" + SOLR_CONTAINER.getConnectionUrl() + "\n");
      bwAnnoPropUpdated.flush();
      bwAnnoPropUpdated.close();
    } catch (IOException e) {
      logger.log(Level.ERROR, "An Exception is thrown during creting the annotation-udate.properties file.", e);
    }
    


  }

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) throws IOException {
    
    registry.add("mongodb.annotation.connectionUrl", MONGO_CONTAINER::getConnectionUrl);
    registry.add("mongodb.annotation.databasename", MONGO_CONTAINER::getAnnotationDb);
    registry.add("solr.annotation.url", SOLR_CONTAINER::getConnectionUrl);    
    registry.add("annotation.indexing.enabled", () -> true);
    registry.add("annotation.environment", () -> "development");
    registry.add("annotation.whitelist.default", () -> "/config/default_whitelist.json");
    registry.add("profiler.expression", () -> "within(none.*)");
    registry.add("europeana.apikey.serviceurl", () -> "https://apikey.eanadev.org/apikey");
    registry.add("europeana.apikey.jwttoken.siganturekey", () -> "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgq2lkW7yOWM1mEIyE3zvJxHoRX6S9U8GJp3leNent2E7CXffk45clrpA2ElzH7OAWEoKEth+ORlHAeyAls4eqTyjimXv4HRVTxxL9PCrQDqsd9oVKXnQPbLYxaMRN9xLF2THBYVNJv7Bz1DT3CL+DAq9f5W9N0X+Nsik2+IE8IUDLWyfY2COQrpfS3gTTzHyt7BFDUbzvOuLs6jRuA2rFyYv1i8dN6vdX7WiamrLyTBLOLNGWwCCuV4qLdhbKMUl7S3jOkPg7WHy+lfkWmWAdeSP9wPTDnSJXpCIb+dbYUW6mhlbLNfQLksjxDAqLCE8MgMD6n/CJgVvf26GhlRxWQIDAQAB-----END PUBLIC KEY-----");
    registry.add("metis.baseUrl", () -> "http://metis-dereference-rest-acceptance.eanadev.org/dereference?uri=");
    registry.add("metis.connection.retries", () -> "3");
    registry.add("metis.connection.timeout", () -> "30000");
    registry.add("annotation.licenses", () -> "http://creativecommons.org/publicdomain/zero/,http://creativecommons.org/publicdomain/mark/,http://creativecommons.org/licenses/by/,http://rightsstatements.org/vocab/NoC-NC/,http://creativecommons.org/licenses/by-sa/,http://creativecommons.org/licenses/by-nd/,http://creativecommons.org/licenses/by-nc-sa/,http://creativecommons.org/licenses/by-nd-nc/");
    registry.add("annotation.apiVersion", () -> "0.3");
    registry.add("annotation.properties.timestamp", () -> "-1");
    registry.add("annotation.subtitles.formats", () -> "text/vtt,application/ttml+xml,video/quicktime");
    registry.add("annotation.subtitles.formats.xml", () -> "/subtitles-formats.xml");     
    registry.add("annotation.api.endpoint", () -> "https://api.europeana.eu/annotation");
    registry.add("annotation.data.endpoint", () -> "http://data.europeana.eu/annotation");
    registry.add("annotation.user.data.endpoint", () -> "http://data.europeana.eu/user");
    registry.add("annotation.client.api.endpoint", () -> "https://api.europeana.eu/apikey");
    registry.add("annotation.item.data.endpoint", () -> "http://data.europeana.eu/item");
    registry.add("annotation.remove.authorization", () -> true);    
   
    // could be used to fix eclipse issues
//    registry.add("scmBranch", () -> "dev");
//    registry.add("buildNumber", () -> "99");
//    registry.add("timestamp", () -> System.currentTimeMillis());

    logger.info("MONGO_CONTAINER : {}", MONGO_CONTAINER.getConnectionUrl());
    logger.info("SOLR_CONTAINER : {}", SOLR_CONTAINER.getConnectionUrl());
 }

}
