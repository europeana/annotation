package eu.europeana.annotation.definitions.model.body;

import eu.europeana.annotation.definitions.model.resource.ResourceDescription;
import eu.europeana.annotation.definitions.model.resource.SpecificResource;
import eu.europeana.annotation.definitions.model.vocabulary.BodyInternalTypes;

public interface Body extends SpecificResource, ResourceDescription{

	public abstract void setTypeEnum(BodyInternalTypes agentType);
	
	public boolean equalsContent(Object other);

	
}
