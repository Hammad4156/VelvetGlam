package velvetglam.model;

/**
 * Represents a login account linked to a staff member.
 *
 * OOP Concepts Demonstrated:
 *  - Encapsulation  : username and password are private and never exposed directly.
 *                     Password is stored as-is (plain text for this lab scope);
 *                     in production it should be hashed with BCrypt.
 *
 * Maps to: users table in velvetglam_db
 */
public class UserAccount {

    // ── Private Fields (Encapsulation) ───────────────────────
    private int    userId;
    private String username;
    private String password;   // stored plain-text for OOP lab; hash in production
    private String role;       // "Manager" or "Cashier" — mirrors staff.role
    private int    staffId;    // FK → staff.staff_id

    // ── Constructor ──────────────────────────────────────────
    public UserAccount(int userId, String username, String password,
                       String role, int staffId) {

        if (username == null || username.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        if (role == null || role.isBlank())
            throw new IllegalArgumentException("Role cannot be empty.");

        this.userId   = userId;
        this.username = username.trim();
        this.password = password;
        this.role     = role;
        this.staffId  = staffId;
    }

    // ── Login Validation ─────────────────────────────────────
    /**
     * Returns true if the supplied plain-text password matches this account.
     * (For a real system, compare against a BCrypt hash here.)
     */
    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getUserId()   { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }   // needed by DAO insert
    public String getRole()     { return role; }
    public int    getStaffId()  { return staffId; }

    // ── Setters ──────────────────────────────────────────────
    public void setUserId(int id)       { this.userId = id; }
    public void setUsername(String u)   {
        if (u == null || u.isBlank())
            throw new IllegalArgumentException("Username cannot be empty.");
        this.username = u.trim();
    }
    public void setPassword(String p)   {
        if (p == null || p.isBlank())
            throw new IllegalArgumentException("Password cannot be empty.");
        this.password = p;
    }
    public void setRole(String role)    { this.role = role; }
    public void setStaffId(int sid)     { this.staffId = sid; }

    @Override
    public String toString() {
        return username + "  (" + role + ")";
    }
}
