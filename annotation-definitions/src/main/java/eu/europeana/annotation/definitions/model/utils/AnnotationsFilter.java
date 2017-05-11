package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationsFilter {
	
	private AnnotationsList annotations;
	
	public AnnotationsFilter(List<? extends Annotation> annotations) {
		this.annotations = new AnnotationsList((List<Annotation>)annotations);
	}
	
	public AnnotationsList getAnnotationsWithID() {
		AnnotationsList annotationsWithId = new AnnotationsList();
		for(Annotation anno : annotations.getAnnotations())
			if (anno.getAnnotationId() != null && StringUtils.isNotEmpty(anno.getAnnotationId().getHttpUrl()))
				annotationsWithId.add(anno);
		return annotationsWithId;
	}
	
	public AnnotationsList getAnnotationsWithoutID() {
		AnnotationsList annotationsWithoutId = new AnnotationsList();
		for(Annotation anno : annotations.getAnnotations())
			if (anno.getAnnotationId() == null || StringUtils.isEmpty(anno.getAnnotationId().getHttpUrl()))
				annotationsWithoutId.add(anno);
		return annotationsWithoutId;
	}

}
