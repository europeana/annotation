package eu.europeana.annotation.definitions.model.resource.selector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.annotation.definitions.model.resource.selector.Selector;
import eu.europeana.annotation.definitions.model.selector.shape.Point;
import eu.europeana.annotation.definitions.model.selector.shape.impl.PointImpl;
import eu.europeana.annotation.definitions.model.vocabulary.SelectorTypes;

public abstract class BaseSvgSelector implements Selector{

	private String selectorType;
	private Point origin;
	private List<Point> points;
	private Map<String, Integer> dimensionMap;
	
	/* Style elements */
	String cssClass;
	String style;
	

	@Override
	public String getSelectorType() {
		return selectorType;
	}

	@Override
	public void setSelectorType(String selectorType) {
		this.selectorType = selectorType;
	}
	
	@Override
	public void setSelectorType(SelectorTypes selectorType) {
		this.selectorType = selectorType.name();
	}

	@Override
	public List<Point> getPoints() {
		return points;
	}

	@Override
	public void setPoints(List<Point> points) {
		this.points = points;
	}

	@Override
	public Point getOrigin() {
		return origin;
	}

	@Override
	public void setOrigin(Point origin) {
		this.origin = origin;
	}

	@Override
	public Map<String, Integer> getDimensionMap() {
		return dimensionMap;
	}

	@Override
	public void setDimensionMap(Map<String, Integer> dimensionMap) {
		this.dimensionMap = dimensionMap;
	}

	@Override
	public String getCssClass() {
		return cssClass;
	}

	@Override
	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	@Override
	public String getStyle() {
		return style;
	}

	@Override
	public void setStyle(String style) {
		this.style = style;
	}
	
	protected BaseSvgSelector(){
		origin = new PointImpl(0, 0);
		dimensionMap = new HashMap<String, Integer>(8);
		if(hasPoints())
			points = new ArrayList<Point>();
	}

	/**
	 * To be overwritten by selector that define additional points to the Origin like Poligons or Polilines 
	 * @return
	 */
	private boolean hasPoints() {
		return false;
	}
}
