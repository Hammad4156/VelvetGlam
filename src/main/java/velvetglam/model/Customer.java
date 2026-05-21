package velvetglam.model;

import java.time.LocalDate;

/**
 * Represents a registered VelvetGlam customer.
 *
 * OOP concepts demonstrated:
 *  - Encapsulation : all fields are private; access only via getters/setters
 *  - Exception Handling : constructor and setters validate input and throw
 *                          IllegalArgumentException for invalid data
 */
public class Customer {

    // ── Private Fields (Encapsulation) ───────────────────────
    private int       customerId;
    private String    name;
    private String    contact;
    private String    email;
    private int       loyaltyPoints;
    private LocalDate dateRegistered;

    /** Points earned per PKR 100 spent. */
    public static final int POINTS_PER_100 = 1;

    // ── Constructor ──────────────────────────────────────────
    public Customer(int customerId, String name, String contact, String email,
                    int loyaltyPoints, LocalDate dateRegistered) {

        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Customer name cannot be empty.");
        if (loyaltyPoints < 0)
            throw new IllegalArgumentException("Loyalty points cannot be negative.");

        this.customerId     = customerId;
        this.name           = name.trim();
        this.contact        = (contact != null)  ? contact.trim()  : "";
        this.email          = (email   != null)  ? email.trim()    : "";
        this.loyaltyPoints  = loyaltyPoints;
        this.dateRegistered = (dateRegistered != null) ? dateRegistered : LocalDate.now();
    }

    // ── Business Logic ───────────────────────────────────────

    /**
     * Calculates how many loyalty points a customer earns for a given spend.
     * 1 point is earned for every PKR 100 spent.
     */
    public static int calculateEarnedPoints(double totalSpent) {
        return (int) (totalSpent / 100.0) * POINTS_PER_100;
    }

    // ── Getters ──────────────────────────────────────────────
    public int       getCustomerId()     { return customerId; }
    public String    getName()           { return name; }
    public String    getContact()        { return contact; }
    public String    getEmail()          { return email; }
    public int       getLoyaltyPoints()  { return loyaltyPoints; }
    public LocalDate getDateRegistered() { return dateRegistered; }

    // ── Setters ──────────────────────────────────────────────
    public void setCustomerId(int id) { this.customerId = id; }

    public void setName(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Customer name cannot be empty.");
        this.name = name.trim();
    }

    public void setContact(String contact)  { this.contact = (contact != null) ? contact.trim() : ""; }
    public void setEmail(String email)      { this.email   = (email   != null) ? email.trim()   : ""; }

    public void setLoyaltyPoints(int pts) {
        if (pts < 0)
            throw new IllegalArgumentException("Loyalty points cannot be negative.");
        this.loyaltyPoints = pts;
    }

    public void setDateRegistered(LocalDate d) { this.dateRegistered = d; }

    /** Used by JComboBox to display customer name and contact. */
    @Override
    public String toString() {
        return name + (contact.isEmpty() ? "" : "  (" + contact + ")");
    }
}
