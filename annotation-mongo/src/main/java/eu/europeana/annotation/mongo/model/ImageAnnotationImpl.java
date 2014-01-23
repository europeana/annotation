package eu.europeana.annotation.mongo.model;

import java.util.List;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Polymorphic;

import eu.europeana.annotation.definitions.model.AnnotationId;
import eu.europeana.annotation.definitions.model.AnnotationTypes;
import eu.europeana.annotation.definitions.model.ImageAnnotation;
import eu.europeana.annotation.definitions.model.shape.Point;

@Entity("annotation")
@Polymorphic
public class ImageAnnotationImpl extends SemanticTagImpl implements ImageAnnotation {

	private String imageUrl;
	private String text;
	@Embedded("poligonshape")
	private List<Point> pointList;
	
	public ImageAnnotationImpl() {
		super();
		setType(AnnotationTypes.IMAGE_ANNOTATION.toString());
	}
	
	public ImageAnnotationImpl(AnnotationId annotationId) {
		super(annotationId);
		setType(AnnotationTypes.IMAGE_ANNOTATION.toString());
	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.ImageAnnotation#getImageUrl()
	 */
	@Override
	public String getImageUrl() {
		return imageUrl;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.ImageAnnotation#setImageUrl(java.lang.String)
	 */
	@Override
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.ImageAnnotation#getText()
	 */
	@Override
	public String getText() {
		return text;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.annotation.model.ImageAnnotation#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.text = text;
	}

	public List<Point> getPointList() {
		return pointList;
	}

	public void setPointList(List<Point> pointList) {
		this.pointList = pointList;
	}
	
	@Override
	public List<Point> getShape() {
		return getPointList();
	}

	@Override
	public void setShape(List<Point> pointList) {
		setPointList(pointList);
		
	}
	
	
}
