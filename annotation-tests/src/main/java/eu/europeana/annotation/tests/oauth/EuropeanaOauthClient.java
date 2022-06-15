package eu.europeana.annotation.tests.oauth;

import java.io.IOException;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;
import eu.europeana.annotation.tests.exception.TechnicalRuntimeException;

/**
 * @author GrafR
 */
public class EuropeanaOauthClient {

    public EuropeanaOauthClient() {
    }

    public static String getOauthToken(String user) {
	try {
	    String accessToken = "access_token";
	    String oauthUri = AnnotationTestsConfiguration.getInstance().getOauthServiceUri();
	    String oauthParams = AnnotationTestsConfiguration.getInstance().getOauthRequestParams(user);
	    
	    HttpConnection connection = new HttpConnection();
	    ResponseEntity<String> response;
	    response = connection.post(oauthUri, oauthParams, "application/x-www-form-urlencoded");

	    if (HttpStatus.OK == response.getStatusCode()) {
	      String body = response.getBody();
	      JSONObject json = new JSONObject(body);
	      if (json.has(accessToken)) {
	        return "Bearer " + json.getString(accessToken);
	      } else {
	        throw new TechnicalRuntimeException("Cannot extract authentication token from reponse:" + body);
	      }
	    } else {
	      throw new TechnicalRuntimeException("Error occured when calling oath service! " + response);
	    }
	} catch (IOException | JSONException e) {
	    throw new TechnicalRuntimeException("Cannot retrieve authentication token!", e);
	}
    }
}