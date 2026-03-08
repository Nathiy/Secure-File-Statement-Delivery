package com.securestatements.model;

import java.sql.Timestamp;

/**
 * Admin Class - Represents an administrator user in the system
 *
 * Administrators have the ability to:
 * - Upload statements for customers
 * - Generate download links
 * - Manage user accounts
 * - View audit logs
 */
public class Admin implements User {

    private int id;
    private String username;
    private String email;
    private String fullName;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    private String lastLoginIp;
    private int loginAttempts;
    private Timestamp lockedUntil;

    // Constructors

    /**
     * Default constructor
     */
    public Admin() {
    }

    /**
     * Constructor with basic info
     */
    public Admin(int id, String username, String email, String fullName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.isActive = true;
    }

    /**
     * Constructor with full details
     */
    public Admin(int id, String username, String email, String fullName, boolean isActive,
                 Timestamp createdAt, Timestamp lastLogin, String lastLoginIp,
                 int loginAttempts, Timestamp lockedUntil) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.lastLoginIp = lastLoginIp;
        this.loginAttempts = loginAttempts;
        this.lockedUntil = lockedUntil;
    }

    // Getters

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    @Override
    public Timestamp getLastLogin() {
        return lastLogin;
    }

    @Override
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    @Override
    public int getLoginAttempts() {
        return loginAttempts;
    }

    @Override
    public Timestamp getLockedUntil() {
        return lockedUntil;
    }

    @Override
    public String getUserType() {
        return "admin";
    }

    @Override
    public boolean isLocked() {
        if (lockedUntil == null) {
            return false;
        }
        return lockedUntil.getTime() > System.currentTimeMillis();
    }

    // Setters

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    @Override
    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    @Override
    public void setLockedUntil(Timestamp lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    // Object methods

    @Override
    public String toString() {
        return "Admin{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                ", lastLoginIp='" + lastLoginIp + '\'' +
                ", loginAttempts=" + loginAttempts +
                ", lockedUntil=" + lockedUntil +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Admin admin = (Admin) o;
        return id == admin.id &&
                username.equals(admin.username);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, username);
    }
}

