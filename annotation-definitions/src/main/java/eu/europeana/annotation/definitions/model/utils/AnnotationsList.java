package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;
import eu.europeana.annotation.definitions.model.search.result.AnnotationPage;

public class AnnotationsList {
	
	private List<? extends Annotation> annotations;
	
	public AnnotationsList(List<? extends Annotation> annotations) {
		this.annotations = annotations;
	}
	
	public List<String> getHttpUrls() {
		List<String> httpUrls = new ArrayList<String>();
		for(Annotation anno : annotations)
			if (anno.hasHttpUrl())
				httpUrls.add(anno.getHttpUrl());
		return httpUrls;
	}
	
	public int size() {
		return annotations.size();
	}
	
//	public AnnotationHttpUrls getAnnotationsWithoutID() {
//		AnnotationHttpUrls annotationsWithoutId = new AnnotationHttpUrls();
//		for(Annotation anno : annotations.getAnnotations())
//			if (!anno.hasHttpUrl())
//				annotationsWithoutId.add(anno.getHttpUrl());
//		return annotationsWithoutId;
//	}

}
