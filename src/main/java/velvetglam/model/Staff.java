package velvetglam.model;

/**
 * Represents a VelvetGlam staff member.
 *
 * OOP Concepts Demonstrated:
 *  - Inheritance    : extends Person (inherits name, contact, getPermissions)
 *  - Encapsulation  : staffId, role, salary are private with getters/setters
 *  - Exception Handling : constructor and setters throw IllegalArgumentException
 *                         for invalid inputs (negative salary, blank role, etc.)
 *
 * Maps to: staff table in velvetglam_db
 */
public class Staff extends Person {

    // ── Role Constants ───────────────────────────────────────
    public static final String ROLE_MANAGER = "Manager";
    public static final String ROLE_CASHIER = "Cashier";

    // ── Private Fields (Encapsulation) ───────────────────────
    private int    staffId;
    private String role;       // "Manager" or "Cashier"
    private double salary;

    // ── Constructor ──────────────────────────────────────────
    public Staff(int staffId, String name, String role,
                 String contact, double salary) {
        super(name, contact);

        if (role == null || role.isBlank())
            throw new IllegalArgumentException("Role cannot be empty.");
        if (!role.equals(ROLE_MANAGER) && !role.equals(ROLE_CASHIER))
            throw new IllegalArgumentException("Role must be 'Manager' or 'Cashier'.");
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative.");

        this.staffId = staffId;
        this.role    = role;
        this.salary  = salary;
    }

    // ── Polymorphism: getPermissions() ───────────────────────
    /**
     * Returns permissions based on role.
     * Overriding happens in Manager and Cashier subclasses.
     * Staff itself provides a default by delegating to role string.
     */
    @Override
    public String[] getPermissions() {
        if (ROLE_MANAGER.equals(role)) {
            return new String[]{
                "Products & Inventory",
                "Customer Management",
                "Sales & Billing",
                "Staff Management",
                "Dashboard"
            };
        } else {
            // Cashier
            return new String[]{
                "Customer Management",
                "Sales & Billing"
            };
        }
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getStaffId() { return staffId; }
    public String getRole()    { return role; }
    public double getSalary()  { return salary; }

    // ── Setters ──────────────────────────────────────────────
    public void setStaffId(int id) { this.staffId = id; }

    public void setRole(String role) {
        if (role == null || role.isBlank())
            throw new IllegalArgumentException("Role cannot be empty.");
        if (!role.equals(ROLE_MANAGER) && !role.equals(ROLE_CASHIER))
            throw new IllegalArgumentException("Role must be 'Manager' or 'Cashier'.");
        this.role = role;
    }

    public void setSalary(double salary) {
        if (salary < 0)
            throw new IllegalArgumentException("Salary cannot be negative.");
        this.salary = salary;
    }

    /** Display string used in JComboBox / JTable. */
    @Override
    public String toString() {
        return getName() + "  [" + role + "]";
    }
}
