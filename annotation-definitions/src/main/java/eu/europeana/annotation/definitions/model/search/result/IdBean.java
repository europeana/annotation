package eu.europeana.annotation.definitions.model.search.result;

import java.util.Date;

public interface IdBean {
	
//	/**
//	 * The date the record was created
//	 * @return 
//	 */
//	Date getCreatedTimestamp();
	
    String getIdentifierAsUriString();
    
    long getIdentifierAsNumber();
	
    /**
	 * The date the record was updated
	 * @return 
	 */
	Date getLastUpdate();
}
