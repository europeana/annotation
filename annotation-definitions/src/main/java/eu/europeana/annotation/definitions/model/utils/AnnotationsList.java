package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationsList {

	private List<Annotation> annotations;
	
	// HTTP URLs (note that annotations do not necessarily have an identifier, the size
	// of the HTTP URLs list might therefore be smaller than the annotations list.
	private List<String> httpUrls;

	protected AnnotationsList() {
		annotations = new ArrayList<Annotation>();
		httpUrls = new ArrayList<String>();
	}

	public AnnotationsList(List<? extends Annotation> annotations) {
		this.annotations = (List<Annotation>) annotations;
		httpUrls = new ArrayList<String>();
		updateHttpUrls();
	}

	public List<Annotation> getAnnotations() { 
		return annotations;
	}

	public Integer size() {
		return annotations.size();
	}

	public void add(Annotation anno) {
		annotations.add(anno);
		addHttpUrl(anno);
	}

	public List<String> getHttpUrls() {
		return httpUrls;
	}
	
	private void updateHttpUrls() {
		for (Annotation anno : annotations) {
			addHttpUrl(anno);
		}
	}
	
	private void addHttpUrl(Annotation anno) {
		if (anno.hasHttpUrl())
			httpUrls.add(anno.getAnnotationId().getHttpUrl());
	}

}
