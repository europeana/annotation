package eu.europeana.annotation.definitions.model.target;

import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public interface Target extends SpecificResource{

	public abstract void setEuropeanaId(String europeanaId);

	public abstract String getEuropeanaId();

	public abstract void setTargetTypeEnum(TargetTypes targetType);

	public abstract void setTargetType(String targetType);

	public abstract String getTargetType();

}
