package eu.europeana.annotation.definitions.model;

import java.util.List;

public interface SemanticTag extends Annotation {

	public abstract List<String> getNamedEntityIdList();

	public abstract void setNamedEntityIdList(List<String> namedEntityIdList);

	public abstract List<String> getNamedEntityLabelList();

	public abstract void setNamedEntityLabelList(
			List<String> namedEntityLabelList);

	public abstract String getLabel();

	public abstract void setLabel(String label);

}