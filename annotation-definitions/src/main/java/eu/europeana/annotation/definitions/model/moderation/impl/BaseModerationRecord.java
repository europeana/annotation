package eu.europeana.annotation.definitions.model.moderation.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import eu.europeana.annotation.definitions.model.moderation.ModerationRecord;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.Vote;


/**
 * This class implements specification of the moderation policy.
 */
public class BaseModerationRecord implements ModerationRecord {

	private long identifier;
	private List<Vote> endorseList;
	private List<Vote> reportList;
	private Summary summary;
	private Date created;
	private Date lastUpdated;

	
	public BaseModerationRecord(){}

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
		
		reportList.add(vote);
	}
	

	@Override
	public boolean equalsContent(Object other) {
	    if (!(other instanceof ModerationRecord)) {
	        return false;
	    }

	    ModerationRecord that = (ModerationRecord) other;

	    boolean res = true;
	    
	    /**
	     * equality check for all relevant fields.
	     */
	    if (this.getIdentifier() != that.getIdentifier()) {
	    	System.out.println("Moderation objects have different 'identifier' fields.");
	    	res = false;
	    }
	    
	    return res;
	}
	
	
	@Override
	public String toString() {
		String res = "\t### Moderation ###\n";
		res = res + "\t\t" + "identifier:" + String.valueOf(identifier) + "\n";
		return res;
	}
	
	@Override
	public Summary computeSummary() {
		if(summary == null)
			summary = new BaseSummary();
		
		if(getEndorseList() != null)	
			summary.setEndorseSum(getEndorseList().size());
		
		if(getReportList() != null)	
			summary.setReportSum(getReportList().size());
		
		summary.computeScore();
		
		return summary;
		
	}	
	
	public long getIdentifier() {
	  return identifier;
	}

	public void setIdentifier(long identifier) {
	  this.identifier = identifier;
	}
}
