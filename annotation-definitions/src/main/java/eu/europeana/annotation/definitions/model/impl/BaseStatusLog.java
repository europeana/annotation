package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.StatusLog;


public class BaseStatusLog implements StatusLog {

	private String user;
	private String status;
	private long date;
	private long identifier;

	public BaseStatusLog(){
		super();
	}
	

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof StatusLog)) {
	        return false;
	    }

	    StatusLog that = (StatusLog) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getUser() != null) && (that.getUser() != null) &&
	    		(!this.getUser().equals(that.getUser()))) {
	    	res = false;
	    }
	    
	    if ((this.getStatus() != null) && (that.getStatus() != null) &&
	    		(!this.getStatus().equals(that.getStatus()))) {
	    	res = false;
	    }
	    
	    if ((this.getDate() > 0) && (that.getDate() > 0) &&
	    		(this.getDate() != that.getDate())) {
	    	res = false;
	    }
	    
	    if (this.getIdentifier() != that.getIdentifier()) {
	    	res = false;
	    }
	    
	    return res;
	}
	
	@Override
	public String toString() {
		String res = "### StatusLog ###\n";
		if (user != null) 
			res = res + "\t" + "user:" + user + "\n";
		if (status != null) 
			res = res + "\t" + "status:" + status + "\n";
		if (date > 0) 
			res = res + "\t" + "date:" + date + "\n";
		if (identifier != 0) 
			res = res + "\t" + "identifier:" + identifier + "\n";
		return res;
	}


	@Override
	public void setUser(String user) {
		this.user = user;
	}


	@Override
	public String getUser() {
		return user;
	}


	@Override
	public void setStatus(String status) {
		this.status = status;
	}


	@Override
	public String getStatus() {
		return status;
	}


	@Override
	public void setDate(long date) {
		this.date = date;
	}


	@Override
	public long getDate() {
		return date;
	}
	
    public long getIdentifier() {
      return identifier;
    }

    public void setIdentifier(long identifier) {
      this.identifier = identifier;
    }
}
