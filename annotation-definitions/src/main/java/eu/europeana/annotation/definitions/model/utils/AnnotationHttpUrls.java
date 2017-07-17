package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationHttpUrls {

	private List<? extends Annotation> annotations;
	
	// HTTP URLs (note that annotations do not necessarily have an identifier, the size
	// of the HTTP URLs list might therefore be smaller than the annotations list.
	private List<String> httpUrls;
	
	public AnnotationHttpUrls() {
		httpUrls = new ArrayList<String>();
	}

	public AnnotationHttpUrls(List<? extends Annotation> annotations) {
		this.annotations = annotations;
		httpUrls = new ArrayList<String>();
		updateHttpUrlsFromAnnotations();
	}

	public List<? extends Annotation> getAnnotations() { 
		return annotations;
	}

	public Integer size() {
		return annotations.size();
	}

	public void add(String httpUrl) {
		httpUrls.add(httpUrl);
	}

	public List<String> getHttpUrls() {
		return httpUrls;
	}
	
	private void updateHttpUrlsFromAnnotations() {
		httpUrls = new ArrayList<String>();
		for (Annotation anno : annotations) {
			if(anno.getAnnotationId() != null) {
				String httpUrl = anno.getAnnotationId().getHttpUrl();
				if(httpUrl != null && StringUtils.isNotEmpty(httpUrl))
					add(httpUrl);
			}
		}
	}

}
