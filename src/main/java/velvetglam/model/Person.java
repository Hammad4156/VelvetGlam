package velvetglam.model;

/**
 * Abstract base class for all people in the VelvetGlam system.
 *
 * OOP Concepts Demonstrated:
 *  - Abstraction    : Person is abstract — cannot be instantiated directly.
 *                     Defines common fields (name, contact) without full implementation.
 *  - Inheritance    : Staff extends Person; Manager and Cashier extend Staff.
 *  - Encapsulation  : All fields are private with protected getters/setters.
 */
public abstract class Person {

    // ── Private Fields (Encapsulation) ───────────────────────
    private String name;
    private String contact;

    // ── Constructor ──────────────────────────────────────────
    protected Person(String name, String contact) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name    = name.trim();
        this.contact = (contact != null) ? contact.trim() : "";
    }

    // ── Abstract Method ──────────────────────────────────────
    /**
     * Returns a list of module/feature names this person is permitted to access.
     * Overridden differently by Manager and Cashier (Polymorphism).
     */
    public abstract String[] getPermissions();

    // ── Getters ──────────────────────────────────────────────
    public String getName()    { return name; }
    public String getContact() { return contact; }

    // ── Setters ──────────────────────────────────────────────
    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty.");
        this.name = name.trim();
    }

    public void setContact(String contact) {
        this.contact = (contact != null) ? contact.trim() : "";
    }

    @Override
    public String toString() {
        return name;
    }
}
