package eu.europeana.annotation.definitions.model;

import java.util.List;

import eu.europeana.annotation.definitions.model.selector.shape.Point;

public interface ImageAnnotation extends Annotation{

	public abstract String getImageUrl();

	public abstract void setImageUrl(String imageUrl);

	public abstract String getText();

	public abstract void setText(String text);

	public abstract List<Point> getShape();

	public abstract void setShape(List<Point> pointList);
}