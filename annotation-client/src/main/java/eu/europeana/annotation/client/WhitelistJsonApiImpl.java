package eu.europeana.annotation.client;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;

import eu.europeana.annotation.client.config.ClientConfiguration;
import eu.europeana.annotation.client.connection.AnnotationApiConnection;
import eu.europeana.annotation.client.exception.TechnicalRuntimeException;
import eu.europeana.annotation.client.model.result.WhitelistOperationResponse;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;

public class WhitelistJsonApiImpl extends BaseAnnotationApi implements WhitelistJsonApi {

	public WhitelistJsonApiImpl(ClientConfiguration configuration,
			AnnotationApiConnection apiConnection) {
		super(configuration, apiConnection);
	}
	
	public WhitelistJsonApiImpl(){
		super();
	}

	@Override
	public List<? extends WhitelistEntry> loadWhitelistFromResources() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WhitelistEntry storeWhitelistEntry(String whitelistEntryJson) {
		WhitelistOperationResponse res;
		try {
			res = apiConnection.createWhitelistEntry(whitelistEntryJson);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the WhitelistEntryJsonApi", e);
		}

		return res.getWhitelistEntry();
	}

	@Override
	public WhitelistEntry updateWhitelistEntry(WhitelistEntry whitelist) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> deleteWhitelistEntry(String url) {
		ResponseEntity<String> res;
		try {
			res = apiConnection.deleteWhitelistEntry(url);
		} catch (IOException e) {
			throw new TechnicalRuntimeException("Exception occured when invoking the WhitelistEntryJsonApi", e);
		}

		return res;
	}

	@Override
	public WhitelistEntry getWhitelistEntryByUrl(String url) {
		WhitelistOperationResponse res;
		
		try {
			res = apiConnection.getWhitelistEntry(url);
			
			if(!Boolean.valueOf(res.getSuccess()))
				throw new TechnicalRuntimeException(res.getError() + " " + res.getAction());
		
		} catch (IOException e) {
				throw new TechnicalRuntimeException("Exception occured when invoking the WhitelistEntryApi", e);
		}

		return res.getWhitelistEntry();
	}

	@Override
	public WhitelistEntry getWhitelistEntryByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends WhitelistEntry> getWhitelist() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<String> deleteWholeWhitelist() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
