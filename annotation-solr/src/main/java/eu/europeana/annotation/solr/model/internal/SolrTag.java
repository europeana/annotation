package eu.europeana.annotation.solr.model.internal;

import eu.europeana.annotation.definitions.model.resource.TagResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;

public interface SolrTag extends TagResource {

		public static final String FIELD_LABEL = "value";
		public static final String FIELD_HTTPURI = "httpUri";
		public static final String FIELD_TAG_TYPE = "tagType";
		
		public abstract String getId();
		public abstract String getLabel();
		public abstract void setLabel(String label);
		public abstract void setTagType(TagTypes tagType);
		public abstract void setTagType(String tagType);
		public abstract String getTagType();
		
}
