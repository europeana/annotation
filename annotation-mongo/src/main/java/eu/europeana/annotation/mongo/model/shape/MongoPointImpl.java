package eu.europeana.annotation.mongo.model.shape;

import org.mongodb.morphia.annotations.Embedded;

import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;

@Embedded
public class MongoPointImpl extends PointImpl {

	public MongoPointImpl(Integer posX, Integer posY){
		super(posX, posY);
	}
	
	public MongoPointImpl(){
		super();
	}
}
