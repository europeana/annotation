package eu.europeana.annotation.solr.model.internal;

import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;

public interface SolrTag extends TagResource {

		public static final String FIELD_LABEL = "value";
		public static final String FIELD_HTTPURI = "httpUri";
		public static final String FIELD_TAG_TYPE = "tagType";
		
		
}
