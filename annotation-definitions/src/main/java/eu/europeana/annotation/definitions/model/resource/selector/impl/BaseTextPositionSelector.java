package eu.europeana.annotation.definitions.model.resource.selector.impl;

import eu.europeana.annotation.definitions.model.resource.selector.TextPositionSelector;

public class BaseTextPositionSelector extends BaseSelector implements TextPositionSelector{

	
	@Override
	public void setStartPosition(Integer start) {
		getDimensionMap().put(DIMMENSION_START_POSITION_NR, start);
		
	}

	@Override
	public Integer getStartPosition() {
		return getDimensionMap().get(DIMMENSION_START_POSITION_NR);
	}

	@Override
	public void setEndPosition(Integer end) {
		getDimensionMap().put(DIMMENSION_END_POSITION_NR, end);
		
	}

	@Override
	public Integer getEndPosition() {
		return getDimensionMap().get(DIMMENSION_END_POSITION_NR);
	}

	

	

	
}
