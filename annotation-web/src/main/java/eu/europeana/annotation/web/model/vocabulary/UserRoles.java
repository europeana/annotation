package eu.europeana.annotation.web.model.vocabulary;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import eu.europeana.api.commons.definitions.vocabulary.Role;
import static eu.europeana.annotation.web.model.vocabulary.Operations.*;

public enum UserRoles implements Role {

	anonimous(new String[]{RETRIEVE}), 
	user(new String[]{RETRIEVE, CREATE, DELETE, UPDATE, REPORT}), 
	tester(new String[]{RETRIEVE, CREATE, DELETE, UPDATE, REPORT}), 
	admin(new String[]{RETRIEVE, CREATE, DELETE, UPDATE, REPORT, 
	    ADMIN_ALL, WRITE_UNLOCK, WRITE_LOCK, 
	    ADMIN_REINDEX, WHITELIST_ALL, 
	    WHITELIST_CREATE, WHITELIST_RETRIEVE, WHITELIST_DELETE}), 
	moderator(new String[]{MODERATION_ALL}),
	publisher(new String[]{RETRIEVE, CREATE, DELETE, UPDATE, REPORT});
	
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
