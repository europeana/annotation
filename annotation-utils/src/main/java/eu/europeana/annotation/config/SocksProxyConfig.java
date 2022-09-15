package eu.europeana.annotation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Load the socks proxy configuration from the provided properties files. Note that we load files
 * manually and not use default Spring-Boot functionality here because this needs to be done early
 * during start-up before any connections are set-up.
 *
 * @author Lúthien on 13 jan 2018
 * @author Patrick Ehlert major refactoring in September 2020
 */
public class SocksProxyConfig {

  private static final String SOCKS_ENABLED = "socks.enabled";
  private static final String SOCKS_HOST = "socks.host";
  private static final String SOCKS_PORT = "socks.port";
  private static final String SOCKS_USER = "socks.user";
  private static final String SOCKS_PASS = "socks.password";

  private static final Logger LOG = LogManager.getLogger(SocksProxyConfig.class);

  private final Properties props = new Properties();

  /**
   * Load the provided properties files. Files are read in order they are provided, so the same
   * properties defined later will override previous provided properties. Each file should be on the
   * class path.
   *
   * @param propertiesFileNames properties files to read. If a file cannot be found a warning is
   *     logged
   */
  public SocksProxyConfig(String... propertiesFileNames) {
    for (String fileName : propertiesFileNames) {
      addProperties(fileName);
    }
  }

  private void addProperties(String propertiesFileName) {
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(propertiesFileName)) {
      if (in == null) {
        LOG.warn("Properties file {} does not exist!", propertiesFileName);
      } else {
        props.load(in);
      }
    } catch (IOException e) {
      LOG.error("Error reading properties file {}!", propertiesFileName, e);
    }
  }

  public boolean isSocksEnabled() {
    return Boolean.parseBoolean(props.getProperty(SOCKS_ENABLED, "false"));
  }

  public String getHost() {
    return props.getProperty(SOCKS_HOST);
  }

  public String getPort() {
    return props.getProperty(SOCKS_PORT);
  }

  public String getUser() {
    return props.getProperty(SOCKS_USER);
  }

  public String getPassword() {
    return props.getProperty(SOCKS_PASS);
  }
}
