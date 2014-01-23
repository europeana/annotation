package eu.europeana.annotation.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.SemanticTag;
import eu.europeana.annotation.definitions.model.impl.ImageAnnotationImpl;
import eu.europeana.annotation.definitions.model.impl.SemanticTagImpl;
import eu.europeana.annotation.definitions.model.shape.Point;
import eu.europeana.annotation.definitions.model.shape.impl.PointImpl;

public class AnnotationManagerTest {

	@Test
	public void createAnnotation(){
		
		AnnotationManagerApi annotationManager = new AnnotationManagerApi();
		SemanticTag tag = new SemanticTagImpl();
		tag.setCreator("unit test");
		tag.setEuropeanaId("testCollection/clientTestObject");
		tag.setLabel("clientTestTag");
		
		Annotation annotation = annotationManager.createAnnotation(tag);
		assertNotNull(annotation);
		assertNotNull(annotation.getAnnotationNr());
		//check the concrete class because ImageAnnotation extends SemanticTag
		assertTrue(annotation instanceof SemanticTagImpl);		
	} 
	
	@Test
	public void createImageAnnotation(){
		
		AnnotationManagerApi annotationManager = new AnnotationManagerApi();
		ImageAnnotation userAnnotation = new ImageAnnotationImpl();
		userAnnotation.setCreator("unit test");
		userAnnotation.setEuropeanaId("testCollection/clientTestObject");
		userAnnotation.setImageUrl("http://localhost:8081/testimages/testimage.jpg");
		userAnnotation.setText("text: unit test - image annotation");
//		Point p1 = ;
//		Point p = new PointImpl(0, 0);
//		Point p1 = new PointImpl(0, 0);
//		Point p1 = new PointImpl(0, 0);
		
		List<Point> shape = new ArrayList<Point>();
		shape.add(new PointImpl(0, 0));
		shape.add(new PointImpl(0, 5));
		shape.add(new PointImpl(5, 5));
		shape.add(new PointImpl(5, 0));
		
		userAnnotation.setShape(shape);
		
		List<String> entities = new ArrayList<String>();
		entities.add("Image annotation");
		entities.add("Open Annotation");
		userAnnotation.setNamedEntityLabelList(entities);
		
		List<String> entityUrls = new ArrayList<String>();
		entityUrls.add("http://wiki.org/Image_annotation");
		entityUrls.add("http://wiki.org/Open_Annotation");
		userAnnotation.setNamedEntityIdList(entityUrls);
		
		ImageAnnotation annotation = (ImageAnnotation)annotationManager.createAnnotation(userAnnotation);
		assertNotNull(annotation);
		assertNotNull(annotation.getAnnotationNr());
		assertTrue(annotation instanceof ImageAnnotation);
		assertEquals(entities.size(), annotation.getNamedEntityLabelList().size());
		assertEquals(entityUrls.size(), annotation.getNamedEntityIdList().size());
				
	} 
}
