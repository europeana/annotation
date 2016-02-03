package eu.europeana.annotation.definitions.model.target;

import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public interface Target extends SpecificResource{

	public abstract void setTypeEnum(TargetTypes targetType);

	public abstract void setType(String targetType);

	public abstract String getType();
	
	public String getInternalType();

	public void setInternalType(String internalType);
	
	public boolean equalsContent(Object other);	
}
