package eu.europeana.annotation.solr.model.internal;

import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;

/**
 * @see eu.europeana.corelib.definitions.solr.beans.BriefBean
 */
// @JsonSerialize(include = Inclusion.NON_EMPTY)
public class SolrTagImpl extends BaseTagResource implements SolrTag { 

	@Override
	@Field("tag_id")
	public void setId(String id) {
		super.setId(id);
	}

	@Override
	@Field("label")
	public void setLabel(String label) {
		super.setLabel(label);;
	}
	
	@Override
	@Field("tag_type")
	public void setTagType(String tagType) {
		super.setTagType(tagType);
	}

	@Override
	@Field("http_uri")
	public void setHttpUri(String httpUri) {
		super.setHttpUri(httpUri);
	}

	@Override
	@Field("language")
	public void setLanguage(String language) {
		super.setLanguage(language);
	}
	
	@Override
	@Field("creator")
	public void setCreator(String creator) {
		super.setCreator(creator);
	}
	
	@Override
	@Field("*_multilingual")
	public void setMultilingual(Map<String, String> multilingual) {
		super.setMultilingual(multilingual);
	}
	
}
