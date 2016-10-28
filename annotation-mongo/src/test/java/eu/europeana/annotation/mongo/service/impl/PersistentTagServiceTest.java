package eu.europeana.annotation.mongo.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
import eu.europeana.annotation.mongo.exception.AnnotationMongoException;
import eu.europeana.annotation.mongo.exception.InvalidTagException;
import eu.europeana.annotation.mongo.model.PersistentTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.annotation.mongo.service.PersistentTagService;
import eu.europeana.corelib.db.dao.NosqlDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/annotation-mongo-context.xml",
		"/annotation-mongo-test.xml" })
public class PersistentTagServiceTest {

	final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	
	@Resource
	private PersistentTagService tagService;

	@Resource(name = "annotation_db_tagDao")
	NosqlDao<PersistentTag, String> tagDao;

	/**
	 * Initialize the testing session
	 * 
	 * @throws IOException
	 */
	@Before
	public void setup() throws IOException {
		//MorphiaLoggerFactory.registerLogger( SLF4JLogrImplFactory.class);
		tagDao.getCollection().drop();
	}

	/**
	 * Cleaning the testing session's data
	 * 
	 * @throws IOException
	 */
	@After
	public void tearDown() throws IOException {
		// tagDao.getCollection().drop();
	}
	
	@Test
	public void createSimpleTag() throws AnnotationMongoException{
		PersistentTag tag = buildTestTag();
		PersistentTag savedTag = tagService.create(tag);
		
		assertEquals(TagTypes.SIMPLE_TAG.name(), savedTag.getTagType());
		assertEquals(tag.getLabel(), savedTag.getLabel());
		assertEquals(tag.getCreator(), savedTag.getCreator());
		assertEquals(tag.getTagType(), savedTag.getTagType());
		
		assertTrue(savedTag.getCreationTimestamp() > 0);
		assertTrue(savedTag.getLastUpdateTimestamp() > 0);
		
		assertNotNull(savedTag.getLastUpdatedBy());
		
	}

	private PersistentTag buildTestTag() {
		PersistentTag tag = new PersistentTagImpl();
		tag.setCreator("testcase");
		tag.setContentType("text");
		tag.setLanguage("ro");
		tag.setLabel("Vlad Tepes");
		tag.setTagTypeEnum(TagTypes.SIMPLE_TAG);
		
		return tag;
	}
	
	private PersistentTag buildQueryTag() {
		PersistentTag tag = new PersistentTagImpl();
		//tag.setCreator("testcase");
		tag.setContentType("text");
		//tag.setLanguage("ro");
		tag.setLabel("Vlad Tepes");
		tag.setTagTypeEnum(TagTypes.SIMPLE_TAG);
		
		return tag;
	}
	
	@Test
	public void createSemanticTag() throws AnnotationMongoException{
		PersistentTag tag = buildTestTag();
		tag.setTagTypeEnum(TagTypes.SEMANTIC_TAG);
		tag.setHttpUri("http://ro.dbpedia.org/resource/Vlad_Țepeș");
		
		
		PersistentTag savedTag = tagService.create(tag);
		
		assertEquals(TagTypes.SEMANTIC_TAG.name(), savedTag.getTagType());
		assertEquals(tag.getHttpUri(), savedTag.getHttpUri());
		assertEquals(tag.getLabel(), savedTag.getLabel());
		assertEquals(tag.getCreator(), savedTag.getCreator());
		assertEquals(tag.getTagType(), savedTag.getTagType());
		
		assertTrue(savedTag.getCreationTimestamp() > 0);
		assertTrue(savedTag.getLastUpdateTimestamp() > 0);
		
		assertNotNull(savedTag.getLastUpdatedBy());
	}
	
	@Test(expected=InvalidTagException.class)
	public void createSemanticTagException() throws AnnotationMongoException{
		PersistentTag tag = buildTestTag();
		tag.setTagTypeEnum(TagTypes.SEMANTIC_TAG);
		tagService.create(tag);
	}
	
	@Test
	public void findSimpleTag() throws AnnotationMongoException{
		//create the tag
		PersistentTag tag = buildTestTag();
		tagService.create(tag);
		
		//search the tag
		Long retrievalTime = System.currentTimeMillis();
		PersistentTag query = buildQueryTag();
		PersistentTag savedTag = tagService.find(query);
		
		assertEquals(TagTypes.SIMPLE_TAG.name(), savedTag.getTagType());
		assertEquals(tag.getLabel(), savedTag.getLabel());
		assertEquals(tag.getCreator(), savedTag.getCreator());
		assertEquals(tag.getTagType(), savedTag.getTagType());
		
		assertTrue(savedTag.getCreationTimestamp() < retrievalTime);
		assertTrue(savedTag.getLastUpdateTimestamp() < retrievalTime);
		
		assertNotNull(savedTag.getLastUpdatedBy());
		
	}
	
	@Test
	public void findById() throws AnnotationMongoException{
		//create the tag
		PersistentTag tag = buildTestTag();
		PersistentTag storedTag = tagService.create(tag);
		String tagId = storedTag.getId().toString();
		assertNotNull(tagId);
		
		//remove the tag
		storedTag = tagService.findByID(tagId);
		
		assertNotNull(storedTag);
		assertEquals(tagId, storedTag.getId().toString());
	}
	
	@Test
	public void findAndRemoveTag() throws AnnotationMongoException{
		//create the tag
		PersistentTag tag = buildTestTag();
		PersistentTag storedTag = tagService.create(tag);
		String tagId = storedTag.getId().toString();
		assertNotNull(tagId);
		
		//remove the tag
		PersistentTag queryTag = buildQueryTag();
		tagService.remove(queryTag);
		
		//search tag 
		PersistentTag notFound = tagService.find(queryTag);
		assertNull(notFound);
	}
	
	@Test
	public void removeTagById() throws AnnotationMongoException{
		//create the tag
		PersistentTag tag = buildTestTag();
		PersistentTag storedTag = tagService.create(tag);
		String tagId = storedTag.getId().toString();
		assertNotNull(tagId);
		
		//remove the tag
		tagService.remove(tagId);
		
		//search tag 
		PersistentTag notFound = tagService.findByID(tagId);
		assertNull(notFound);
	}
	
	@Test
	public void updateTag() throws AnnotationMongoException{
		//create the tag
		PersistentTag tag = buildTestTag();
		PersistentTag storedTag = tagService.create(tag);
		String tagId = storedTag.getId().toString();
		assertNotNull(tagId);
		
		tag.setLabel("Vlad the Impaler");
		tag.setLanguage("en");
		
		//update the tag
		storedTag = tagService.update(tag, "sergiu");
		assertNotNull(storedTag);
		
		assertEquals(tag.getLabel(), storedTag.getLabel());
		assertEquals(tag.getLanguage(), storedTag.getLanguage());
		assertEquals("sergiu", storedTag.getLastUpdatedBy());
		assertTrue(storedTag.getLastUpdateTimestamp() > storedTag.getCreationTimestamp()); 
		
		//read the tag
		storedTag = tagService.find(tag);
		assertNotNull(storedTag);
		
		assertEquals(tag.getLabel(), storedTag.getLabel());
		assertEquals(tag.getLanguage(), storedTag.getLanguage());
		assertEquals("sergiu", storedTag.getLastUpdatedBy());
		assertTrue(storedTag.getLastUpdateTimestamp() > storedTag.getCreationTimestamp()); 
		
	}
}
