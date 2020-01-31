package eu.europeana.annotation.web.model.vocabulary;

public interface Operations extends eu.europeana.api.commons.web.model.vocabulary.Operations {

	//feedback
	public static final String REPORT = "report";
	
	//admin
	public static final String ADMIN_UNLOCK = "admin_unlock";
	public static final String ADMIN_REINDEX = "admin_reindex"; 
	
	//moderation
	public static final String MODERATION_ALL = "moderation_all";
	
	//whitelist
	public static final String WHITELIST_ALL = "whitelist_all";
	public static final String WHITELIST_RETRIEVE = "whitelist_retrieve";
	public static final String WHITELIST_CREATE = "whitelist_create";
	public static final String WHITELIST_UPDATE = "whitelist_update";
	public static final String WHITELIST_DELETE = "whitelist_delete";
	
}
