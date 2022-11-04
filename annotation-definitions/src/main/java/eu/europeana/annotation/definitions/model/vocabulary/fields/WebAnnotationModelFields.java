package eu.europeana.annotation.definitions.model.vocabulary.fields;

public interface WebAnnotationModelFields {

	
	/**
	 * Model attribute names
	 */
	//** common fields **/
	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String LANGUAGE = "language";
	public static final String FORMAT = "format";
	public static final String TITLE = "title";
	public static final String VALUE = "value";

	
	//** annotation fields **/
	public static final String AT_CONTEXT = "@context";
	public static final String CREATOR = "creator";
	public static final String CREATED = "created";
	public static final String GENERATED = "generated";
	public static final String GENERATOR = "generator";
	public static final String MOTIVATION = "motivation";
	public static final String STATUS = "status";
	public static final String BODY = "body";
	public static final String BODY_VALUE = "bodyValue";
	public static final String TARGET = "target";
	
	// ** agent fields **/
	public static final String NAME = "name";
	public static final String ACCOUNT = "account";
	public static final String EMAIL = "email";
	public static final String EMAIL_SHA1 = "email_sha1";
	public static final String NICKNAME = "nickname";
	public static final String HOMEPAGE = "homepage";
	
	//** Resource fields **/
	public static final String STYLED_BY = "styledBy";
	public static final String SAME_AS = "sameAs";
	public static final String EQUIVALENT_TO = "equivalentTo";
	public static final String HTTP_URI = "httpUri";
	public static final String SOURCE = "source";
	public static final String SELECTOR = "selector";
	public static final String STYLE_CLASS = "styleClass";
//	public static final String TEXT = "text";
	public static final String PAGE = "page";

	public static final String CANONICAL = "canonical";
	public static final String VIA = "via";
	
	public static final String ITEMS = "items";
	public static final String PURPOSE = "purpose";

	/** Europeana extensions **/
	public static final String GRAPH = "@graph";
	/** geo location **/
	public static final String LATITUDE = "lat";
	public static final String LONGITUDE = "long";
	
	
	/** internal fields */
	public static final String INTERNAL_TYPE = "internalType";
	public static final String LAST_INDEXED = "lastIndexed";
	public static final String LAST_UPDATE = "lastUpdate";
	public static final String ANNOTATION_ID = "annotationId";
	
	/** namespaces */
	public static final String NS_OA = "oa";
	public static final String NS_DC = "dc";
	public static final String NS_DCTERMS = "dcterms";
	public static final String NS_DCTYPES = "dctypes";
	public static final String NS_FOAF = "foaf";
	public static final String NS_RDF = "rdf";
	public static final String NS_RDFS = "rdfs";
	public static final String NS_SKOS = "skos";
	public static final String NS_XSD = "xsd";
	public static final String NS_LDP = "ldp";
	public static final String NS_IANA = "iana";
	public static final String NS_OWL = "owl";
	public static final String NS_AS = "as";
	
	// semantic tag - entity body
	public static final String PREF_LABEL = "prefLabel";
	public static final String DATE_OF_BIRTH = "dateOfBirth";
	public static final String DATE_OF_DEATH = "dateOfDeath";
	public static final String PLACE_OF_BIRTH = "placeOfBirth";
	public static final String PLACE_OF_DEATH = "placeOfDeath";
	public static final String STREET_ADDRESS = "streetAddress";
	public static final String POSTAL_CODE = "postalCode";
	public static final String POST_OFFICE_BOX = "postOfficeBox";
	public static final String LOCALITY = "locality";
	public static final String REGION = "region";
	public static final String COUNTRY_NAME = "countryName";
	
	public static final String COMMA = ",";

}
