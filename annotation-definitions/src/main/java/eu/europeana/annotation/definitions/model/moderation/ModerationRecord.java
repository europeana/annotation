package eu.europeana.annotation.definitions.model.moderation;

import java.util.Date;
import java.util.List;

public interface ModerationRecord {

	public List<Vote> getEndorseList();

	public void setEndorseList(List<Vote> endorseList);

	public List<Vote> getReportList();

	public void setReportList(List<Vote> reportList);

	public Summary getSummary();

	public Summary computeSummary();

	public void setSummary(Summary summary);

	public Date getCreated();

	public void setCreated(Date created);

	public Date getLastUpdated();

	public void setLastUpdated(Date lastUpdated);
	
	public void addReport(Vote vote);

	boolean equalsContent(Object other);
	
    public long getIdentifier();

    public void setIdentifier(long identifier);
}
