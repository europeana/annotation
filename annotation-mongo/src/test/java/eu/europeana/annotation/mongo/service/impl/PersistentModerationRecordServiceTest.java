package eu.europeana.annotation.mongo.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.definitions.model.moderation.Summary;
import eu.europeana.annotation.definitions.model.moderation.impl.BaseSummary;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.model.PersistentModerationRecordImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentModerationRecord;
import eu.europeana.annotation.mongo.service.PersistentModerationRecordService;
import eu.europeana.api.commons.nosql.dao.NosqlDao;


@ExtendWith(SpringExtension.class)
@ContextConfiguration({ "/annotation-mongo-context.xml",
		"/annotation-mongo-test.xml" })
public class PersistentModerationRecordServiceTest {

	@Resource AnnotationConfiguration configuration;

	@Resource
	private PersistentModerationRecordService moderationRecordService;

	@Resource(name = "annotation_db_moderationRecordDao")
	NosqlDao<PersistentModerationRecord, String> moderationRecordDao;

	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@BeforeEach
	public void setup() throws IOException {
		moderationRecordDao.getCollection().drop();
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@AfterEach
	public void tearDown() throws IOException {
		// tagDao.getCollection().drop();
	}
	
	@Test
	public void createModerationRecord() throws AnnotationMongoException{
		PersistentModerationRecord moderationRecord = buildTestModerationRecord();
		PersistentModerationRecord savedModerationRecord = moderationRecordService.create(moderationRecord);
		
		assertEquals(savedModerationRecord.getSummary().getEndorseSum(), 5);
		assertEquals(savedModerationRecord.getSummary().getReportSum(), 2);
		assertEquals(savedModerationRecord.getSummary().getTotal(), 3);
		
//		assertTrue(savedModerationRecord.getCreated().getTime() > 0);
//		assertTrue(savedModerationRecord.getLastUpdated().getTime() > 0);			
	}

	private PersistentModerationRecord buildTestModerationRecord() {
		PersistentModerationRecord moderationRecord = new PersistentModerationRecordImpl();
//		moderationRecord.setAnnotationId(id);("testcase");
//		moderationRecord.setCreated(created);
//		moderationRecord.setEndorseList(endorseList);
//		moderationRecord.setLastIndexedTimestamp(lastIndexedTimestamp);
//		moderationRecord.setLastUpdated(lastUpdated);
//		moderationRecord.setReportList(reportList);
		Summary summary = new BaseSummary();
		summary.setEndorseSum(5);
		summary.setReportSum(2);
		moderationRecord.setSummary(summary);
		
		return moderationRecord;
	}
	
}
