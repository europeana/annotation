package eu.europeana.annotation.definitions.model.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationsList {

	enum idFilterType {
		ID_AVAILABLE, ID_MISSING
	}

	private List<? extends Annotation> annotations;

	public AnnotationsList(List<? extends Annotation> annotations) {
		this.annotations = annotations;
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
	
	public LinkedHashMap<Annotation, Annotation> getAnnotationsMap() {
		LinkedHashMap<Annotation, Annotation> annosMap = new LinkedHashMap<Annotation, Annotation>();
		for (Annotation anno : annotations)
				annosMap.put(anno, null);
		return annosMap;
	}
	
	protected void putAnnotationsDependingOnIdAvailability(List<? super Annotation> dest, List<? extends Annotation> src,
			idFilterType filter) {
		Annotation anno = null;
		for (int i = 0; i < src.size(); i++) {
			anno = src.get(i);
			boolean hasIdentifier = anno.getIdentifier() != 0;
			if ((filter == idFilterType.ID_AVAILABLE && hasIdentifier)
					|| (filter == idFilterType.ID_MISSING && !hasIdentifier))
				dest.add(src.get(i));
		}
	}
	
	public List<Long> getIdentifiers() {
	  return annotations.stream().map(x -> x.getIdentifier()).collect(Collectors.toList());
	}

}