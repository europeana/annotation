package eu.europeana.annotation.synchronize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import europeana.fulltext.api.FulltextAPI;
import europeana.metadata.api.MetadataAPI;

public class BaseAnnotationSynchronizer {

  public static final String PROP_SOLR_METADATA_URL = "solr.metadata.url";
  public static final String PROP_SOLR_FULLTEXT_URL = "solr.fulltext.url";
  public static final String PROP_SOLR_METADATA_COLLECTION = "solr.metadata.collection";
  public static final String PROP_SOLR_FULLTEXT_COLLECTION = "solr.fulltext.collection";

  static final Logger LOGGER = LoggerFactory.getLogger(AnnotationSynchronizer.class);

  Properties appProps;
  AnnotationSearchApiImpl annotationSearch;
  MetadataAPI mAPI;
  FulltextAPI ftAPI;

  public static final String PROPERTIES_FILE = "/solr.properties";
  
  public static final String IMPORT_FULL = "full";
  public static final String IMPORT_INCREMENTAL = "incremental";
  public static final String IMPORT_DATE = "date";
  public static final String IMPORT_INDIVIDUAL = "individual";
 


  public void init() throws Exception {
    // read properties
    loadProperties(PROPERTIES_FILE);
    String solrMetadataURLs = getProperty(PROP_SOLR_METADATA_URL);
    String solrFulltextURLs = getProperty(PROP_SOLR_FULLTEXT_URL);
  
    String solrMetadataCollection = getProperty(PROP_SOLR_METADATA_COLLECTION);
    String solrFulltextCollection = getProperty(PROP_SOLR_FULLTEXT_COLLECTION);

    LOGGER.info("Solr metadata URLs: " + solrMetadataURLs);
    LOGGER.info("Solr fulltext URLs: " + solrFulltextURLs);
    LOGGER.info("Solr metadata collection: " + solrMetadataCollection);
    LOGGER.info("Solr fulltext collection: " + solrFulltextCollection);
    
    if (solrMetadataURLs == null || solrMetadataURLs.isEmpty())
    	throw new IllegalArgumentException(PROP_SOLR_METADATA_URL + " should not be null or empty!");
    if (solrFulltextURLs == null || solrFulltextURLs.isEmpty())
    	throw new IllegalArgumentException(PROP_SOLR_FULLTEXT_URL + " should not be null or empty!");
    if (solrMetadataCollection == null || solrMetadataCollection.isEmpty())
    	throw new IllegalArgumentException(PROP_SOLR_METADATA_COLLECTION + " should not be null or empty!");
    if (solrFulltextCollection == null || solrFulltextCollection.isEmpty())
    	throw new IllegalArgumentException(PROP_SOLR_FULLTEXT_COLLECTION + " should not be null or empty!");

    //initialize annotation search 
    annotationSearch = new AnnotationSearchApiImpl();
    
    //initialize metadata api
	String[] mtURLs = getProperty(PROP_SOLR_METADATA_URL).split(",");
	String mtCollection = getProperty(PROP_SOLR_METADATA_COLLECTION);
	List<String> mtURLsList = Arrays.asList(mtURLs);
	mAPI = new MetadataAPI(mtURLsList, mtCollection);

	//initialize fulltext api
	String[] ftURLs = getProperty(PROP_SOLR_FULLTEXT_URL).split(",");
	String ftCollection = getProperty(PROP_SOLR_FULLTEXT_COLLECTION);
	List<String> ftURLsList = Arrays.asList(ftURLs);
	ftAPI = new FulltextAPI(ftURLsList, ftCollection);
    
  }

  public Properties loadProperties(String propertiesFile)
      throws URISyntaxException, IOException, FileNotFoundException {
    appProps = new Properties();
    appProps.load( getClass().getResourceAsStream(propertiesFile));
    return appProps;
  }


  
  public String getProperty(String propertyName){
    return appProps.getProperty(propertyName);
  }
}
