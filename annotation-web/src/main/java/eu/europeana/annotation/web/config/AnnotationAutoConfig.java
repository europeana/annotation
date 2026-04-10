package eu.europeana.annotation.web.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import eu.europeana.api.commons.auth.AuthenticationBuilder;
import eu.europeana.api.commons.auth.AuthenticationConfig;
import eu.europeana.api.commons.auth.AuthenticationHandler;
import eu.europeana.api.commons.oauth2.service.impl.EuropeanaClientDetailsService;

/**
  Configuration class responsible for setting up and providing the required beans.
 */
@Configuration()
@PropertySource(
    value = {"classpath:annotation.properties", "classpath:annotation.user.properties", "file:/opt/app/config/annotation.user.properties"},
    ignoreResourceNotFound = true)
public class AnnotationAutoConfig {
  private static final Logger LOG = LogManager.getLogger(AnnotationAutoConfig.class);

  @Value("${europeana.apikey.serviceurl}")
  private String apikeyServiceUrl;

  @Value("${keycloak.token.endpoint}")
  private String tokenEndpoint;

  @Value("${keycloak.token.grant.params}")
  private String grantParams;

  /**
   Create and instantiate {@code EuropeanaClientDetailsService} bean.
   @return A fully configured instance of {@code EuropeanaClientDetailsService}
   */
  @Bean(name = "europeanaClientDetailsService")
  public EuropeanaClientDetailsService getApiKeyClientDetailsService(){
    EuropeanaClientDetailsService clientDetails = new EuropeanaClientDetailsService();
    clientDetails.setApiKeyServiceUrl(apikeyServiceUrl);
    clientDetails.setAuthHandler(getAuthenticationHandler());
    return clientDetails;
  }

  @Bean(name = "searchApiAccess")
  public AuthenticationHandler getSearchApiAccess() {
    return getAuthenticationHandler();
  }
  /**
   * Generate AuthenticationHandler to access other services via EM ( like keycloak and SR API)
   * @return
   */
  public AuthenticationHandler getAuthenticationHandler() {
    if (StringUtils.isNotEmpty(tokenEndpoint) && StringUtils.isNotEmpty(grantParams)) {
      AuthenticationConfig config = new AuthenticationConfig(tokenEndpoint, grantParams);
      return AuthenticationBuilder.newAuthentication(config);
    } else {
      LOG.error("Keycloak token endpoint and parameters NOT set !!");
    }
    return null;
  }
}