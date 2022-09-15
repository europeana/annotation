package eu.europeana.annotation.config;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Activate the use of a Socks Proxy for all connections (if this is configured properly). Note that
 * this has to be done before any connections are set-up, so early during startup. Also note that
 * Java NIO classes do not support using a socks proxy.
 *
 * @author Patrick Ehlert on 20 sep 2020
 */
public final class SocksProxyActivator {

  private static final Logger LOG = LogManager.getLogger(SocksProxyActivator.class);

  private SocksProxyActivator() {
    // empty constructor to prevent initialization
  }

  /**
   * This will activate the use of a Socks Proxy if the provided configuration is valid and has it
   * enabled.
   *
   * @param config SockProxyConfig object
   * @return true if it was activated, false if no socks settings or a "enabled=false" setting was
   *     found
   */
  public static boolean activate(SocksProxyConfig config) {
    if (!isValidConfiguration(config)) {
      return false;
    }

    System.setProperty("socksProxyHost", config.getHost());
    System.setProperty("socksProxyPort", config.getPort());

    String user = config.getUser();
    if (StringUtils.isNotBlank(user)) {
      String pass = config.getPassword();
      System.setProperty("java.net.socks.username", user);
      System.setProperty("java.net.socks.password", pass);
      Authenticator.setDefault(new SockProxyAuthenticator(user, pass));
    }
    return true;
  }

  /**
   * Check if the provided configuration contains settings for activating a socks proxy and if it is
   * set to enabled
   *
   * @return true if there is at least a socks host defined and the socks enabled setting is true
   */
  private static boolean isValidConfiguration(SocksProxyConfig config) {
    boolean result = false;
    if (StringUtils.isEmpty(config.getHost())) {
      LOG.info("No socks proxy configured");
    } else if (!config.isSocksEnabled()) {
      LOG.info("Socks proxy disabled");
    } else {
      LOG.info("Setting up socks proxy at {}", config.getHost());
      result = true;
    }
    return result;
  }

  private static class SockProxyAuthenticator extends Authenticator {

    private final String user;
    private final char[] password;

    SockProxyAuthenticator(String user, String password) {
      this.user = user;
      this.password = password.toCharArray();
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, password);
    }
  }
}
