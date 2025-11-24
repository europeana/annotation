package eu.europeana.annotation.web.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import eu.europeana.annotation.definitions.model.Annotation;

public class AnnotationOrderComparator implements Comparator<Annotation>{

  private final ConcurrentHashMap<Long, Integer> order;
  public AnnotationOrderComparator(List<Long> annotationIds) {
    order = new ConcurrentHashMap<Long, Integer>(annotationIds.size());
    int position = 0;
    for (Long annotationId : annotationIds) {
      order.put(annotationId, position++);
    }
  }
  
  @Override
  public int compare(Annotation o1, Annotation o2) {
    return order.getOrDefault(o1.getIdentifier(), Integer.MAX_VALUE) 
        - order.getOrDefault(o2.getIdentifier(), Integer.MAX_VALUE);
  }

}
