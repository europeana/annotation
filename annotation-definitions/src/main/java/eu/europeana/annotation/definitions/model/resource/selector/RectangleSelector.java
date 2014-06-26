package eu.europeana.annotation.definitions.model.resource.selector;

public interface RectangleSelector extends Selector {

	public static final String DIMENSION_WIDTH = "width";
	public static final String DIMENSION_HEIGHT = "height";
	public static final String DIMENSION_RX = "rx";
	public static final String DIMENSION_RY = "ry";
	public abstract void setRy(Integer ry);
	public abstract Integer getRy();
	public abstract void setRx(Integer rx);
	public abstract Integer getRx();
	public abstract void setHeight(Integer height);
	public abstract Integer getHeight();
	public abstract void setWidth(Integer width);
	public abstract Integer getWidth();
	public abstract void setY(Integer y);
	public abstract Integer getY();
	public abstract void setX(Integer x);
	public abstract Integer getX();
	
}
