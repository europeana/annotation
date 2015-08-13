package eu.europeana.annotation.web.http;

public interface HttpHeaders extends javax.ws.rs.core.HttpHeaders{

	/**
	 * see {@link <a href="http://www.w3.org/wiki/LinkHeader">W3C Link Header documentation</a>}.
	 * 
	 */
	public static final String LINK = "Link";
	
	public static final String VALUE_LD_RESOURCE = "<http://www.w3.org/ns/ldp#Resource>; rel=\"type\"";
	
	
}
