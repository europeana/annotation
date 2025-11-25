package eu.europeana.annotation.web.config;

import eu.europeana.api.commons.auth.AuthenticationBuilder;
import eu.europeana.api.commons.auth.AuthenticationConfig;
import eu.europeana.api.commons.oauth2.service.impl.EuropeanaClientDetailsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration()
@PropertySource(
    value = {"classpath:annotation.properties", "classpath:annotation.user.properties"},
    ignoreResourceNotFound = true)
public class AnnotationConfig {
  private static final Logger LOG = LogManager.getLogger(AnnotationConfig.class);

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
    if(StringUtils.isNotEmpty(tokenEndpoint) && StringUtils.isNotEmpty(grantParams)) {
    AuthenticationConfig config = new AuthenticationConfig(tokenEndpoint,grantParams);
    clientDetails.setAuthHandler(AuthenticationBuilder.newAuthentication(config));
    }else{
      LOG.error("Keycloak token-endpoint and/or grant-parameters NOT set !! ");
    }
    return clientDetails;
  }
}