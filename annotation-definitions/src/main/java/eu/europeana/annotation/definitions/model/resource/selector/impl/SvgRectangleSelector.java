package eu.europeana.annotation.definitions.model.resource.selector.impl;

import eu.europeana.annotation.definitions.model.resource.selector.RectangleSelector;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;

public class SvgRectangleSelector extends BaseSvgSelector implements RectangleSelector{

	@Override
	public Integer getX() {
		return getOrigin().getPosX();
	}

	@Override
	public void setX(Integer x) {
		if(getOrigin() == null)
			setOrigin(new PointImpl(0, 0));
		getOrigin().setPosX(x);
	}

	@Override
	public Integer getY() {
		return getOrigin().getPosY();
	}

	@Override
	public void setY(Integer y) {
		if(getOrigin() == null)
			setOrigin(new PointImpl(0, 0));
		getOrigin().setPosY(y);
	}

	@Override
	public Integer getWidth() {
		return getDimensionMap().get(DIMENSION_WIDTH);
	}

	@Override
	public void setWidth(Integer width) {
		getDimensionMap().put(DIMENSION_WIDTH, width);
	}

	@Override
	public Integer getHeight() {
		return getDimensionMap().get(DIMENSION_HEIGHT);
	}

	@Override
	public void setHeight(Integer height) {
		getDimensionMap().put(DIMENSION_HEIGHT, height);
	}

	@Override
	public Integer getRx() {
		return getDimensionMap().get(DIMENSION_RX);
	}

	@Override
	public void setRx(Integer rx) {
		getDimensionMap().put(DIMENSION_RX, rx);
	}

	@Override
	public Integer getRy() {
		return getDimensionMap().get(DIMENSION_RY);
	}

	@Override
	public void setRy(Integer ry) {
		getDimensionMap().put(DIMENSION_RY, ry);
	}
	
}
