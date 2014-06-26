package eu.europeana.annotation.definitions.model.resource.selector;

import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;

public interface Selector{

	public abstract void setDimensionMap(Map<String, Integer> dimensionMap);

	public abstract Map<String, Integer> getDimensionMap();

	public abstract void setOrigin(Point origin);

	public abstract Point getOrigin();

	public abstract void setPoints(List<Point> points);

	public abstract List<Point> getPoints();

	public abstract void setSelectorType(SelectorTypes selectorType);

	public abstract void setSelectorType(String selectorType);

	public abstract String getSelectorType();

	public abstract void setStyle(String style);

	public abstract String getStyle();

	public abstract void setCssClass(String cssClass);

	public abstract String getCssClass();

}
