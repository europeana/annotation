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

import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.definitions.model.vocabulary.TagTypes;
//import eu.europeana.corelib.solr.bean.impl.*;
//import eu.europeana.corelib.definitions.solr.DocType;
//import eu.europeana.corelib.definitions.solr.beans.BriefBean;
//import eu.europeana.corelib.web.service.impl.EuropeanaUrlServiceImpl;

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
	//
	//
	// @Field("creationTimestamp")
	// protected Long creationTimestamp;
	//
	// @Field("lastUpdateTimestamp")
	// protected Long lastUpdateTimestamp;
	//
	// @Field("lastUpdatedBy")
	// protected String lastUpdatedBy;
	//
	// @Field("creator")
	// protected String creator;
	//
	// @Field("tagType")
	// protected String tagType;
	//
	// @Field("translations")
	// protected Map<String, String> translations;
	//
	// @Field("sameAs")
	// protected List<String> sameAs;
	//
	// @Field("value")
	// protected String value;
	//
	// @Field("language")
	// protected String language;
	//
	// @Field("LANGUAGE")
	// protected String[] language;

	// @Override
	// public String getTagId() {
	// return id;
	// }
	// public void setTagId(String id) {
	// this.id = id;
	// }
	//
	// @Override
	// public Long getCreationTimestamp() {
	// return creationTimestamp;
	// }
	//
	// @Override
	// public void setCreationTimestamp(Long creationTimestamp) {
	// this.creationTimestamp = creationTimestamp;
	// }
	//
	// @Override
	// public Long getLastUpdateTimestamp() {
	// return lastUpdateTimestamp;
	// }
	//
	// @Override
	// public void setLastUpdateTimestamp(Long lastUpdateTimestamp) {
	// this.lastUpdateTimestamp = lastUpdateTimestamp;
	// }
	//
	// @Override
	// public Map<String, String> getTranslations() {
	// return translations;
	// }
	//
	// @Override
	// public void setTranslations(Map<String, String> translations) {
	// this.translations = translations;
	// }
	//
	// public void addTranslation(String language, String text){
	// if(getTranslations() == null)
	// this.translations = new HashMap<String, String>();
	//
	// getTranslations().put(language, text);
	// }
	//
	// @Override
	// public List<String> getSameAs() {
	// return sameAs;
	// }
	// protected void setSameAs(List<String> sameAs) {
	// this.sameAs = sameAs;
	// }
	//
	// @Override
	// public void addToSameAs(String value){
	// if(getSameAs() == null)
	// this.sameAs = new ArrayList<String>();
	//
	// getSameAs().add(value);
	// }
	//
	// @Override
	// public String getLastUpdatedBy() {
	// return lastUpdatedBy;
	// }
	//
	// @Override
	// public void setLastUpdatedBy(String lastUpdatedBy) {
	// this.lastUpdatedBy = lastUpdatedBy;
	// }
	//
	// @Override
	// public String getCreator() {
	// return creator;
	// }
	//
	// @Override
	// public void setCreator(String creator) {
	// this.creator = creator;
	// }
	//
	// @Override
	// public String getLabel() {
	// return getValue();
	// }
	//
	// @Override
	// public String getTagType() {
	// return tagType;
	// }
	//
	// @Override
	// public void setTagType(String tagType) {
	// this.tagType = tagType;
	// }
	//
	// @Override
	// public void setTagType(TagTypes tagType) {
	// this.tagType = tagType.name();
	// }
	//
	// @Override
	// public void setLabel(String label) {
	// this.setValue(label);
	// }
	//
	// @Override
	// public String getLanguage() {
	// return this.language;
	// }
	//
	// // @Override
	// // public String[] getLanguage() {
	// // return (this.language != null ? this.language.clone() : null);
	// // }
	//
	// @Override
	// public void setLanguage(String language) {
	// this.language = language;
	// }
	// @Override
	// public void setValue(String value) {
	// this.value = value;
	// }
	// @Override
	// public String getValue() {
	// return this.value;
	// }
	// @Override
	// public void setHttpUri(String httpUri) {
	// // TODO Auto-generated method stub
	// }
	// @Override
	// public String getHttpUri() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// @Override
	// public void setMediaType(String mediaType) {
	// // TODO Auto-generated method stub
	// }
	// @Override
	// public String getMediaType() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// @Override
	// public void setContentType(String contentType) {
	// // TODO Auto-generated method stub
	//
	// }
	// @Override
	// public String getContentType() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	// @Override
	// public void copyInto(InternetResource destination) {
	// // TODO Auto-generated method stub
	//
	// }
}
