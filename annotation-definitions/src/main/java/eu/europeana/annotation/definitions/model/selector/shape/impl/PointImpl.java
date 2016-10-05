package eu.europeana.annotation.definitions.model.selector.shape.impl;

import eu.europeana.annotation.definitions.model.selector.shape.Point;

public class PointImpl  implements Point{

	private Integer posX;
	private Integer posY;
	
	public PointImpl(){
		super();
	}
	
	public PointImpl(Integer posX, Integer posY){
		super();
		this.posX = posX;
		this.posY = posY;
	}
	
	@Override
	public Integer getPosX() {
		return posX;
	}
	@Override
	public void setPosX(Integer posX) {
		this.posX = posX;
	}
	@Override
	public Integer getPosY() {
		return posY;
	}
	@Override
	public void setPosY(Integer posY) {
		this.posY = posY;
	}
	
}
