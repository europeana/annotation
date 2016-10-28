package eu.europeana.annotation.mongo.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.ObjectTag;
import eu.europeana.annotation.definitions.model.agent.Agent;
import eu.europeana.annotation.definitions.model.agent.impl.SoftwareAgent;
import eu.europeana.annotation.definitions.model.body.impl.PlainTagBody;
import eu.europeana.annotation.definitions.model.body.impl.SemanticTagBody;
import eu.europeana.annotation.definitions.model.body.impl.TextBody;
import eu.europeana.annotation.definitions.model.impl.BaseAnnotationId;
import eu.europeana.annotation.definitions.model.resource.selector.Rectangle;
import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.resource.selector.impl.SvgRectangleSelector;
import eu.europeana.annotation.definitions.model.resource.state.State;
import eu.europeana.annotation.definitions.model.resource.state.impl.BaseState;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.target.impl.BaseTarget;
import eu.europeana.annotation.definitions.model.target.impl.ImageTarget;
import eu.europeana.annotation.definitions.model.vocabulary.AnnotationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.MotivationTypes;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;
import eu.europeana.annotation.mongo.model.PersistentImageAnnotationImpl;
import eu.europeana.annotation.mongo.model.PersistentObjectTagImpl;
import eu.europeana.annotation.mongo.model.internal.PersistentAnnotation;

public class AnnotationTestDataBuilder {

	public final static String TEST_EUROPEANA_ID = "/testCollection/testObject";
	public final static String TEST_DRACULA_ID = "/15502/GG_8285";
	String baseAnnotationUrl = null;
	
	public void setBaseAnnotationUrl(String baseAnnotationUrl) {
		this.baseAnnotationUrl = baseAnnotationUrl;
	}


	public String getBaseAnnotationUrl() {
		return baseAnnotationUrl;
	}


	public AnnotationTestDataBuilder(String baseAnnotationUrl){
		this.baseAnnotationUrl = baseAnnotationUrl;
	}
	
	
	protected void checkAnnotation(Annotation persistantObject, Annotation storedAnnotation) {
	
		// persistantObject.setAnnotatedBy(new PersistantAgent);
//		assertEquals(persistantObject.getAnnotationId().getResourceId(),
//				storedAnnotation.getAnnotationId().getResourceId());
	
		assertNotNull(storedAnnotation.getAnnotationId().getIdentifier());
		assertNotNull(((PersistentAnnotation) storedAnnotation)
				.getCreated());
		assertEquals(
				((PersistentAnnotation) storedAnnotation).getCreated(),
				((PersistentAnnotation) storedAnnotation).getGenerated());
	
		assertNotNull(storedAnnotation.getBody());
		assertNotNull(storedAnnotation.getTarget());
		assertNotNull(storedAnnotation.getCreator());
	}

	protected ObjectTag buildObjectTag() {
		ObjectTag persistentObject = new PersistentObjectTagImpl();
		Target target = buildWebpageTarget();
		persistentObject.setTarget(target);
	
		// set AnnotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("unit test");
		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		persistentObject.setCreator(creator);
		persistentObject.setAnnotationId(new BaseAnnotationId(getBaseAnnotationUrl(), "webanno", null));
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
	
	protected TextBody buildTextBody(String text, String language) {
		TextBody body = new  TextBody();
		//TODO: change to predefined values
		body.setContentType("dcTypes:Text");
		body.setValue(text);
		//body.setMediaType("");
		//body.setHttpUri("https://www.freebase.com/m/035br4");
		
		return body;
	}

	protected ImageAnnotation createSimpleAnnotationInstance() {
		ImageAnnotation persistentObject = new PersistentImageAnnotationImpl();
		persistentObject.setInternalType(AnnotationTypes.OBJECT_COMMENT.name());
		
		// set target
		Target target = buildImageTarget();
		persistentObject.setTarget(target);
			
		//set Body
		String comment = "Same hair style as in Dracula Untold: https://www.youtube.com/watch?v=_2aWqecTTuE";
		TextBody body = buildTextBody(comment, "en");
		persistentObject.setBody(body);
				
		// set AnnotatedBy
		Agent creator = new SoftwareAgent();
		creator.setName("unit test");
		creator.setHomepage("http://www.pro.europeana.eu/web/europeana-creative");
		persistentObject.setCreator(creator);
		
		//set serializeb by
		persistentObject.setGenerator(creator);
		
		//motivation
		persistentObject.setMotivation(MotivationTypes.COMMENTING.name());
		
		//persistentObject.setType(type)
		persistentObject.setAnnotationId(new BaseAnnotationId(getBaseAnnotationUrl(), "webanno", null));
		return persistentObject;
	}

	private Target buildWebpageTarget() {
		Target target = new BaseTarget(TargetTypes.WEB_PAGE);
//		target.setMediaType("image");
		target.setContentType("text-html");
		target.setHttpUri("http://europeana.eu/portal/record/15502/GG_8285.html");
//		target.setEuropeanaId(TEST_DRACULA_ID);
		return target;
	}
	
	private Target buildImageTarget() {
		Target target = new ImageTarget();
//		target.setMediaType("image");
		target.setContentType("image/jpeg");
		target.setHttpUri("http://europeanastatic.eu/api/image?uri=http%3A%2F%2Fbilddatenbank.khm.at%2Fimages%2F500%2FGG_8285.jpg&size=FULL_DOC&type=IMAGE");
//		target.setEuropeanaId(TEST_DRACULA_ID);
		
		Rectangle selector = new SvgRectangleSelector();
		selector.setX(5);
		selector.setY(5);
		selector.setHeight(100);
		selector.setWidth(200);
		
		target.setSelector((Selector)selector);
		
		State state = new BaseState();
		state.setFormat("image/jpeg");
		state.setVersionUri("http://bilddatenbank.khm.at/images/350/GG_8285.jpg");
		state.setAuthenticationRequired(false);
		target.setState(state);
		
		return target;
	}
}
