package eu.europeana.annotation.mongo.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;
import eu.europeana.annotation.mongo.model.PersistentObjectTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;

public class AnnotationTestDataBuilder {

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	public final static String TEST_DRACULA_ID = "/15502/D75AA597009ABDCBF6E8117800CB53EADCAEF5E8";

	protected void checkAnnotation(Annotation persistantObject, Annotation storedAnnotation) {
	
		// persistantObject.setAnnotatedBy(new PersistantAgent);
		assertEquals(persistantObject.getAnnotationId().getResourceId(),
				storedAnnotation.getAnnotationId().getResourceId());
	
		assertNotNull(storedAnnotation.getAnnotationId().getAnnotationNr());
		assertNotNull(((PersistentAnnotation) storedAnnotation)
				.getAnnotatedAt());
		assertEquals(
				((PersistentAnnotation) storedAnnotation).getAnnotatedAt(),
				((PersistentAnnotation) storedAnnotation).getSerializedAt());
	
		assertNotNull(storedAnnotation.getHasBody());
		assertNotNull(storedAnnotation.getHasTarget());
		assertNotNull(storedAnnotation.getAnnotatedBy());
	}

	protected ObjectTag buildObjectTag() {
		ObjectTag persistentObject = new PersistentObjectTagImpl();
		// set target
		Target target = new BaseTarget(TargetTypes.WEB_PAGE);
		target.setMediaType("image");
		target.setContentType("text-html");
		target.setHttpUri("http://europeana.eu/portal/record/15502/D75AA597009ABDCBF6E8117800CB53EADCAEF5E8.html");
		target.setEuropeanaId(TEST_DRACULA_ID);
		persistentObject.setHasTarget(target);
	
		// set AnnotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("unit test");
		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		persistentObject.setAnnotatedBy(creator);
		return persistentObject;
	}

	protected PlainTagBody buildPlainTagBody() {
		PlainTagBody body = new PlainTagBody();
		// TODO: change to predefined values
		body.setContentType("cnt:chars");
		body.setValue("vlad tepes");
		return body;
	}

	protected SemanticTagBody buildSemanticTagBody() {
		SemanticTagBody body = new SemanticTagBody();
		//TODO: change to predefined values
		body.setContentType("Link");
		body.setValue("Vlad Tepes");
		body.setHttpUri("https://www.freebase.com/m/035br4");
		body.setLanguage("ro");
		return body;
	}

}
