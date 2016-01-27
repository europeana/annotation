package eu.europeana.annotation.web.model;

import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.api2.web.model.json.abstracts.ApiResponse;

public class ModerationOperationResponse extends ApiResponse{
	
	ModerationRecord moderationRecord;

	public static String ERROR_NO_OBJECT_FOUND = "No Object Found!";
	public static String ERROR_ID_GENERATION_TYPE_DOES_NOT_MATCH = 
			"Passed 'idGeneration' type parameter does not match to the registered Id generation types given in the data model!";	
	
	public static String ERROR_PROVIDER_EXISTS_IN_DB = 
			"Cannot store object! An object with the given id already exists in the database: ";
	
	public ModerationOperationResponse(String apiKey, String action){
		super(apiKey, action);
	}
	
	public ModerationRecord getModerationRecord() {
		return moderationRecord;
	}

	public void setModerationRecord(ModerationRecord moderationRecord) {
		this.moderationRecord = moderationRecord;
	}

}
