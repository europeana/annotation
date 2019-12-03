package eu.europeana.annotation.synchronize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import europeana.fulltext.api.FulltextContent;
import europeana.fulltext.api.FulltextDocument;
import europeana.utils.SolrErrorHandling;

/**
 * This class performs the synchronization of annotations. Namely, it enables updating the fulltext index 
 * taking into account the modifications of the annotations and/or their metadata.
 * As first command line argument, use one of {@link #IMPORT_FULL} or {@link #IMPORT_DATE}.
 * If type is {@link #IMPORT_DATE} second argument needs to be a date provided in 
 * the ISO 8601 format (e.g. "2019-11-22T10:44:30.620Z").
 * 
 * @author SrdjanStevanetic
 *
 */
public class AnnotationSynchronizer extends BaseAnnotationSynchronizer {

  boolean fullImport = false;
  
  //a map of id->transcription for the active annotations
  public static Map<String,String> map_id_annos_active = new HashMap<String, String>();
  //a list of disabled annotations
  public static List<String> annos_disabled = new ArrayList<String>();

  public static void main(String[] args) {
    AnnotationSynchronizer importer = new AnnotationSynchronizer();

    String lastRun = null;
    if (args.length > 0) {
      switch (args[0]) {
        case IMPORT_FULL:
          lastRun = "1970-01-01T00:00:00Z";
          importer.fullImport = true;
          break;
        case IMPORT_DATE:
          if (args.length == 1) {
            logAndExit("A date must be provided when import type is: " + IMPORT_DATE);
          }

          lastRun = args[1];
          break;
        default:
          logAndExit(
              "Invalid import type. Check command line arguments: " + StringUtils.join(args, ' '));
      }
    } else {
      logAndExit(
          "The import type is mandatory! Please provide one of the following as command like argument: "
              + args);
    }


    try {
      importer.init();
    } catch (Throwable th) {
      logAndExit("Cannot initialize!", th);
    }

    try {
      importer.run(lastRun);
    } catch (Throwable th) {
      logAndExit("The import job failed!", th);
    }
  }

  private static void logAndExit(String message, Throwable th) {
    if(th == null){
      LOGGER.error(message);
    } else {
      LOGGER.error(message, th);
    }
    // jenkins job failure is indicated trough a predefined value of the exit code, we set it too 3
    // (same as for runtime exceptions)
    System.exit(3);
  }

  private static void logAndExit(String message) {
    logAndExit(message, null);
  }
  
  public void run(String lastRun) throws SolrServerException, IOException, InterruptedException {
    
	int page = 0;
    final int pageSize = 100;
    boolean hasNext = true;
    int numberFetchedAnnos = 0;
    int activeOrDeleted=1;
   
    while (hasNext) {
    	if(activeOrDeleted==1)
    	{
    		//retrieve active annotation (not deleted ones)
    		numberFetchedAnnos = updateActiveAnnotations(lastRun, page, pageSize);
    	}
    	else
    	{
    		//retrieve deleted annotation 
    		numberFetchedAnnos = updateDeletedAnnotations(lastRun, page, pageSize);
    	}

        if (numberFetchedAnnos < pageSize) {
          // last page: if no more annotations exist
          if(activeOrDeleted==1) 	
          {
        	  activeOrDeleted = 2;
        	  page = 0;
        	  hasNext = true;
          }
          else hasNext = false;
          
        } else {
          // go to next page
          page += 1;
        }
      }
    
    updateFulltextIndexWithActiveAnnotations();
    updateFulltextIndexWithDisabledAnnotations();
    
    map_id_annos_active.clear();
    annos_disabled.clear();    

  }
  
  public int updateActiveAnnotations (String startingDate, int page, int pageSize) {
	  
	  //getting active annotations modified after the given date from the AnnotationAPI
	  String query = "q=*:*&fq=modified:[" + startingDate + " TO NOW]"; //example of the query: "q=*:*&fq=modified:[2019-11-24T16:10:49.624Z TO NOW]";
	  AnnotationPage annPg = annotationSearch.searchAnnotations(query, Integer.toString(page), Integer.toString(pageSize), null, null);
	  List<? extends Annotation> annos = annPg.getAnnotations();
	  
	  if(annos==null) 
	  {
		  return 0;
		  //throw new IllegalArgumentException("Retrieved annotations set should not be null!");
	  }
	  
	  LOGGER.info("Processing annotations set: {}", page+1+"-"+(page+annos.size()));
	  
	  for(int i=0;i<annos.size();i++)
	  {
		  String key = annos.get(i).getTarget().getResourceId();
		  String existingTranscriptionString = map_id_annos_active.get(key);
		  if(existingTranscriptionString==null) existingTranscriptionString = annos.get(i).getBody().getValue();
		  else existingTranscriptionString += annos.get(i).getBody().getValue();		  
		  map_id_annos_active.put(key, existingTranscriptionString);
	  }	  

	  return annos.size();
  }
  
  public void updateFulltextIndexWithActiveAnnotations () throws SolrServerException, IOException, InterruptedException
  {
	  //updating fulltext documents based on the europeana_ids
	  List<FulltextDocument> ftDocs = new ArrayList<FulltextDocument>();
	  for (String europeana_id: map_id_annos_active.keySet()) {
		  
		  //ensuring that there is a document in the metadata
		  if(mAPI.get(europeana_id)!=null)
		  {
			  FulltextContent ftCont = new FulltextContent(map_id_annos_active.get(europeana_id), "en");
			  ftDocs.add(new FulltextDocument(europeana_id, Arrays.asList(ftCont)));
		  }
		  else
		  {
			  //delete the document from the fulltext API
			  UpdateResponse deleteResponse = ftAPI.deleteById(europeana_id);
			  SolrErrorHandling.commit(ftAPI.getSolrClient(), ftAPI.getSolrCollection());
			  if(deleteResponse.getStatus()==0) LOGGER.info("Successfully deleted fulltext document!");
			  else LOGGER.info("ERROR occured during deleting a fulltext document with the id: ", europeana_id);

		  }
	  }
	  
	  if(!ftDocs.isEmpty())
	  {
		  UpdateResponse addResponse = ftAPI.set(ftDocs, mAPI);
		  SolrErrorHandling.commit(ftAPI.getSolrClient(), ftAPI.getSolrCollection());
		  
		  if(addResponse.getStatus()==0) LOGGER.info("Updating fulltext documents with active annotations was successfull!");
		  else LOGGER.info("ERROR occured during updating fulltext documents with active annotations!");
	  }
  }
  public int updateDeletedAnnotations (String startingDate, int page, int pageSize) throws SolrServerException, IOException {
	  //getting deleted annotations modified after the given date from the AnnotationAPI
	  String query = "disabled=true&lastUpdate=" + startingDate; //example of the query: "disabled=true&lastUpdate=2019-11-24T16:10:49.624Z";
	  AnnotationPage annPg = annotationSearch.searchAnnotations(query, Integer.toString(page), Integer.toString(pageSize), null, null);
	  List<? extends Annotation> annos = annPg.getAnnotations();
	  
	  if(annos==null) 
	  {
		  return 0;
		  //throw new IllegalArgumentException("Retrieved annotations set should not be null!");
	  }
	  	 
	  LOGGER.info("Processing annotations set: {}", page+1+"-"+(page+annos.size()));

	  for(int i=0;i<annos.size();i++)
	  {
		  String europeana_id = annos.get(i).getTarget().getResourceId();
		  if(!annos_disabled.contains(europeana_id)) annos_disabled.add(europeana_id);
	  }
	  
	  return annos.size();

  }
  public void updateFulltextIndexWithDisabledAnnotations () throws SolrServerException, IOException 
  {
	  for(int i=0;i<annos_disabled.size();i++)
	  {
		  //delete the document from the fulltext API
		  UpdateResponse deleteResponse = ftAPI.deleteById(annos_disabled.get(i));
		  SolrErrorHandling.commit(ftAPI.getSolrClient(), ftAPI.getSolrCollection());
		  if(deleteResponse.getStatus()==0) LOGGER.info("Successfully deleted fulltext document!");
		  else LOGGER.info("ERROR occured during deleting a fulltext document with the id: ", annos_disabled.get(i));
	  }
  }

}
