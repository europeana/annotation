package eu.europeana.annotation.web.config;

import eu.europeana.api.commons.auth.AuthenticationBuilder;
import eu.europeana.api.commons.auth.AuthenticationConfig;
import eu.europeana.api.commons.oauth2.service.impl.EuropeanaClientDetailsService;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration()
@PropertySource(
    value = {"classpath:annotation.properties", "classpath:annotation.user.properties"},
    ignoreResourceNotFound = true)
public class AnnotationConfig {
  @Value("${europeana.apikey.serviceurl}")
  private String apikeyServiceUrl;

  @Value("${keycloak.token.endpoint}")
  private String tokenEndpoint;

  @Value("${keycloak.token.grant.params}")
  private String grantParams;


  @Bean(name = "commons_oauth2_europeanaClientDetailsService")
  public EuropeanaClientDetailsService getApiKeyClientDetailsService(){
    EuropeanaClientDetailsService clientDetails = new EuropeanaClientDetailsService();
    clientDetails.setApiKeyServiceUrl(apikeyServiceUrl);
    AuthenticationConfig config = new AuthenticationConfig(loadProperties());
    clientDetails.setAuthHandler(AuthenticationBuilder.newAuthentication(config));
    return clientDetails;
  }

  private Properties loadProperties() {
    Properties properties = new Properties();
    properties.setProperty(AuthenticationConfig.CONFIG_TOKEN_ENDPOINT,tokenEndpoint);
    properties.setProperty(AuthenticationConfig.CONFIG_GRANT_PARAMS,grantParams);
    return properties;
  }

}