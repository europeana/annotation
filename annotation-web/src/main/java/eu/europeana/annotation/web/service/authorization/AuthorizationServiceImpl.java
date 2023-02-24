package eu.europeana.annotation.web.service.authorization;

import java.util.Set;
import javax.annotation.Resource;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.web.model.vocabulary.Operations;
import eu.europeana.annotation.web.model.vocabulary.UserRoles;
import eu.europeana.api.commons.definitions.vocabulary.Role;
import eu.europeana.api.commons.nosql.service.ApiWriteLockService;
import eu.europeana.api.commons.service.authorization.BaseAuthorizationService;

public class AuthorizationServiceImpl extends BaseAuthorizationService
    implements AuthorizationService {

  @Resource
  AnnotationConfiguration configuration;

  @Resource(name = "commons_oauth2_europeanaClientDetailsService")
  ClientDetailsService clientDetailsService;

  @Resource(name = "annotation_db_apilockService")
  private ApiWriteLockService apiWriteLockService;
  
  private static final Set<String> maintenanceOperations = Set.of(Operations.WRITE_UNLOCK, Operations.ADMIN_UNLOCK, Operations.ADMIN_REINDEX);

  public AuthorizationServiceImpl() {}

  // @Override
  protected String getAuthorizationApiName() {
    return getConfiguration().getAuthorizationApiName();
  }

  @Override
  protected ClientDetailsService getClientDetailsService() {
    return clientDetailsService;
  }

  @Override
  protected String getSignatureKey() {
    return getConfiguration().getJwtTokenSignatureKey();
  }

  @Override
  protected Role getRoleByName(String name) {
    return UserRoles.getRoleByName(name);
  }

  @Override
  protected String getApiName() {
    return getConfiguration().getAuthorizationApiName();
  }

  @Override
  protected ApiWriteLockService getApiWriteLockService() {
    return apiWriteLockService;
  }

  protected Set<String> getMaintenanceOperations(){
    return maintenanceOperations;
  }
  
  public AnnotationConfiguration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(AnnotationConfiguration configuration) {
    this.configuration = configuration;
  }



}
