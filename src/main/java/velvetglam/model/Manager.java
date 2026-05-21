package velvetglam.model;

/**
 * Manager — a Staff member with full system access.
 *
 * OOP Concepts Demonstrated:
 *  - Inheritance    : extends Staff, which extends Person (3-level hierarchy)
 *  - Polymorphism   : overrides getPermissions() to return full access list
 */
public class Manager extends Staff {

    public Manager(int staffId, String name, String contact, double salary) {
        super(staffId, name, Staff.ROLE_MANAGER, contact, salary);
    }

    /**
     * Managers have unrestricted access to all four modules plus the dashboard.
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
            "Products & Inventory",
            "Customer Management",
            "Sales & Billing",
            "Staff Management",
            "Dashboard"
        };
    }
}
