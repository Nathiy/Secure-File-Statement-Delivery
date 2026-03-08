package com.securestatements.model;

import java.sql.Timestamp;

/**
 * User Interface - Represents a user in the system (Admin or Customer)
 *
 * This interface defines the common contract for all user types in the
 * Secure File Statement Delivery system.
 */
public interface User {

    /**
     * Gets the user's unique identifier
     * @return User ID
     */
    int getId();

    /**
     * Gets the user's login username
     * @return Username
     */
    String getUsername();

    /**
     * Gets the user's email address
     * @return Email address
     */
    String getEmail();

    /**
     * Gets the user's full name
     * @return Full name
     */
    String getFullName();

    /**
     * Gets whether the user account is active
     * @return True if active, false if disabled
     */
    boolean isActive();

    /**
     * Gets the user's account creation timestamp
     * @return Creation timestamp
     */
    Timestamp getCreatedAt();

    /**
     * Gets the user's last login timestamp
     * @return Last login timestamp, or null if never logged in
     */
    Timestamp getLastLogin();

    /**
     * Gets the IP address of the user's last login
     * @return Last login IP address
     */
    String getLastLoginIp();

    /**
     * Gets the number of failed login attempts
     * @return Failed login attempts count
     */
    int getLoginAttempts();

    /**
     * Gets the timestamp until which the account is locked
     * @return Lock timestamp, or null if not locked
     */
    Timestamp getLockedUntil();

    /**
     * Gets the type of user (admin or customer)
     * @return User type
     */
    String getUserType();

    /**
     * Checks if the user's account is currently locked
     * @return True if locked, false otherwise
     */
    boolean isLocked();

    /**
     * Setters for user properties
     */
    void setId(int id);
    void setUsername(String username);
    void setEmail(String email);
    void setFullName(String fullName);
    void setActive(boolean active);
    void setCreatedAt(Timestamp createdAt);
    void setLastLogin(Timestamp lastLogin);
    void setLastLoginIp(String lastLoginIp);
    void setLoginAttempts(int loginAttempts);
    void setLockedUntil(Timestamp lockedUntil);
}

