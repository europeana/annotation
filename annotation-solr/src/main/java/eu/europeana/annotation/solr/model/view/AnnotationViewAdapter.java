package eu.europeana.annotation.solr.model.view;

import java.util.Date;

import eu.europeana.annotation.definitions.model.view.AnnotationView;
import eu.europeana.annotation.solr.model.internal.SolrAnnotationImpl;

public class AnnotationViewAdapter extends SolrAnnotationImpl implements AnnotationView {

    @Override
    public String getIdentifierAsString() {
      return String.valueOf(getAnnoId());
    }
  
	@Override
	public Date getLastUpdate() {
		return null;
	}

}
