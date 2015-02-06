/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.annotation.solr.model.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.WebAnnotationFields;
import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;

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
	public String getLabel() {
		return getValue();
	}

	@Override
	@Field("label")
	public void setLabel(String label) {
		setValue(label);
	}

	@Override
	public void setTagType(TagTypes tagType) {
		setTagType(tagType.name());
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
	
	protected Map<String, String> multilingual;

	@Override
	public Map<String, String> getMultilingual() {
		return multilingual;
	}

	@Override
	public void setMultilingual(Map<String, String> multilingual) {
		this.multilingual = multilingual;
	}
	
	@Override
	public void addLabelInMapping(String language, String label) {
	    if(this.multilingual == null) {
	        this.multilingual = new HashMap<String, String>();
	    }
	    this.multilingual.put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
	}

}
