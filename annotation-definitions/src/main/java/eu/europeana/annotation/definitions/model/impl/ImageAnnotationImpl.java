package eu.europeana.annotation.definitions.model.impl;

import java.util.List;

import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.shape.Point;

public class ImageAnnotationImpl extends SemanticTagImpl implements ImageAnnotation {


	public ImageAnnotationImpl(){
		super();
		setType(AnnotationTypes.IMAGE_ANNOTATION.toString());
	}
	
	private String imageUrl;
	private String text;
	private List<Point> shape;
	
	@Override
	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
		
	}

	@Override
	public List<Point> getShape() {
		return shape;
	}

	@Override
	public void setShape(List<Point> shape) {
		this.shape = shape;
		
	}

//	public void setPointList(List<Point> pointList) {
//		this.shape = pointList;
//		
//	}
	
}
