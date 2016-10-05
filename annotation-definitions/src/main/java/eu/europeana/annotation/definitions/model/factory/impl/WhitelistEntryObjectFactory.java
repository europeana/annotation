package eu.europeana.annotation.definitions.model.factory.impl;

import eu.europeana.annotation.definitions.model.factory.AbstractModelObjectFactory;
import eu.europeana.annotation.definitions.model.vocabulary.WhitelistStatus;
import eu.europeana.annotation.definitions.model.whitelist.WhitelistEntry;

public class WhitelistEntryObjectFactory extends AbstractModelObjectFactory<WhitelistEntry, WhitelistStatus>{

	private static WhitelistEntryObjectFactory singleton;

	//force singleton usage
	private WhitelistEntryObjectFactory(){};
	
	public static WhitelistEntryObjectFactory getInstance() {

		if (singleton == null) {
			synchronized (WhitelistEntryObjectFactory.class) {
				singleton = new WhitelistEntryObjectFactory();
			}
		}

		return singleton;

	}

	@Override
	public Class<? extends WhitelistEntry> getClassForType(Enum<WhitelistStatus> modelType) {
		return eu.europeana.annotation.definitions.model.whitelist.BaseWhitelistEntry.class;
	}

	@Override
	public Class<WhitelistStatus> getEnumClass() {
		return WhitelistStatus.class;
	}

	
}
