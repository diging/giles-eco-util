package edu.asu.diging.gilesecosystem.util.users;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;

import edu.asu.diging.gilesecosystem.util.exceptions.BadPasswordException;
import edu.asu.diging.gilesecosystem.util.exceptions.UnauthorizedException;

/**
 * Class to manage admin users.
 * 
 * @author jdamerow
 *
 */
public interface IAdminUserManager {

    /**
     * Update password of a admin user. It is assumed that only the admin user
     * themselves can update their password. Hence, the old password needs to be provided.
     * If it is required to completely reset a password, this should be done
     * directly on the server.
     * @param username Username of admin user whose password should be changed.
     * @param newPassword New Password in plaintext.
     * @param oldPassword Old password
     * @param role role of user
     * @return true if password was succesfully changed; otherwise false.
     * @throws BadPasswordException Thrown if password is empty.
     */
    public abstract boolean updatePassword(String username, String oldPassword, String newPassword, String role)
            throws BadPasswordException, UnauthorizedException;

    /**
     * Get all administrators.
     * @return list of all administrators
     */
    public abstract List<UserDetails> getAdministrators();

    /**
     * Checks if the provided password is the one stored for the user.
     * @param username Username of the administrator to check the password for.
     * @param password Password in plain text.
     * @return true if password is correct; otherwise false.
     */
    public abstract boolean isPasswordValid(String username, String password);

}