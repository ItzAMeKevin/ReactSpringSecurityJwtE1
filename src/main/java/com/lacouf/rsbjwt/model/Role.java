package com.lacouf.rsbjwt.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

	@JsonValue
	public String getString() {
		return string;
	}

	@JsonCreator
	public static Role fromString(String value) {
		for (Role r : values()) {
			if (r.string.equals(value) || r.name().equals(value)) {
				return r;
			}
		}
		throw new IllegalArgumentException("Unknown role: " + value);
	}

	@Override
	public String toString(){
		return string;
	}

}
