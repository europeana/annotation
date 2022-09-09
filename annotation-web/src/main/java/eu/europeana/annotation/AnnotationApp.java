package eu.europeana.annotation;

import org.apache.logging.log4j.LogManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ImportResource;
import eu.europeana.annotation.config.SocksProxyActivator;
import eu.europeana.annotation.config.SocksProxyConfig;

/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
@SpringBootApplication(
    scanBasePackages = {"eu.europeana.annotation"},
    exclude = {
        // Remove these exclusions to re-enable security
        SecurityAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class,
        // DataSources are manually configured (for EM and batch DBs)
        DataSourceAutoConfiguration.class
      }    
)
@EnableAutoConfiguration(exclude = {WebMvcAutoConfiguration.class })
@ImportResource("classpath:annotation-web-mvc.xml")
public class AnnotationApp extends SpringBootServletInitializer {

  /**
   * Main entry point of this application
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    // When deploying to Cloud Foundry, this will log the instance index number, IP and GUID
    LogManager.getLogger(AnnotationApp.class)
        .info(
            "CF_INSTANCE_INDEX  = {}, CF_INSTANCE_GUID = {}, CF_INSTANCE_IP  = {}",
            System.getenv("CF_INSTANCE_INDEX"),
            System.getenv("CF_INSTANCE_GUID"),
            System.getenv("CF_INSTANCE_IP"));

    // Activate socks proxy (if your application requires it)
    SocksProxyActivator.activate(
        new SocksProxyConfig("config/annotation.properties"));

    SpringApplication.run(AnnotationApp.class, args);
  }
}