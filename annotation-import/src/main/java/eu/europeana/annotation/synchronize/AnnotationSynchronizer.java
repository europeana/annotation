package eu.europeana.annotation.synchronize;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.stanbol.commons.exception.JsonParseException;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.impl.AnnotationDeletion;
import eu.europeana.annotation.definitions.model.search.SearchProfiles;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.fulltext.api.FulltextContent;
import eu.europeana.fulltext.api.FulltextDocument;
import eu.europeana.utils.SolrErrorHandling;

/**
 * This class performs the synchronization of annotations. Namely, it enables
 * updating the fulltext index taking into account the modifications of the
 * annotations and/or their metadata. As first command line argument, use one of
 * {@link #IMPORT_FULL} or {@link #IMPORT_DATE}. If type is {@link #IMPORT_DATE}
 * second argument needs to be a date provided in the {@link #SOLR_DATE_FORMAT} format (e.g.
 * "2019-11-22T10:44:30.620Z").
 * 
 * @author SrdjanStevanetic
 *
 */
public class AnnotationSynchronizer extends BaseAnnotationSynchronizer {

    // a map of id->transcription for the active annotations
    // must preserve order of annotations
    // to be updated when nested documents are available
//    public Map<String, Annotation> transcriptionsMap = new LinkedHashMap<String, Annotation>();
    List<? extends Annotation> transcriptions;
    Set<String> records = new TreeSet<String>();

    public static void main(String[] args) {
	AnnotationSynchronizer importer = new AnnotationSynchronizer();

	Date lastRun = null;
	if (args.length > 0) {
	    switch (args[0]) {
	    case IMPORT_INCREMENTAL:
		lastRun = new Date(0);
		// TODO - derive last update date
		importer.incrementalImport = true;
		break;
	    case IMPORT_FULL:
		lastRun = new Date(0);
		importer.fullImport = true;
		break;
	    case IMPORT_DATE:
		if (args.length == 1) {
		    logAndExit("A date must be provided when import type is: " + IMPORT_DATE);
		}

		lastRun = parseDate(args[1]);
		break;
	    default:
		logAndExit("Invalid import type. Check command line arguments: " + StringUtils.join(args, ' '));
	    }
	} else {
	    logAndExit("The import type is mandatory! Please provide one of the following as command like argument: "
		    + args);
	}

	try {
	    importer.init();
	} catch (Throwable th) {
	    logAndExit("Cannot initialize!", th);
	}

	try {
	    importer.run(lastRun);
	    logResults(importer);
	    LOGGER.info("The import job was completed successfully!");
	} catch (Throwable th) {
	    logResults(importer);
	    LOGGER.error("The import job failed!", th);
	}

    }

    public void run(Date lastRun) throws SolrServerException, IOException, InterruptedException, JsonParseException {

	if (incrementalImport) {
	    lastRun = fulltextAPI.getLastAnnotationUpdate(null);
	    if (lastRun == null) {
		logAndExit(
			"cannot retreeve the last run date from solr, please verify configurations or run a full import");
	    } else {
		LOGGER.info("Last import run:{}", lastRun);
	    }
	}

	int page = 0;
	final int pageSize = getAnnotationSearchPageSize();
	// update transcriptions using pagination
	while (fetchTranscriptions(lastRun, page, pageSize)) {
	    // retrieve active annotation (not deleted ones)
	    // update fulltext index
	    updateFulltextWithTranscriptions();
	    // move to next page and clear transcriptions map
	    page++;
//	    transcriptionsMap.clear();
	    transcriptions.clear();
	}

	// retrieve deleted annotation
	// TODO: implement pagination for deleted annotations
	removeDisabledAnnotations(lastRun);

    }

    protected void removeDisabledAnnotations(Date lastRun)
	    throws SolrServerException, IOException, JsonParseException, InterruptedException {
	List<String> toDelete = new ArrayList<String>();
	updateDeletedAnnotations(lastRun, toDelete, -1, -1);
	deleteDocs(toDelete);
    }

    /**
     * 
     * @param startingDate
     * @param page
     * @param pageSize
     * @return false if no transcriptions are retrieved in in the given page
     */
    public boolean fetchTranscriptions(Date startingDate, int page, int pageSize) {

	// getting active annotations modified after the given date from the
	// AnnotationAPI
//	  String query = "q=*:*&fq=modified:[" + startingDate + " TO NOW]"; //example of the query: "q=*:*&fq=modified:[2019-11-24T16:10:49.624Z TO NOW]";
//	SOLR_DATE_FORMAT
	SimpleDateFormat solrDateFormatter = new SimpleDateFormat(SOLR_DATE_FORMAT);
	String afterDate = solrDateFormatter.format(startingDate);
	String query = WebAnnotationFields.MOTIVATION + ":" + MotivationTypes.TRANSCRIBING.getOaType() + " AND "
		+ WebAnnotationFields.FIELD_MODIFIED + ":[" + afterDate + " TO NOW]";
	AnnotationPage annPg = annotationSearchApi.searchAnnotations(query, null, WebAnnotationFields.FIELD_MODIFIED,
		"asc", String.valueOf(page), String.valueOf(pageSize), SearchProfiles.STANDARD, null);
	List<? extends Annotation> annotations = annPg.getAnnotations();

	if (annotations == null || annotations.size() == 0) {
	    return false;
	}

	int start = page * pageSize;
	LOGGER.debug("Processing annotations set: {}", (start + 1) + "-" + (start + annotations.size()));

//	for (Annotation annotation : annotations) {
//	    String resourceId = annotation.getTarget().getResourceId();
//	    transcriptionsMap.put(resourceId, annotation);
//	}
	transcriptions = annotations;

	return true;
    }

    public void updateFulltextWithTranscriptions() throws SolrServerException, IOException, InterruptedException {
	// updating fulltext documents based on the europeana_ids
	List<FulltextDocument> fulltextDocs = new ArrayList<FulltextDocument>();
	List<String> fulltextDeleteIds = new ArrayList<String>();
	// decide what to update and what to delete from fulltext index
	fillUpdateAndDeleteLists(fulltextDocs, fulltextDeleteIds);
	// update docs in fulltext solr
	updateDocs(fulltextDocs);
	// delete docs in fulltext solr
	deleteDocs(fulltextDeleteIds);
    }

    protected void deleteDocs(List<String> fulltextDeleteIds) throws SolrServerException, IOException {
	if (!fulltextDeleteIds.isEmpty()) {
	    UpdateResponse deleteResponse = fulltextAPI.deleteById(fulltextDeleteIds);
	    UpdateResponse commitResponse = SolrErrorHandling.commit(fulltextAPI.getSolrClient(),
		    fulltextAPI.getSolrCollection());
	    deteleOperations += fulltextDeleteIds.size();
	    if (deleteResponse.getStatus() == 0 && commitResponse.getStatus() == 0) {
		LOGGER.info("Successfully deleted fulltext document!");
		deletedFulltextRecords.addAll(fulltextDeleteIds);
	    } else {
		LOGGER.info("ERROR occured during deleting a fulltext document with the id: ", fulltextDeleteIds);
		throw new RuntimeException("Cannot delete records from tulltext index:" + fulltextDeleteIds);
	    }
	}
    }

    protected void updateDocs(List<FulltextDocument> fulltextDocs)
	    throws SolrServerException, IOException, InterruptedException {

	for (FulltextDocument fulltextDocument : fulltextDocs) {
	    UpdateResponse addResponse = fulltextAPI.set(fulltextDocument, metadataAPI);
	    UpdateResponse commitResponse = SolrErrorHandling.commit(fulltextAPI.getSolrClient(),
		    fulltextAPI.getSolrCollection());
	    if (addResponse.getStatus() == 0 && commitResponse.getStatus() == 0) {
		LOGGER.info("Updating fulltext documents with active annotations was successfull!");
		updateOperations++;
//		addRecordIdsToSet(fulltextDocs, updatedFulltextRecords);
		records.add(fulltextDocument.getEuropeana_id());
	    } else {
		LOGGER.info("ERROR occured during updating fulltext document with annotations!", fulltextDocument);
		//
		throw new RuntimeException("cannot submit records to solr index for fulltext doc:" + fulltextDocument);
	    }
	}
    }

    protected void fillUpdateAndDeleteLists(List<FulltextDocument> fulltextDocs, List<String> fulltextDeleteIds)
	    throws SolrServerException, IOException {
	
//	Annotation annotation;
	//to be revisited when nested documents for resources is implemented
//	for (String europeana_id : transcriptionsMap.keySet()) {
//	    // ensuring that there is a document in the metadata
//	    if (metadataAPI.get(europeana_id) == null) {
//		// delete the document from the fulltext API
//		fulltextDeleteIds.add(europeana_id);
//	    } else {
//		// TODO: clarify the language aspect and use TranscriptionLanguage
//		// even if exists it must be overwritten with the new value
////		  String existingTranscription = transcriptionsMap.get(key);
////		  if(existingTranscription==null) {
//		annotation = transcriptionsMap.get(europeana_id);
//		String transcription = annotation.getBody().getValue();
//		String language = annotation.getBody().getLanguage();
//		if(language == null)
//		    language = "";
//		
//		Date updated = annotation.getGenerated();
//		
//		if (StringUtils.isNotBlank(transcription)) {
//			FulltextContent fulltextContent = new FulltextContent(transcription, language);
//			fulltextDocs.add(new FulltextDocument(europeana_id, Arrays.asList(fulltextContent), updated));   
//		} else {
//		    LOGGER.info("No textual transcription available, the annotation is not added to fulltext: {0}", annotation.getAnnotationId());
//		}
//	    }
//	}
	String europeana_id, transcription, language;
	for (Annotation annotation : transcriptions) {
	    
	    europeana_id = annotation.getTarget().getResourceId();
	    //TODO preserve metadata document to reduce nr of solr requests, or implement exists
	    if (metadataAPI.get(europeana_id) == null) {
		// delete the document from the fulltext API
		fulltextDeleteIds.add(europeana_id);
	    } else {
		transcription = annotation.getBody().getValue();
		language = annotation.getBody().getLanguage();
		if(language == null)
		    language = "";
		
		Date updated = annotation.getGenerated();
		
		if (StringUtils.isNotBlank(transcription)) {
			FulltextContent fulltextContent = new FulltextContent(transcription, language);
			fulltextDocs.add(new FulltextDocument(europeana_id, Arrays.asList(fulltextContent), updated));   
		} else {
		    LOGGER.info("No textual transcription available, the annotation is not added to fulltext: {0}", annotation.getAnnotationId());
		}
	    } 
	}
    }

    public void updateDeletedAnnotations(Date startingDate, List<String> toDelete, int page, int pageSize)
	    throws SolrServerException, IOException, JsonParseException, InterruptedException {
	// getting deleted annotations modified after the given date from the
	// AnnotationAPI
	List<AnnotationDeletion> disabledResources = mongoPersistance.getDeletedByLastUpdateTimestamp(MotivationTypes.TRANSCRIBING.getOaType(), startingDate.getTime());
	if (disabledResources == null || disabledResources.isEmpty()) {
	    LOGGER.debug("No disabled resources to process!");
	    return;
	}
	Date lastFulltextUpdate, deletionDate;
	for (AnnotationDeletion annotationDeletion : disabledResources) {
	    if (annotationDeletion.getResourceId() == null) {
		LOGGER.info("annotation doesn't have a resource id {}", annotationDeletion.getAnnotaionId());
		continue;
	    }
	    lastFulltextUpdate = fulltextAPI.getLastAnnotationUpdate(annotationDeletion.getResourceId());
	    deletionDate = new Date(annotationDeletion.getTimestamp());
//	    if (lastFulltextUpdate == null || lastFulltextUpdate.after(deletionDate))
	    if (lastFulltextUpdate == null) {
		// nothing to do, record doesn't exists in the fulltext or transcription was
		// already updated by another annotation
		LOGGER.info(
			"Fulltext record {} doesn't exist for annotation {}  or the record was updated with a later timestamp than the curent one {}: {}",
			annotationDeletion.getResourceId(), annotationDeletion.getAnnotaionId(), deletionDate,
			lastFulltextUpdate);
	    } else {
		toDelete.add(annotationDeletion.getResourceId());
	    }
	}
    }

}
