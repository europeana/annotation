package eu.europeana.annotation.definitions.model.moderation.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.Vote;


/**
 * This class implements specification of the moderation policy.
 */
public class BaseModerationRecord implements ModerationRecord {

	private AnnotationId id;
	private List<Vote> endorseList;
	private List<Vote> reportList;
	private Summary summary;
	private Date created;
	private Date lastUpdated;

	
	public BaseModerationRecord(){}
	
	
	public AnnotationId getAnnotationId() {
		return id;
	}

	public void setAnnotationId(AnnotationId id) {
		this.id = id;
	}

	public List<Vote> getEndorseList() {
		return endorseList;
	}

	public void setEndorseList(List<Vote> endorseList) {
		this.endorseList = endorseList;
	}

	public List<Vote> getReportList() {
		return reportList;
	}

	public void setReportList(List<Vote> reportList) {
		this.reportList = reportList;
	}

	public Summary getSummary() {
		return summary;
	}

	public void setSummary(Summary summary) {
		this.summary = summary;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public void addReport(Vote vote) {
		if(reportList == null)
			reportList = new ArrayList<Vote>();
		
		if(! reportList.contains(vote))
			reportList.add(vote);
	}
	

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof ModerationRecord)) {
	        return false;
	    }

	    ModerationRecord that = (ModerationRecord) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if ((this.getAnnotationId() != null) && (that.getAnnotationId() != null) &&
	    		(!this.getAnnotationId().equals(that.getAnnotationId()))) {
	    	System.out.println("Moderation objects have different 'Id' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
			
	public boolean equalsContent(Object other) {
		return equals(other);
	}
	
	@Override
	public String toString() {
		String res = "\t### Moderation ###\n";
		
		if (getAnnotationId() != null) 
			res = res + "\t\t" + "Id:" + getAnnotationId() + "\n";
		return res;
	}	
}
