package edu.asu.diging.gilesecosystem.util.users.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import edu.asu.diging.gilesecosystem.util.exceptions.BadPasswordException;
import edu.asu.diging.gilesecosystem.util.exceptions.UnauthorizedException;
import edu.asu.diging.gilesecosystem.util.users.IAdminUserDetailsService;
import edu.asu.diging.gilesecosystem.util.users.IAdminUserManager;

/**
 * Manager class for admin users.
 * 
 * @author Julia Damerow
 *
 */
@Service
public class AdminUserManager implements IAdminUserManager {

    @Autowired
    @Qualifier("adminDetailsService")
    private IAdminUserDetailsService adminDetailsService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.gilesecosystem.web.users.IAdminUserManager#getAdministrators
     * ()
     */
    @Override
    public List<UserDetails> getAdministrators() {
        return adminDetailsService.getAllAdmins();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.gilesecosystem.web.users.IAdminUserManager#isPasswordValid
     * (java.lang.String, java.lang.String)
     */
    @Override
    public boolean isPasswordValid(String username, String password) {
        return adminDetailsService.isPasswordValid(username, password);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.gilesecosystem.web.users.IAdminUserManager#updatePassword
     * (java.lang.String, java.lang.String)
     */
    @Override
    public boolean updatePassword(String username, String oldPassword, String newPassword, String role)
            throws BadPasswordException, UnauthorizedException {
        boolean valid = isPasswordValid(username, oldPassword);
        if (!valid) {
            throw new UnauthorizedException("Provided password is not valid.");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new BadPasswordException("Password cannot be empty.");
        }
        return adminDetailsService.changePassword(username, newPassword, role);
    }

}
