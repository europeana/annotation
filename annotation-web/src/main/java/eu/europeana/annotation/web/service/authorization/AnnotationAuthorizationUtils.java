package eu.europeana.annotation.web.service.authorization;

import java.util.Iterator;
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
 * NOTE:
 * This class exists also in the set-api (here a new parameter "apiName" is added to the methods) and needs to be moved to the api-commons.
 * It implements authorization utils when fake tokens are used (not with real credentials)
 */
public class AnnotationAuthorizationUtils {
  
  private AnnotationAuthorizationUtils() {
    super();
  }
  
  public static Authentication createAuthentication(String userId, String userName, String apikey, Role role, String apiName) {
    return createAuthentication(userId, userName, apikey, role.getName(), apiName);
  }

  private static Authentication createAuthentication(String userId, String userName,
      String apikey, final String roleName, String apiName) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleName);
    return new EuropeanaAuthenticationToken(List.of(grantedAuthority), apiName, userId,
        new EuropeanaApiCredentials(userName, "unknown-client", apikey));

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
    String apiKey = (parts.length > 2) ? parts[2] : "noapikey"; 
    return createAuthentication(parts[0], parts[1], apiKey, UserRoles.valueOf(parts[SEPARATOR_COUNT]), apiName);
  }
  
  public static boolean hasRole(Authentication authentication, String userRole) {
    for (Iterator<? extends GrantedAuthority> iterator = authentication.getAuthorities().iterator(); iterator.hasNext();) {
        //role based authorization
        String role = iterator.next().getAuthority();
        if(role!=null && role.equals(userRole)){
            return true;
        }
    }
    return false;
}   
}
