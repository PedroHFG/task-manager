package com.dev.taskmanager.projections;

public interface UserProjection {
    Long getId();
    String getEmail();
    String getName();
    String getPassword();
    Long getRoleId();
    String getAuthority();
}
