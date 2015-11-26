package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.WhitelistStatus;
import eu.europeana.annotation.definitions.model.whitelist.Whitelist;

public class WhitelistObjectFactory extends AbstractModelObjectFactory<Whitelist, WhitelistStatus>{

	private static WhitelistObjectFactory singleton;

	//force singleton usage
	private WhitelistObjectFactory(){};
	
	public static WhitelistObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (WhitelistObjectFactory.class) {
				singleton = new WhitelistObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Class<? extends Whitelist> getClassForType(Enum<WhitelistStatus> modelType) {
		return eu.europeana.annotation.definitions.model.whitelist.BaseWhitelist.class;
	}

	@Override
	public Class<WhitelistStatus> getEnumClass() {
		return WhitelistStatus.class;
	}

	
}
