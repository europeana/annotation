package eu.europeana.annotation.definitions.model.target;

import eu.europeana.annotation.definitions.model.resource.ResourceDescription;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public interface Target extends SpecificResource, ResourceDescription{

	public abstract void setTypeEnum(TargetTypes targetType);

	public boolean equalsContent(Object other);	
}
