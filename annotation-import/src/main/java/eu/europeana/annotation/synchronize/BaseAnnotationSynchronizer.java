package eu.europeana.annotation.synchronize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.europeana.annotation.client.AnnotationSearchApiImpl;
import eu.europeana.annotation.client.WebAnnotationAuxilaryMethodsApiImpl;
import eu.europeana.annotation.definitions.model.utils.AnnotationIdHelper;
import eu.europeana.fulltext.api.FulltextAPI;
import eu.europeana.metadata.api.MetadataAPI;

public class BaseAnnotationSynchronizer {

    public static final String PROP_SOLR_METADATA_URL = "solr.metadata.url";
    public static final String PROP_SOLR_FULLTEXT_URL = "solr.fulltext.url";
    public static final String PROP_SOLR_METADATA_COLLECTION = "solr.metadata.collection";
    public static final String PROP_SOLR_FULLTEXT_COLLECTION = "solr.fulltext.collection";
    public static final String PROP_SEARCH_PAGE_SIZE = "annotation.search.pageSize";

    static final Logger LOGGER = LogManager.getLogger(AnnotationSynchronizer.class);
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    Properties appProps;
    
    AnnotationSearchApiImpl annotationSearchApi;
    WebAnnotationAuxilaryMethodsApiImpl annotationAuxilaryMethodsApi;
    MetadataAPI metadataAPI;
    FulltextAPI fulltextAPI;
    int pageSize = 100;
    protected boolean fullImport = false;
    protected boolean incrementalImport = false;
    protected long updateOperations = 0;
    protected long deteleOperations = 0;
    AnnotationIdHelper annotationIdHelper = new AnnotationIdHelper();
    protected Set<String> updatedFulltextRecords = new HashSet<String>();
    protected Set<String> deletedFulltextRecords = new HashSet<String>();

    public static final String PROPERTIES_FILE = "/annotation-import.properties";

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

	annotationSearchApi = new AnnotationSearchApiImpl();

	annotationAuxilaryMethodsApi = new WebAnnotationAuxilaryMethodsApiImpl();

	// initialize metadata api
	String[] metadataURLs = getProperty(PROP_SOLR_METADATA_URL).split(",");
	String metadataCollection = getProperty(PROP_SOLR_METADATA_COLLECTION);
	List<String> metadataURLsList = Arrays.asList(metadataURLs);
	metadataAPI = new MetadataAPI(metadataURLsList, metadataCollection);

	// initialize fulltext api
	String[] fulltextURLs = getProperty(PROP_SOLR_FULLTEXT_URL).split(",");
	String fulltextCollection = getProperty(PROP_SOLR_FULLTEXT_COLLECTION);
	List<String> fulltextURLsList = Arrays.asList(fulltextURLs);
	fulltextAPI = new FulltextAPI(fulltextURLsList, fulltextCollection);

	if(appProps.containsKey(PROP_SEARCH_PAGE_SIZE)) {
	    this.pageSize =  Integer.parseInt(getProperty(PROP_SEARCH_PAGE_SIZE).trim());
	}
    }

    public Properties loadProperties(String propertiesFile)
	    throws URISyntaxException, IOException, FileNotFoundException {
	appProps = new Properties();
	appProps.load(getClass().getResourceAsStream(propertiesFile));
	return appProps;
    }

    public String getProperty(String propertyName) {
	return appProps.getProperty(propertyName);
    }

    protected int getAnnotationSearchPageSize() {
	return pageSize;
    }
    
//    protected void addRecordIdsToSet(List<FulltextDocument> fulltextDocs, Set<String> resourceIdSet) {
//        for (FulltextDocument fulltextDocument : fulltextDocs) {
//            resourceIdSet.add(fulltextDocument.getEuropeana_id());
//        }
//    }

    protected static void logResults(AnnotationSynchronizer importer) {
	LOGGER.info("Update Fulltext Operation: {}", importer.updateOperations);
        LOGGER.info("Delete Fulltext Operations: {}", importer.deteleOperations);
        LOGGER.info("Total Updated FulltextRecords: {}", importer.updatedFulltextRecords.size());
        LOGGER.info("Total Deleted FulltextRecords: {}", importer.deletedFulltextRecords.size());
    }

    protected static void logAndExit(String message, Throwable th) {
        if (th == null) {
            LOGGER.error(message);
        } else {
            LOGGER.error(message, th);
        }
        // jenkins job failure is indicated trough a predefined value of the exit code,
        // we set it too 3
        // (same as for runtime exceptions)
        System.exit(3);
    }

    protected static void logAndExit(String message) {
        logAndExit(message, null);
    }

    protected AnnotationIdHelper getAnnotationIdHelper() {
        return annotationIdHelper;
    }

    public static Date parseDate(String dateString) {
	    SimpleDateFormat format = new SimpleDateFormat(SOLR_DATE_FORMAT);
	    try {
	      return format.parse(dateString);
	    } catch (ParseException e) {
	      String message = "When first argument is: " + IMPORT_DATE
	          + "the second argument must be a date formated as: " + SOLR_DATE_FORMAT;
	      throw new IllegalArgumentException(message, e);
	    }
	  }
}
