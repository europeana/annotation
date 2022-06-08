package eu.europeana.annotation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
 * Main application. Allows deploying as a war and logs instance data when deployed in Cloud Foundry
 */
//@SpringBootApplication(scanBasePackages = {"eu.europeana.enrichment"},
@SpringBootApplication(scanBasePackages = {"eu.europeana.annotation"},
        exclude = {
                // Remove these exclusions to re-enable security
                SecurityAutoConfiguration.class,
                ManagementWebSecurityAutoConfiguration.class,
                // DataSources are manually configured for the enrichment DB
                DataSourceAutoConfiguration.class
        }
)
@EnableBatchProcessing
public class AnnotationApp extends SpringBootServletInitializer {

	static Properties props = new Properties();
	private static final String SOCKS_PROXY_URL = "socks.proxy.url";
	
    /**
     * Main entry point of this application
     * @param args command-line arguments
     * @throws IOException 
     * @throws URISyntaxException 
     */
    public static void main(String[] args) throws IOException, URISyntaxException {
        // When deploying to Cloud Foundry, this will log the instance index number, IP and GUID
        LogManager.getLogger(AnnotationApp.class).
                info("CF_INSTANCE_INDEX  = {}, CF_INSTANCE_GUID = {}, CF_INSTANCE_IP  = {}",
                    System.getenv("CF_INSTANCE_INDEX"),
                    System.getenv("CF_INSTANCE_GUID"),
                    System.getenv("CF_INSTANCE_IP"));

        loadProperties();
        registerSocksProxy();
        
        SpringApplication.run(AnnotationApp.class, args);
    }
    
 

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AnnotationApp.class);
    }
    
	private static void registerSocksProxy() throws URISyntaxException{
		String socksProxyUrl = props.getProperty(SOCKS_PROXY_URL);
		if(StringUtils.isBlank(socksProxyUrl))
			return;
		try {
			SocksProxyConfig socksProxy;
			socksProxy = new SocksProxyConfig(socksProxyUrl);
			socksProxy.init();
		} catch (URISyntaxException e) {
			throw e;
		}		
	}

	private static void loadProperties() throws IOException{
		String propsLocation = getAppConfigFile();
		try {
			EncodedResource propsResource = new EncodedResource( new ClassPathResource(propsLocation), "UTF-8");
			PropertiesLoaderUtils.fillProperties(props, propsResource);
		} catch (IOException e) {
			throw e;
		}
	}

	private static String getAppConfigFile() {
		return "config/annotation.properties";
	}
	

}
