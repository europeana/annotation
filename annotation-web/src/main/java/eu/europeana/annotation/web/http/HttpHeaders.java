package eu.europeana.annotation.web.http;

public interface HttpHeaders extends javax.ws.rs.core.HttpHeaders{

	/**
	 * see {@link <a href="http://www.w3.org/wiki/LinkHeader">W3C Link Header documentation</a>}.
	 * 
	 */
	public static final String LINK = "Link";
	public static final String ALLOW = "Allow";
	public static final String PREFER = "Prefer";
	
	public static final String ALLOW_POST = "POST";
	public static final String ALLOW_GET = "GET";
	public static final String ALLOW_GPuD = "GET,PUT,DELETE";
	public static final String ALLOW_GOH = "GET,OPTIONS,HEAD";
	
	
	public static final String VALUE_LDP_RESOURCE = "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"";
	public static final String VALUE_LDP_CONTAINER = "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"\n"+
			"<http://www.w3.org/TR/annotation-protocol/constraints>;\n" +
			"rel=\"http://www.w3.org/ns/ldp#constrainedBy\"";
	public static final String VALUE_LDP_CONTENT_TYPE = "application/ld+json; profile=\"http://www.w3.org/ns/anno.jsonld\"";
	
	
}
