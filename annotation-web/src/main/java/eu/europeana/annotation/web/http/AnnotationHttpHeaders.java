package eu.europeana.annotation.web.http;

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
}
