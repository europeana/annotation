package eu.europeana.annotation.web.service.authorization;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import eu.europeana.annotation.web.model.vocabulary.UserRoles;
import eu.europeana.api.commons.definitions.vocabulary.Role;
import eu.europeana.api.commons.exception.AuthorizationExtractionException;
import eu.europeana.api.commons.oauth2.model.impl.EuropeanaApiCredentials;
import eu.europeana.api.commons.oauth2.model.impl.EuropeanaAuthenticationToken;
import eu.europeana.api.commons.oauth2.utils.OAuthUtils;

/* 
 * TODO:
 * This class exists also in the set-api (here a new parameter "apiName" is added to the methods) and needs to be moved to the api-commons.
 * It implements authorization utils when fake tokens are used (not with real credentials)
 */
public class AnnotationAuthorizationUtils {
  
  public static Authentication createAuthentication(String userId, String userName, Role role, String apiName) {
    return createAuthentication(userId, userName, role.getName(), apiName);
  }

  private static Authentication createAuthentication(String userId, String userName,
      final String roleName, String apiName) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleName);
    return new EuropeanaAuthenticationToken(List.of(grantedAuthority), apiName, userId,
        new EuropeanaApiCredentials(userName, "unknown-client"));

  }

  public static Authentication createAuthentication(String plainTextToken, String apiName)
      throws AuthorizationExtractionException {
    final int SEPARATOR_COUNT = 2;
    if (StringUtils.isBlank(plainTextToken)
        || StringUtils.countMatches(plainTextToken, ':') != SEPARATOR_COUNT) {
      throw new AuthorizationExtractionException("invalid plain text token: " + plainTextToken);
    } 
    String plainToken = plainTextToken.replace(OAuthUtils.TYPE_BEARER, "");
    String[] parts = plainToken.trim().split("\\:");
    return createAuthentication(parts[0], parts[1], UserRoles.valueOf(parts[SEPARATOR_COUNT]), apiName);
  }
}
