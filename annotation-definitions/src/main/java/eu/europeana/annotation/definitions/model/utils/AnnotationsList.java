package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationsList {

	enum idFilterType {
		ID_AVAILABLE, ID_MISSING
	}

	private List<? extends Annotation> annotations;
	private List<String> httpUrls;

	public AnnotationsList(List<? extends Annotation> annotations) {
		this.annotations = annotations;
	}
	
	
	public List<String> getHttpUrls() {
		if(httpUrls == null)
			this.httpUrls = getHttpUrlsFromAnnotations();
		return httpUrls;
	}
	
	public List<String> getHttpUrlsFromAnnotations() {
		httpUrls = new ArrayList<String>();
		for (Annotation anno : annotations) {
			if(anno.getAnnotationId() != null) {
				String httpUrl = anno.getAnnotationId().getHttpUrl();
				if(httpUrl != null && StringUtils.isNotEmpty(httpUrl))
					httpUrls.add(httpUrl);
			}
		}
		return httpUrls;
	}

	public int size() {
		return annotations.size();
	}

	public List<? extends Annotation> getAnnotations() {
		return annotations;
	}

	public AnnotationsList getAnnotationsWithId() {
		List<Annotation> annosWithId = new ArrayList<Annotation>();
		putAnnotationsDependingOnIdAvailability(annosWithId, annotations, idFilterType.ID_AVAILABLE);
		return new AnnotationsList(annosWithId);
	}

	public AnnotationsList getAnnotationsWithoutId() {
		List<Annotation> annosWithoutId = new ArrayList<Annotation>();
		putAnnotationsDependingOnIdAvailability(annosWithoutId, annotations, idFilterType.ID_MISSING);
		return new AnnotationsList(annosWithoutId);
	}
	
	public HashMap<String, ? extends Annotation> getHttpUrlAnnotationsMap(AnnotationsList annoList) {
		List<? extends Annotation> annos = annoList.getAnnotations();
		HashMap<String, Annotation> annosMapWithHttpUrlKey = new HashMap<String, Annotation>();
		for (Annotation anno : annos)
			if (anno.getAnnotationId() != null) {
				String httpUrl = anno.getAnnotationId().getHttpUrl();
				if(httpUrl != null && StringUtils.isNotEmpty(httpUrl))
					annosMapWithHttpUrlKey.put(httpUrl, anno);
			}	
		return annosMapWithHttpUrlKey;
	}
	
	public LinkedHashMap<Annotation, Annotation> getAnnotationsMap() {
		LinkedHashMap<Annotation, Annotation> annosMap = new LinkedHashMap<Annotation, Annotation>();
		for (Annotation anno : annotations)
				annosMap.put(anno, null);
		return annosMap;
	}
	
	public HashMap<String, ? extends Annotation> getHttpUrlAnnotationsMap() {
		return getHttpUrlAnnotationsMap(this);
	}
	
	protected void putAnnotationsDependingOnIdAvailability(List<? super Annotation> dest, List<? extends Annotation> src,
			idFilterType filter) {
		Annotation anno = null;
		for (int i = 0; i < src.size(); i++) {
			anno = src.get(i);
			boolean hasHttpUrl = anno.getAnnotationId() != null 
					&& anno.getAnnotationId().getHttpUrl() != null 
					&&  StringUtils.isNotEmpty(anno.getAnnotationId().getHttpUrl()) ;
			if ((filter == idFilterType.ID_AVAILABLE && hasHttpUrl)
					|| (filter == idFilterType.ID_MISSING && !hasHttpUrl))
				dest.add(src.get(i));
		}
	}

}
