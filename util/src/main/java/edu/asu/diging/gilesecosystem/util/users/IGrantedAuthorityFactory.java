package edu.asu.diging.gilesecosystem.util.users;

import org.springframework.security.core.GrantedAuthority;

public interface IGrantedAuthorityFactory {
    
    GrantedAuthority createGrantedAuthority(String role);

}
