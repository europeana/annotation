package eu.europeana.annotation.web.http;

import eu.europeana.api.commons.web.http.HttpHeaders;

public interface AnnotationHttpHeaders extends HttpHeaders {

	public static final String ALLOW_GPuDOH = "GET,PUT,DELETE,OPTIONS,HEAD";
	
	public static final String VALUE_LDP_CONTAINER = "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"\n"+
			"<http://www.w3.org/TR/annotation-protocol/constraints>;\n" +
			"rel=\"http://www.w3.org/ns/ldp#constrainedBy\"";
	public static final String VALUE_LDP_CONTENT_TYPE = CONTENT_TYPE_JSONLD_UTF8 + "; profile=\"http://www.w3.org/ns/anno.jsonld\"";
	public static final String VALUE_CONSTRAINTS = "<http://www.w3.org/TR/annotation-protocol/constraints>; " +
			"rel=\"http://www.w3.org/ns/ldp#constrainedBy\"";
	
	
	public static final String VALUE_PREFER_CONTAINEDIRIS = "return=representation;include=\"http://www.w3.org/ns/oa#PreferContainedIRIs\"";
	public static final String VALUE_PREFER_CONTAINEDDESCRIPTIONS = "return=representation;include=\"http://www.w3.org/ns/oa#PreferContainedDescriptions\"";

	/**
	 * Authorization
	 */
	public static final String BEARER = "Bearer";
	
	/**
	 * CORS definitions
	 */
	public static final String ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	public static final String ALLOW_METHODS = "Access-Control-Allow-Methods";
	public static final String ALLOW_HEADERS = "Access-Control-Allow-Headers";
	public static final String EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	public static final String ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	public static final String MAX_AGE = "Access-Control-Max-Age";

	public static final String ALL = "*";
	public static final String MAX_AGE_VALUE = "600";
	
	
}
