package eu.europeana.annotation.mongo.model;

import java.util.HashMap;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import eu.europeana.annotation.definitions.model.resource.impl.BaseTagResource;
import eu.europeana.annotation.definitions.model.vocabulary.WebAnnotationFields;
import eu.europeana.annotation.mongo.model.internal.PersistentTag;
import eu.europeana.api.commons.nosql.entity.NoSqlEntity;

/**
 * 
 * @author Sergiu Gordea 
 *
 */
@Entity("tag")
@Indexes( {@Index("httpUri, tagType"), @Index("value, tagType"), @Index("value")})
public class PersistentTagImpl extends BaseTagResource implements PersistentTag, NoSqlEntity{
	
	@Id
	private ObjectId objectId;
	
	
	@Override
	public ObjectId getObjectId() {
		return objectId;
	}
	public void setObjectId(ObjectId id) {
		this.objectId = id;
	}
	
	@Override
	public void addMultilingualLabel(String language, String label) {
	    if(this.multilingual == null) {
	        this.multilingual = new HashMap<String, String>();
	    }
	    this.multilingual.put(language + "_" + WebAnnotationFields.MULTILINGUAL, label);
	}
	
	@Override
	public String getId() {
		if(getObjectId() != null)
			return this.getObjectId().toString();
		else return null;
	}
}
