package eu.europeana.annotation.web.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import eu.europeana.api.commons.net.socks.SocksProxyConfig;

/**
 * Activate the use of a Socks Proxy for all connections (if this is configured properly). Note that
 * this has to be done before any connections are set-up, so early during startup. Also note that
 * Java NIO classes do not support using a socks proxy.
 *
 * @author Patrick Ehlert on 20 sep 2020
 */
public final class SocksProxyActivator {

  private static final Logger LOG = LogManager.getLogger(SocksProxyActivator.class);
  private static final String PROP_SOCKS_PROXY_URL = "socks.proxy.url";

  private SocksProxyActivator() {
    
  }
 
  public static void activate(String propertiesFileName) {
    Properties props = loadProperties(propertiesFileName);
    if (props.containsKey(PROP_SOCKS_PROXY_URL)) {
      String socksProxyUrl = props.getProperty(PROP_SOCKS_PROXY_URL);
      if (StringUtils.isNotBlank(socksProxyUrl)) {
         try {
          SocksProxyConfig socksConfig = new SocksProxyConfig(socksProxyUrl);
          socksConfig.init();
        } catch (URISyntaxException e) {
           LOG.warn("Invalid SOCKS Proxy URL {},  skip socks proxy initialization!", socksProxyUrl,
                   e);
         }
      }
    }
  }

  private static Properties loadProperties(String propertiesFileName) {
    Properties props = new Properties();
    // check first additional configuration
    if (propertiesFileName.startsWith("/")) {
      loadPropertiesFromFileSystem(propertiesFileName, props);
    } else {
      loadPropertiesFromClasspath(propertiesFileName, props);
    }
    return props;
  }

  private static void loadPropertiesFromClasspath(String propertiesFileName, Properties props) {
    try (InputStream in =
                 Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName)) {
      if (in == null) {
        LOG.warn("Properties file {} does not exist in the classpath!", propertiesFileName);
      } else {
        props.load(in);
      }
    } catch (IOException e) {
      LOG.warn("Error reading properties classpath {}!", propertiesFileName, e);
    }
  }

  private static void loadPropertiesFromFileSystem(String propertiesFileName, Properties props) {
    File propsFile = new File(propertiesFileName);
    if (propsFile.exists()) {
      try(InputStream in = new FileInputStream(propsFile))  {
        props.load(in);
      } catch (IOException e) {
        LOG.warn("Cannot read properties file from system {}!", propertiesFileName);
      }
    }
  }

}
