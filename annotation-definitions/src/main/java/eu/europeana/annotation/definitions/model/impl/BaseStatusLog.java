package eu.europeana.annotation.definitions.model.impl;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.StatusLog;


public class BaseStatusLog implements StatusLog {

	private String user;
	private String status;
	private long date;
	private AnnotationId annotationId;

	
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
	    	System.out.println("StatusLog objects have different 'user' fields.");
	    	res = false;
	    }
	    
	    if ((this.getStatus() != null) && (that.getStatus() != null) &&
	    		(!this.getStatus().equals(that.getStatus()))) {
	    	System.out.println("StatusLog objects have different 'status' fields.");
	    	res = false;
	    }
	    
	    if ((this.getDate() > 0) && (that.getDate() > 0) &&
	    		(this.getDate() != that.getDate())) {
	    	System.out.println("StatusLog objects have different 'date' fields.");
	    	res = false;
	    }
	    
	    if ((this.getAnnotationId() != null) && (that.getAnnotationId() != null) &&
	    		(!this.getAnnotationId().equals(that.getAnnotationId()))) {
	    	System.out.println("StatusLog objects have different 'AnnotationId' fields.");
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
		if (annotationId != null) 
			res = res + "\t" + "annotationId:" + annotationId.toString() + "\n";
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


	@Override
	public void setAnnotationId(AnnotationId annotationId) {
		this.annotationId = annotationId;
	}


	@Override
	public AnnotationId getAnnotationId() {
		return annotationId;
	}	
	
}
