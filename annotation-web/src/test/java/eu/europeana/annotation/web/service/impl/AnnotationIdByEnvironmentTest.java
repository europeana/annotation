package eu.europeana.annotation.web.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import javax.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import eu.europeana.annotation.config.AnnotationConfiguration;
import eu.europeana.annotation.config.AnnotationConfigurationImpl;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.util.AnnotationTestObjectBuilder;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.mongo.service.PersistentAnnotationService;
import eu.europeana.annotation.solr.exceptions.AnnotationServiceException;
import eu.europeana.annotation.web.exception.InternalServerException;
import eu.europeana.annotation.web.service.AnnotationService;


/**
 * Unit test for the Web Annotation service
 *
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/annotation-mongo-test.xml"})
public class AnnotationIdByEnvironmentTest extends AnnotationTestObjectBuilder{

    Logger log = LogManager.getLogger(getClass());
  
	@Resource 
	AnnotationService webAnnotationService;
	
    @Resource(name="adminService") 
    AdminServiceImpl adminService;
    
    @Resource(name="annotation_db_annotationService")
    PersistentAnnotationService mongoPersistance;

	@Test
	public void testStoreInDevelopmentEnvironment() 
			throws MalformedURLException, IOException, AnnotationServiceException, InternalServerException {
		
		String environment = AnnotationConfiguration.VALUE_ENVIRONMENT_DEVELOPMENT;
        long annoIdentifier = mongoPersistance.generateAnnotationIdentifier();
		createTestAnnoInEnvironment(environment, annoIdentifier);	
	}

	@Test
	public void testStoreInTestEnvironment() 
			throws MalformedURLException, IOException, AnnotationServiceException, InternalServerException {
		
		String environment = AnnotationConfiguration.VALUE_ENVIRONMENT_TEST;
        long annoIdentifier = mongoPersistance.generateAnnotationIdentifier();
		createTestAnnoInEnvironment(environment, annoIdentifier);
	}

	@Test
	public void testStoreInProductionEnvironment() 
			throws MalformedURLException, IOException, AnnotationServiceException, InternalServerException {
		
		String environment = AnnotationConfiguration.VALUE_ENVIRONMENT_PRODUCTION;
        long annoIdentifier = mongoPersistance.generateAnnotationIdentifier();
		createTestAnnoInEnvironment(environment, annoIdentifier);
	}

	protected void createTestAnnoInEnvironment(String environment, long identifier)
			throws MalformedURLException, IOException, AnnotationServiceException, InternalServerException {
		
		AnnotationConfigurationImpl config = (AnnotationConfigurationImpl) ((BaseAnnotationServiceImpl)webAnnotationService).getConfiguration();
//		config.setEnvironment(environment);
		
		Annotation anno = testCreateAnnotationWebanno(identifier);
		
		log.debug("Identifier in "+ environment + " environment: " + anno.getIdentifier());
		adminService.deleteAnnotation(anno.getIdentifier());
	}
	
	public Annotation testCreateAnnotationWebanno(long identifier) 
			throws MalformedURLException, IOException, AnnotationServiceException {
		
		Annotation testAnnotation = createTestAnnotation(identifier);		

		/**
		 * Store Annotation in database.
		 */
		Annotation webAnnotation = webAnnotationService.storeAnnotation(testAnnotation);
		
		if (StringUtils.isBlank(webAnnotation.getType())) {
			webAnnotation.setType(AnnotationTypes.OBJECT_TAG.name());
		}
		
//		log.debug("testAnnotation: " + testAnnotation.toString());
//		log.debug("webAnnotation: " + webAnnotation.toString());
		
		return webAnnotation;
	}

	/**
	 * Create a test annotation object.
	 * @return Annotation
	 */
	Annotation createTestAnnotation(long identifier) {
		Annotation testAnnotation = createBaseObjectTagInstance(identifier);
		return testAnnotation;
	}

	
	
	
		
}
