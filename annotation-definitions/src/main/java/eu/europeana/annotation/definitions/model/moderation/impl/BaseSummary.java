package eu.europeana.annotation.definitions.model.moderation.impl;

import eu.europeana.annotation.definitions.model.moderation.Summary;


/**
 * This class comprises summary about votes.
 */
public class BaseSummary implements Summary {

	private int endorseSum;
	private int reportSum;
	
	public int getTotal() {
		return endorseSum - reportSum;
	}

	public int getEndorseSum() {
		return endorseSum;
	}

	public void setEndorseSum(int endorseSum) {
		this.endorseSum = endorseSum;
	}

	public int getReportSum() {
		return reportSum;
	}

	public void setReportSum(int reportSum) {
		this.reportSum = reportSum;
	}

	@Override
	public boolean equals(Object other) {
	    if (!(other instanceof Summary)) {
	        return false;
	    }

	    return true;
	}
			
	public boolean equalsContent(Object other) {
		return equals(other);
	}
	
	@Override
	public String toString() {
		String res = "\t### Summary ###\n";
		
		if (getEndorseSum() > 0 ) 
			res = res + "\t\t" + "EndorseSum:" + getEndorseSum() + "\n";
		if (getReportSum() > 0) 
			res = res + "\t\t" + "ReportSum:" + getReportSum() + "\n";
		if (getTotal() > 0) 
			res = res + "\t\t" + "Total:" + getTotal() + "\n";
		return res;
	}	
}
