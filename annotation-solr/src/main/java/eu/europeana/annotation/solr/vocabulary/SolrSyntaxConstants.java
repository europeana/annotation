package eu.europeana.annotation.solr.vocabulary;

public interface SolrSyntaxConstants {
	
	/**
	 * Search fields for Web Service
	 */
	public static final String ALL = "all";
	
	public static final String ALL_SOLR_ENTRIES = "*:*";
	public static final String DELIMETER = ":";
	
	/**
	 * Logger name
	 */
	public static final String ROOT = "root";

	/**
	 * Solr query
	 */
	public static final String STAR = "*";
	public static final String AND = "&";
	public static final String START = "start";
	public static final String ROWS = "rows";
	public static final String EQUALS = "=";
	
	/**
	 * Facets
	 */
	/**
	 * Number of milliseconds before the query is aborted by SOLR
	 */
	public static final int TIME_ALLOWED = 30000;
}
