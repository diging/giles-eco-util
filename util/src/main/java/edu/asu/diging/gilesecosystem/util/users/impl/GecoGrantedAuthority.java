package edu.asu.diging.gilesecosystem.util.users.impl;

import org.springframework.security.core.GrantedAuthority;

public class GecoGrantedAuthority implements GrantedAuthority {
    private static final long serialVersionUID = 711167440813692597L;

    public final static String ROLE_USER = "ROLE_USER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";

    private String roleName;

    public GecoGrantedAuthority(String name) {
        this.roleName = name;
    }

    public GecoGrantedAuthority() {}
 
    public String getAuthority() {
        return roleName;
    }

    public void setAuthority(String rolename) {
        this.roleName = rolename;
    }
}
