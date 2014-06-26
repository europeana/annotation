package eu.europeana.annotation.definitions.model.resource;

import java.util.List;
import java.util.Map;

public interface TagResource extends InternetResource{

	public abstract void setLastUpdateTimestamp(Long lastUpdateTimestamp);

	public abstract Long getLastUpdateTimestamp();

	public abstract void setCreationTimestamp(Long creationTimestamp);

	public abstract Long getCreationTimestamp();

	public abstract void setCreator(String creator);

	public abstract String getCreator();

	public abstract void setLastUpdatedBy(String lastUpdatedBy);

	public abstract String getLastUpdatedBy();

	public abstract void addToSameAs(String value);

	public abstract List<String> getSameAs();

	public abstract void setTranslations(Map<String, String> translations);

	public abstract Map<String, String> getTranslations();
}
