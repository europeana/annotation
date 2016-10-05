package eu.europeana.annotation.mongo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Index;
import com.google.code.morphia.annotations.Indexes;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.Vote;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseSummary;
import eu.europeana.annotation.mongo.exception.ModerationMongoException;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.annotation.mongo.model.internal.PersistentObject;

@Entity("moderationRecord")
@Polymorphic
@Indexes({@Index(PersistentAnnotation.FIELD_BASEURL + ", "+ PersistentAnnotation.FIELD_PROVIDER + ", " + PersistentAnnotation.FIELD_IDENTIFIER), 
	@Index("provider, identifier")})
public class PersistentModerationRecordImpl implements PersistentModerationRecord, PersistentObject {

	@Id
	private ObjectId id;
	
	@Embedded
	private MongoAnnotationId annotationId;
	
//	private AnnotationId annotationId;
	private List<Vote> endorseList;
	private List<Vote> reportList;
	private Summary summary;
	private Date created;
	private Date lastUpdated;

	
	//getters and setters
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String toString() {
		String res = "\t### PersistentModerationRecord ###\n";
		
		if (getAnnotationId() != null) 
			res = res + "\t\t" + "Id:" + getAnnotationId() + "\n";
		return res;
	}
	
	public AnnotationId getAnnotationId() {
		return annotationId;
	}

	public void setAnnotationId(AnnotationId annotationId) {
//		this.annotationId = annotationId;
		if(annotationId instanceof MongoAnnotationId)
			this.annotationId = (MongoAnnotationId) annotationId;
		else{
			MongoAnnotationId id = new MongoAnnotationId();
			id.copyFrom(annotationId);
			this.annotationId = id;
		}
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

	@Override
	public void addReport(Vote vote) {
		if(reportList == null)
			reportList = new ArrayList<Vote>();
		
		reportList.add(vote);
	}

	//TODO try to remove duplication with BaseModerationRecord, probably by moving to some business logic class like ModerationHelper
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

	@Override
	public boolean equalsContent(Object other) {
		throw new RuntimeException(new ModerationMongoException("Method not supported"));
	}

}