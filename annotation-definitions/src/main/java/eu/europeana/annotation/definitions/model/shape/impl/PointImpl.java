package eu.europeana.annotation.definitions.model.shape.impl;

import eu.europeana.annotation.definitions.model.shape.Point;

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
	
	public Integer getPosX() {
		return posX;
	}
	public void setPosX(Integer posX) {
		this.posX = posX;
	}
	public Integer getPosY() {
		return posY;
	}
	public void setPosY(Integer posY) {
		this.posY = posY;
	}
	
}
