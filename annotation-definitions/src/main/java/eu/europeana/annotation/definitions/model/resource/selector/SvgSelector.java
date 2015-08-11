package eu.europeana.annotation.definitions.model.resource.selector;

import java.util.List;

import eu.europeana.annotation.definitions.model.selector.shape.Point;

public interface SvgSelector extends Selector{

	public abstract void setOrigin(Point origin);

	public abstract Point getOrigin();

	public abstract void setPoints(List<Point> points);

	public abstract List<Point> getPoints();

	public abstract void setStyle(String style);

	public abstract String getStyle();

	public abstract void setCssClass(String cssClass);

	public abstract String getCssClass();
}
