package eu.europeana.annotation.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationListUtils {
	
	public static List<String> getHttpUrls(List<? extends Annotation> annos) {
		List<String> httpUrls = new ArrayList<String>();
		for (Annotation anno : annos)
			if(anno.getAnnotationId() != null && StringUtils.isNotEmpty(anno.getAnnotationId().getHttpUrl()))
				httpUrls.add(anno.getAnnotationId().getHttpUrl());
		return httpUrls;
	}

}
