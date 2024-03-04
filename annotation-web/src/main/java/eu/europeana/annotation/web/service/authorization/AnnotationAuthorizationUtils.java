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
 
  private static Authentication createAuthentication(String userId, String userName, Role role, String apikey, String affiliation, String apiName) {
    return createAuthentication(userId, userName, role.getName(), apikey, affiliation, apiName);
  }
 
  private static Authentication createAuthentication(String userId, String userName,
      final String roleName, String apikey, String affiliation, String apiName) {
    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleName);
    return new EuropeanaAuthenticationToken(List.of(grantedAuthority), apiName, userId,
        new EuropeanaApiCredentials(userName, "test-client-id", apikey, affiliation));

  }

  public static Authentication createAuthentication(String plainTextToken, String apiName)
      throws AuthorizationExtractionException {
    
    //FAKE Tokens format: user_id:user_name:role:apikey(optional):affiliation(optional)
    final int MANDATORY_PARTS = 3; //mandatory separators + 1
    if (StringUtils.isBlank(plainTextToken)
        || StringUtils.countMatches(plainTextToken, ':') +1 < MANDATORY_PARTS) {
      throw new AuthorizationExtractionException("invalid plain text token: " + plainTextToken);
    } 
    String plainToken = plainTextToken.replace(OAuthUtils.TYPE_BEARER, "");
    String[] parts = plainToken.trim().split("\\:");
    final int apiKEyPos = 3;
    String apiKey = (parts.length > apiKEyPos) ? parts[apiKEyPos] : "noapikey";
    final int affiliationPos = 4;
    String affiliation = (parts.length > affiliationPos) ? parts[affiliationPos] : null;
    return createAuthentication(parts[0], parts[1], UserRoles.valueOf(parts[2]), apiKey, affiliation, apiName);
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
