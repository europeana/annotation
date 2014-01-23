package eu.europeana.annotation.mongo.model.shape;

import com.google.code.morphia.annotations.Embedded;

import eu.europeana.annotation.definitions.model.shape.impl.PointImpl;

@Embedded
public class MongoPointImpl extends PointImpl {

	public MongoPointImpl(Integer posX, Integer posY){
		super(posX, posY);
	}
	
	public MongoPointImpl(){
		super();
	}
}
