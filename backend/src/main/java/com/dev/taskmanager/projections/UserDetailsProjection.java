package com.dev.taskmanager.projections;

public interface UserDetailsProjection {

    String getUsername();
	String getPassword();
	Long getRoleId();
	String getAuthority();
}
