package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationsList {

	enum idFilterType {
		ID_AVAILABLE, ID_MISSING
	}

	private List<? extends Annotation> annotations;

	public AnnotationsList(List<? extends Annotation> annotations) {
		this.annotations = annotations;
	}

	public List<String> getHttpUrls() {
		List<String> httpUrls = new ArrayList<String>();
		for (Annotation anno : annotations)
			if (anno.hasHttpUrl())
				httpUrls.add(anno.getHttpUrl());
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
			if (anno.hasHttpUrl())
				annosMapWithHttpUrlKey.put(anno.getHttpUrl(), anno);
		return annosMapWithHttpUrlKey;
	}

	public HashMap<String, ? extends Annotation> getHttpUrlAnnotationsMap() {
		return getHttpUrlAnnotationsMap(this);
	}
	
	protected void putAnnotationsDependingOnIdAvailability(List<? super Annotation> dest, List<? extends Annotation> src,
			idFilterType filter) {
		for (int i = 0; i < src.size(); i++) {
			boolean hasHttpUrl = src.get(i).hasHttpUrl();
			if ((filter == idFilterType.ID_AVAILABLE && hasHttpUrl)
					|| (filter == idFilterType.ID_MISSING && !hasHttpUrl))
				dest.add(src.get(i));
		}
	}

}
