package eu.europeana.annotation.web.model.vocabulary;

import eu.europeana.api.commons.definitions.vocabulary.Role;

public enum UserGroups implements Role {

	ANONYMOUS(new String[]{Operations.RETRIEVE}), 
	USER(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT}), 
	TESTER(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT}), 
	ADMIN(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT, Operations.ADMIN_ALL, Operations.ADMIN_UNLOCK, Operations.ADMIN_REINDEX, Operations.WHITELIST_ALL, Operations.WHITELIST_CREATE, Operations.WHITELIST_RETRIEVE, Operations.WHITELIST_DELETE}), 
	MODERATOR(new String[]{Operations.MODERATION_ALL});
	
	String[] operations;
	
	UserGroups (String[] operations){
		this.operations = operations;
	}
	
	public String[] getOperations() {
		return operations;
	}
	
	public void setOperations(String[] operations) {
		this.operations = operations;
	}

	/**
	 * This method returns the api specific Role for the given role name
	 * 
	 * @param name the name of user role 
	 * @return the user role
	 */
	public static Role getRoleByName(String name) {
	    Role userRole = null;
	    for(UserGroups role : UserGroups.values()) {
		if(role.name().toLowerCase().equals(name)) {
		    userRole = role;
		    break;
		}
	    }
	    return userRole;
	}

	@Override
	public String getName() {
	    return this.name();
	}

	@Override
	public String[] getPermissions() {
	    return getOperations();
	}
			
}
