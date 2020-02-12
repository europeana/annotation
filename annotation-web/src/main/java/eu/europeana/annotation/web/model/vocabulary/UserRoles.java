package eu.europeana.annotation.web.model.vocabulary;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import eu.europeana.api.commons.definitions.vocabulary.Role;

public enum UserRoles implements Role {

	anonimous(new String[]{Operations.RETRIEVE}), 
	user(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT}), 
	tester(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT}), 
	admin(new String[]{Operations.RETRIEVE, Operations.CREATE, Operations.DELETE, Operations.UPDATE, Operations.REPORT, Operations.ADMIN_ALL, Operations.ADMIN_UNLOCK, Operations.ADMIN_REINDEX, Operations.WHITELIST_ALL, Operations.WHITELIST_CREATE, Operations.WHITELIST_RETRIEVE, Operations.WHITELIST_DELETE}), 
	moderator(new String[]{Operations.MODERATION_ALL});
	
	String[] operations;
	static Set<String> operationSet;
	
	UserRoles (String[] operations){
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
	    for(UserRoles role : UserRoles.values()) {
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
	
	/**
	 * This method retrieves operations from provided roles
	 * @param roles
	 * @return a set of supported operations
	 */
	public static Set<String> getPermissionSet(List<String> roles) {
		if (operationSet == null || operationSet.isEmpty()) {
			for (String role : roles) {
				Role userGroup = getRoleByName(role);
				operationSet.addAll(Arrays.asList(userGroup.getPermissions()));			
				break;
			}
		}
		return operationSet;
	}
}
