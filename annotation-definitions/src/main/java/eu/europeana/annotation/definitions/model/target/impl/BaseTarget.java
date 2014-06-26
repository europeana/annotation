package eu.europeana.annotation.definitions.model.target.impl;

import eu.europeana.annotation.definitions.model.resource.impl.BaseInternetResource;
import eu.europeana.annotation.definitions.model.target.Target;
import eu.europeana.annotation.definitions.model.vocabulary.TargetTypes;

public class BaseTarget extends BaseInternetResource implements Target {
 
	private String targetType;
	private String europeanaId;
	

	@Override
	public String getTargetType() {
		return targetType;
	}

	@Override
	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}
	
	@Override
	public void setTargetType(TargetTypes targetType) {
		this.targetType = targetType.name();
	}
	
	public BaseTarget(){}
	
	public BaseTarget(String targetType){
		super();
		setTargetType(targetType);
	}

	public BaseTarget(TargetTypes targetType){
		super();
		setTargetType(targetType);
	}
	@Override
	public String getEuropeanaId() {
		return europeanaId;
	}

	@Override
	public void setEuropeanaId(String europeanaId) {
		this.europeanaId = europeanaId;
	};
}

