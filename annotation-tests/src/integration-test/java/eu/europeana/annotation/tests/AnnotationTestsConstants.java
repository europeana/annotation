package eu.europeana.annotation.tests;

import java.io.IOException;

import eu.europeana.annotation.definitions.model.vocabulary.StatusTypes;
import eu.europeana.annotation.definitions.model.vocabulary.fields.WebAnnotationModelKeywords;
import eu.europeana.annotation.tests.config.AnnotationTestsConfiguration;

public class AnnotationTestsConstants {
  public static final String DUMMY_PARAM = "dummy";
  public static final String URI_VERMEER_VIAF = "http://viaf.org/viaf/51961439";
  public static final String AGENT_VERMEER_VIAF_XML = "/metis-deref/agent_jan_vermeer_viaf.xml";
  public static final String DEREFERENCE_MANY = "metis-deref_many";
  public static final String DEREFERENCE_MANY_XML = "/metis-deref/dereference_many.xml";
  public static final String EMPTY_METIS_RESPONSE = "/metis-deref/empty_metis_response.xml";
  
  public static final String EMPTY_SEARCH_API_RESPONSE = "/search-api/empty_api_response.json";
  
  public static final String TEST_REPORT_SUMMARY_FIELD = "reportSum";
  
  public static final String USER_REGULAR = "regular";
  public static final String USER_ADMIN = "admin";
  public static final String USER_PUBLISHER = "publisher";
  public static final String USER_PROVIDER_SAZ = "provider_saz";

  public static final String TEST_STATUS = StatusTypes.PRIVATE.name().toLowerCase();

  public static final String LINK_MINIMAL = "/link/minimal.json";
  public static final String LINK_STANDARD = "/link/standard.json";
  public static final String TAG_STANDARD = "/tag/standard.json";
  public static final String TAG_MINIMAL = "/tag/minimal.json";
  public static final String TAG_STANDARD_TEST_VALUE = "/tag/standard_test_value.json";
  public static final String TAG_STANDARD_TEST_VALUE_BODY = "test";

  public String get_TAG_STANDARD_TEST_VALUE_TARGET(String itemDataEndpoint) {
    return itemDataEndpoint + "/09102/_UEDIN_214";
  }
  //  public static final String FULL_TEXT_RESOURCE = "/tag/full_text_resource.json";
  public static final String TAG_ANNOTATION = "/tag/annotation.json";
  public static final String WHITELIST_ENTRY = "/whitelist/entry.json";
  
  public static final String VALUE_TESTSET = "generator_uri: \"http://test.europeana.org/45e86248-1218-41fc-9643-689d30dbe651\"";
  public static final String VALUE_ID = "anno_id:";
      
  public static final String VALUE_SEARCH_DESCRIBING_TARGET_SOURCE = "target_uri:\"http://www.europeana1914-1918.eu/attachments/2020601/20841.235882.full.jpg\"";
  
  public static final String VALUE_TAG_BODY_URI = "http://www.geonames.org/2988507";
  public static final String VALUE_SEARCH_TAG_BODY_URI = "body_uri:\"" +VALUE_TAG_BODY_URI+ "\"";
  
  public static final String VALUE_DESCRIBING_BODY_VALUE = "body_value:\"the textual description of the item\"";
  public static final String VALUE_SEARCH_DESCRIBING_BODY_VALUE = "body_value:\"textual description\"";
  
  public static final String VALUE_TAG_BODY_VALUE = "trombone";
  public static final String VALUE_SEARCH_TAG_BODY_VALUE = "body_value:\""+VALUE_TAG_BODY_VALUE+"\"";
  
  public static final String VALUE_BODY_LINK_RELATION = "isSimilarTo";
  public static final String VALUE_SEARCH_BODY_LINK_RELATION = "link_relation:\"" +VALUE_BODY_LINK_RELATION+ "\"";
  
  public static final String VALUE_BODY_LINK_RESOURCE_URI = "https://www.wikidata.org/wiki/Q762";
  public static final String VALUE_SEARCH_BODY_LINK_RESOURCE_URI = "link_resource_uri:\""+VALUE_BODY_LINK_RESOURCE_URI+"\"";
  
  
  public static final String VALUE_BODY_SPECIFIC_RESOURCE = "http://www.geonames.org/2988507"; // source
  public static final String VALUE_SEARCH_BODY_SPECIFIC_RESOURCE = "body_uri:\""+VALUE_BODY_SPECIFIC_RESOURCE+"\""; // source
  
  public static final String VALUE_BODY_FULL_TEXT_RESOURCE = "... complete transcribed text in HTML ...";
  public static final String VALUE_SEARCH_BODY_FULL_TEXT_RESOURCE = "body_value:\"transcribed text in HTML\"";
  public static final String VALUE_SEARCH_BODY_VALUE_IT = "body_value.it:(con il grande finale)";

  public static final String TAG_BODY_TEXT = "/tag/bodyText.json";
  public static final String TAG_MINIMAL_WRONG = "/tag/wrong/minimal_wrong.json";
  public static final String TAG_WITHOUT_BODY = "/tag/wrong/without_body.json";
  public static final String TAG_GEO_WRONG_LAT = "/tag/wrong/geotag_wrong_lat.json";
  public static final String TAG_GEO_WRONG_LONG = "/tag/wrong/geotag_wrong_long.json";
  public static final String TAG_GEOTAG = "/tag/geotag.json";
  public static final String LINK_SEMANTIC = "/link/edmIsSimilarTo.json";
  public static final String SEMANTICTAG_SIMPLE_MINIMAL = "/semantictag/simple_minimal.json";
  public static final String SEMANTICTAG_SIMPLE_STANDARD = "/semantictag/simple_standard.json";
  public static final String SEMANTICTAG_SPECIFIC_MINIMAL = "/semantictag/specific_minimal.json";
  public static final String SEMANTICTAG_SPECIFIC_STANDARD = "/semantictag/specific_standard.json";
  public static final String SEMANTICTAG_WEB_RESOURCE = "/semantictag/web_resource.json";
  public static final String SEMANTICTAG_ENTITY = "/semantictag/semantictag_entity.json";
  public static final String SEMANTICTAG_AGENT_ENTITY = "/semantictag/semantictag_agent_entity.json";
  public static final String SEMANTICTAG_VCARD_ADDRESS = "/semantictag/vcard_address.json";
  public static final String TAG_CANONICAL = "/tag/canonical.json";
  public static final String TAG_VIA_STRING = "/tag/via_string.json";
  public static final String TAG_VIA_ARRAY = "/tag/via_array.json";
  public static final String DEREFERENCED_SEMANTICTAG_MOZART_ENTITY = "/semantictag/dereferenced_semantictag_mozart_entity.json";
  public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY = "/semantictag/dereferenced_semantictag_viaf_test_entity.json";
  public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_2 = "/semantictag/dereferenced_semantictag_viaf_test_entity2.json";
  public static final String DEREFERENCED_SEMANTICTAG_TEST_ENTITY_3 = "/semantictag/dereferenced_semantictag_viaf_test_entity3.json";
  public static final String DESCRIBING_WEB_RESOURCE = "/describing/web_resource.json";

  public static final String DESCRIBING_WITHOUT_BODY_LANGUAGE = "/describing/web_resource_without_body_language.json";
  public static final String TAGGING_BODY_ADDRESS_NO_TYPE = "/tag/wrong/body_address_no_type.json";
  public static final String TAGGING_BODY_ADDRESS_NO_STREET_ADDRESS = "/tag/wrong/body_address_no_street_address.json";
  
  // FULLTEXT 
  public static final String TRANSCRIPTION_WITH_RIGHTS = "/transcription/transcription-with-rights.json";
  public static final String TRANSCRIPTION_COPYRIGHT = "/transcription/transcription-coypright.json";
  public static final String TRANSCRIPTION_MINIMAL_DUPLICATE_UPDATE = "/transcription/minimal-duplicate-update.json";
  public static final String TRANSCRIPTION_MINIMAL = "/transcription/minimal.json";

  public static final String TRANSLATION_MINIMAL = "/translation/minimal.json";
  
  public static final String SUBTITLE_MINIMAL = "/subtitle/minimal.json";
  public static final String SUBTITLE_WITH_COYRIGHT= "/subtitle/subtitle-copyright.json";
  public static final String CAPTION_MINIMAL = "/caption/minimal.json";
  public static final String CAPTION_MINIMAL_EN = "/caption/minimal-en.json";
  public static final String CAPTION_WITH_COPYRIGHT = "/caption/caption-copyright.json";
  
  public static final String LINK_FOR_CONTRIBUTING_BODY_OBJECT = "/linkforcontributing/link_for_contributing_body_object.json";
  public static final String LINK_FOR_CONTRIBUTING_BODY_STRING = "/linkforcontributing/link_for_contributing_body_string.json";
  public static final String LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC = "/linkforcontributing/link_for_contributing_target_specific.json";
  public static final String LINK_FOR_CONTRIBUTING_TARGET_SPECIFIC_ID = "/linkforcontributing/link_for_contributing_target_specific_id.json";
  
  
  
  public static final String START = "{";
  public static final String END = "}";
  public static final String TYPE = "\"@context\": \"" + WebAnnotationModelKeywords.WA_CONTEXT + "\","
          + "\"type\": \"oa:Annotation\",";

  //TODO: migrate old test cases to use annotations from test files
  public static final String ANNOTATED_SERIALIZED = "\"creator\": {" + "\"id\": \"https://www.historypin.org/en/person/55376/\","
          + "\"type\": \"Person\"," + "\"name\": \"John Smith\"" + "},"
          + "\"created\": \"2015-02-27T12:00:43Z\"," + "\"generated\": \"2015-02-28T13:00:34Z\","
          + "\"generator\": \"http://www.historypin.org\",";

  public static final String EQUIVALENT_TO = "\"oa:equivalentTo\": \"https://www.historypin.org/en/item/456\",";

  public static final String BODY_VALUE_TO_TRIM = " überhaupt ";
  public static final String BODY_VALUE_AFTER_TRIMMING = "überhaupt";

  public String get_TAG_JSON_BY_TYPE_JSONLD(String itemDataEndpoint) {
    String TAG_CORE = TYPE + ANNOTATED_SERIALIZED + "\"bodyValue\": \"church\","
        + "\"target\": \"" + itemDataEndpoint + "/123/xyz\"," + EQUIVALENT_TO;
    return START + TAG_CORE + END;
  }
  
  public String get_TAG_JSON_VALIDATION(String itemDataEndpoint) {
     String TAG_CORE_VALIDATION = TYPE + ANNOTATED_SERIALIZED + 
         "\"motivation\": \"oa:tagging\"," +
         "\"bodyValue\": \"" + BODY_VALUE_TO_TRIM + "\"," +
         "\"target\": \"" + itemDataEndpoint + "/123/xyz\"," + EQUIVALENT_TO;
    return START + TAG_CORE_VALIDATION + END;
  }

  public String get_LINK_CORE(String itemDataEndpoint) {
     String LINK_CORE = TYPE + ANNOTATED_SERIALIZED + "\"target\": ["
         + "\"" + itemDataEndpoint +  "/123/xyz.html\","
         + "\"" + itemDataEndpoint +  "/333/xyz.html\"" + "]," + EQUIVALENT_TO;
    return LINK_CORE;
  }

  public String get_LINK_JSON_BY_TYPE_JSONLD(String itemDataEndpoint) {
     String LINK_CORE = TYPE + ANNOTATED_SERIALIZED + "\"target\": ["
         + "\"" + itemDataEndpoint +  "/123/xyz.html\","
         + "\"" + itemDataEndpoint +  "/333/xyz.html\"" + "]," + EQUIVALENT_TO;
    return START + LINK_CORE + END;
  }
  
  public String get_LINK_JSON_WITHOUT_BLANK() throws IOException {
    return START + get_LINK_CORE(AnnotationTestsConfiguration.getInstance().getPropAnnotationItemDataEndpoint()) + "\"motivation\":\"oa:linking\"" +  END;
  }
  
}