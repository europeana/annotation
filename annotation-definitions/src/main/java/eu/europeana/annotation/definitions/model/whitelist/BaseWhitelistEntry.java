package eu.europeana.annotation.definitions.model.whitelist;

import java.util.Date;

import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;

/**
 * This is a class for Whitelist object.
 * The sample Whitelist entity JSON presentation is:
 * 
 * 		{
 * 			"id": "1",
 * 			"name": "data.europeana",
 * 			"httpUrl": "http://data.europeana.eu",
 * 			"creationDate": "23.11.2015",
 * 			"lastUpdate": "23.11.2015",
 * 			"status": "enabled",
 * 			"enableFrom": "23.11.2015",
 * 			"disableTo": "23.11.2016"
 *		} 	
 *
 */
public class BaseWhitelistEntry implements WhitelistEntry {

	private int id;
	private String name;
	private String httpUrl;
	private String status;
	private Date creationDate;
	private Date lastUpdate;
	private Date enableFrom;
	private Date disableTo;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(Date lastUpdateTimestamp) {
		this.lastUpdate = lastUpdateTimestamp;
	}

	public Date getCreationDate() {
		return this.creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEnableFrom() {
		return this.enableFrom;
	}

	public void setEnableFrom(Date enableFromTimestamp) {
		this.enableFrom = enableFromTimestamp;
	}

	public Date getDisableTo() {
		return this.disableTo;
	}

	public void setDisableTo(Date disableToTimestamp) {
		this.disableTo = disableToTimestamp;
	}

	public boolean equals(Object other) {
	    if (!(other instanceof WhitelistEntry)) {
	        return false;
	    }

	    WhitelistEntry that = (WhitelistEntry) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getHttpUrl() != null) && (that.getHttpUrl() != null) &&
	    		(!this.getHttpUrl().toString().equals(that.getHttpUrl().toString()))) {
	    	System.out.println("Concept objects have different 'HttpUrl' fields.");
	    	res = false;
	    }
	    
	    if ((this.getName() != null) && (that.getName() != null) &&
	    		(!this.getName().toString().equals(that.getName().toString()))) {
	    	System.out.println("Concept objects have different 'Name' fields.");
	    	res = false;
	    }
	    
	    if ((this.getStatus() != null) && (that.getStatus() != null) &&
	    		(!this.getStatus().toString().equals(that.getStatus().toString()))) {
	    	System.out.println("Concept objects have different 'Status' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	@Override
	public String toString() {
		String res = "\t### Whitelist ###\n";
		
		if (getHttpUrl() != null) 
			res = res + "\t\t" + WebAnnotationFields.HTTP_URI + ":" + getHttpUrl() + "\n";
		if (getName() != null) 
			res = res + "\t\t" + WebAnnotationFields.NAME + ":" + getName() + "\n";
		if (getStatus() != null) 
			res = res + "\t\t" + WebAnnotationFields.STATUS + ":" + getStatus() + "\n";
		return res;
	}	
}
