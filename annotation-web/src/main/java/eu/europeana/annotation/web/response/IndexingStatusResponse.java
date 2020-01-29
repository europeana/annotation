package eu.europeana.annotation.web.response;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import eu.europeana.api.commons.web.model.ApiResponse;

/**
 * 
 * @author Sergiu Gordea <sergiu.gordea_at_ait.ac.at>
 *
 */
@JsonSerialize()
@JsonInclude(content=Include.NON_EMPTY, value=Include.NON_EMPTY)
public class IndexingStatusResponse extends ApiResponse {

//	private String collectionId;
//	private int totalObjects;
//	private int indexedObjects;
//	private int skippedObjects;
	private IndexingStatus indexingStatus;
	
	public IndexingStatus getIndexingStatus() {
		return indexingStatus;
	}

	public IndexingStatusResponse(String apikey, String action) {
		super(apikey, action);
	}

	public IndexingStatusResponse() {
		super();
	}
	
//	public String getCollectionId() {
//		return collectionId;
//	}
//	public void setCollectionId(String collectionId) {
//		this.collectionId = collectionId;
//	}
//	public int getTotalObjects() {
//		return totalObjects;
//	}
//	public void setTotalObjects(int totalObjects) {
//		this.totalObjects = totalObjects;
//	}
//	public int getIndexedObjects() {
//		return indexedObjects;
//	}
//	public void setIndexedObjects(int indexedObjects) {
//		this.indexedObjects = indexedObjects;
//	}
//	public int getSkippedObjects() {
//		return skippedObjects;
//	}
//	public void setSkippedObjects(int skippedObjects) {
//		this.skippedObjects = skippedObjects;
//	}
	
	public void setIndexingStatus(IndexingStatus indexingStatus){
		
	}
	
}
