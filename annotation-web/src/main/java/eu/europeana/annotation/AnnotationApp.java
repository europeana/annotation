package eu.europeana.annotation;

import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
@SpringBootApplication(scanBasePackages = {"eu.europeana.annotation"}, exclude = {
    // Remove these exclusions to re-enable security
    SecurityAutoConfiguration.class,
    // WebMvcAutoConfiguration.class,
    EmbeddedMongoAutoConfiguration.class, EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
    MongoAutoConfiguration.class, MongoDataAutoConfiguration.class,
    MongoMetricsAutoConfiguration.class,
    // MongoMetricsAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class,
    // DataSources are manually configured (for EM and batch DBs)
    DataSourceAutoConfiguration.class})
@ImportResource("classpath:annotation-web-context.xml")
public class AnnotationApp extends SpringBootServletInitializer {

  private static final Logger logger = LogManager.getLogger(AnnotationApp.class);

  /**
   * Main entry point of this application
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    ApplicationContext ctx = SpringApplication.run(AnnotationApp.class, args);

    if (logger.isDebugEnabled()) {
      printRegisteredBeans(ctx);
    }
  }

  private static void printRegisteredBeans(ApplicationContext ctx) {
    String[] beanNames = ctx.getBeanDefinitionNames();

    Arrays.sort(beanNames);
    logger.debug("Instantiated beans:");
    logger.debug(StringUtils.join(beanNames, "\n"));
  }    
}
