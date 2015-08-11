package eu.europeana.annotation.definitions.model.resource.selector.impl;

import eu.europeana.annotation.definitions.model.resource.selector.CircleSelector;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;

public class SvgCircleSelector extends BaseSvgSelector implements CircleSelector {

	public static final String DIMENSION_R = "r";
	
	@Override
	public Integer getCx() {
		return getOrigin().getPosX();
	}
	
	@Override
	public void setCx(Integer cx) {
		if(getOrigin() == null)
			setOrigin(new PointImpl(0, 0));
		getOrigin().setPosX(cx);
	}
	
	@Override
	public Integer getCy() {
		return getOrigin().getPosY();
	}
	@Override
	public void setCy(Integer cy) {
		if(getOrigin() == null)
			setOrigin(new PointImpl(0, 0));
		getOrigin().setPosY(cy);
	}
	
	@Override
	public Integer getR() {
		return getDimensionMap().get(DIMENSION_R);
	}
	@Override
	public void setR(Integer r) {
		getDimensionMap().put(DIMENSION_R, r);
	}
	
	
}
