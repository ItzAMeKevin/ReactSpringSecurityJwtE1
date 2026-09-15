package com.lacouf.rsbjwt.model.auth;

import java.util.HashSet;
import java.util.Set;

public enum Role{
	MANAGER("ROLE_MANAGER"),
	EMPLOYER("ROLE_EMPLOYER"),
	STUDENT("ROLE_STUDENT"),
	;

	private final String string;
	private final Set<Role> managedRoles = new HashSet<>();

	static{
		MANAGER.managedRoles.add(EMPLOYER);
		MANAGER.managedRoles.add(STUDENT);
	}

	Role(String string){
		this.string = string;
	}

	@Override
	public String toString(){
		return string;
	}

}
