package velvetglam.model;

/**
 * Cashier — a Staff member with limited access (sales & customers only).
 *
 * OOP Concepts Demonstrated:
 *  - Inheritance    : extends Staff, which extends Person
 *  - Polymorphism   : overrides getPermissions() to return restricted access list
 */
public class Cashier extends Staff {

    public Cashier(int staffId, String name, String contact, double salary) {
        super(staffId, name, Staff.ROLE_CASHIER, contact, salary);
    }

    /**
     * Cashiers can only access Customer Management and Sales/Billing.
     * They cannot view or edit Products, Staff, or the full Dashboard.
     */
    @Override
    public String[] getPermissions() {
        return new String[]{
            "Customer Management",
            "Sales & Billing"
        };
    }
}
