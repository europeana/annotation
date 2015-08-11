package eu.europeana.annotation.definitions.model.resource.selector;

public interface TextPositionSelector extends Selector {

	public static String DIMMENSION_START_POSITION_NR = "startPositionNr";
	public static String DIMMENSION_END_POSITION_NR = "endPosition";
	
	
	public abstract void setStartPosition(Integer start);

	public abstract Integer getStartPosition();

	public abstract void setEndPosition(Integer end);

	public abstract Integer getEndPosition();
	
}
