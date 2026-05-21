package velvetglam.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a complete sales transaction at the VelvetGlam counter.
 *
 * OOP concepts demonstrated:
 *  - Composition    : a Sale is composed of List&lt;SaleItem&gt; objects
 *  - Interface      : implements the Billable interface (calculateTotal, generateReceipt)
 *  - Encapsulation  : all fields are private with getters/setters
 *  - Exception Handling : discount and items are validated
 *
 * Maps to: sales table (header) + sale_items table (line items).
 */
public class Sale implements Billable {

    // ── Private Fields (Encapsulation) ───────────────────────
    private int            saleId;
    private int            customerId;
    private String         customerName;   // populated by DAO JOIN
    private int            staffId;
    private double         discount;
    private LocalDate      saleDate;

    /** Composition: a Sale is made up of multiple SaleItem objects. */
    private List<SaleItem> items;

    /**
     * Transient field — populated by SaleDAO when reading history rows.
     * Stores the total_amount value that was saved in the database so
     * the history table can display it without re-fetching all sale_items.
     */
    private double storedTotal = -1.0;

    // ── Constructor ──────────────────────────────────────────
    public Sale(int saleId, int customerId, int staffId,
                double discount, LocalDate saleDate) {

        if (discount < 0)
            throw new IllegalArgumentException("Discount cannot be negative.");

        this.saleId      = saleId;
        this.customerId  = customerId;
        this.staffId     = staffId;
        this.discount    = discount;
        this.saleDate    = (saleDate != null) ? saleDate : LocalDate.now();
        this.items       = new ArrayList<>();
    }

    // ── Cart Management ──────────────────────────────────────

    /** Adds a line item to the cart. */
    public void addItem(SaleItem item) {
        if (item == null) throw new IllegalArgumentException("SaleItem cannot be null.");
        items.add(item);
    }

    /** Removes the item at the given zero-based index. */
    public void removeItem(int index) {
        if (index < 0 || index >= items.size())
            throw new IndexOutOfBoundsException("Invalid item index: " + index);
        items.remove(index);
    }

    /** Removes all items from the cart. */
    public void clearItems() { items.clear(); }

    // ── Billable interface ───────────────────────────────────

    /**
     * Calculates the final payable amount: subtotal − discount.
     * Never returns negative — floored at 0.
     */
    @Override
    public double calculateTotal() {
        return Math.max(0.0, getSubtotal() - discount);
    }

    /**
     * Generates a formatted receipt string ready for display in a dialog
     * or for printing.
     */
    @Override
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════╗\n");
        sb.append("║       V E L V E T G L A M    ║\n");
        sb.append("║   Smart Cosmetics Store       ║\n");
        sb.append("╚══════════════════════════════╝\n");
        sb.append(String.format("Date     : %s%n",   saleDate));
        sb.append(String.format("Sale #   : %d%n",   saleId));
        if (customerName != null && !customerName.isBlank())
            sb.append(String.format("Customer : %s%n", customerName));
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s %4s %10s%n", "Product", "Qty", "Amount"));
        sb.append("--------------------------------\n");
        for (SaleItem item : items) {
            sb.append(String.format("%-20s %4d %10.2f%n",
                truncate(item.getProductName(), 20),
                item.getQuantity(),
                item.getLineTotal()));
        }
        sb.append("--------------------------------\n");
        sb.append(String.format("%-20s      %10.2f%n", "Subtotal (PKR):",  getSubtotal()));
        sb.append(String.format("%-20s      %10.2f%n", "Discount (PKR):",  discount));
        sb.append("================================\n");
        sb.append(String.format("%-20s      %10.2f%n", "TOTAL (PKR):", calculateTotal()));
        sb.append("================================\n");
        sb.append(String.format("Points Earned: +%d pts%n",
            Customer.calculateEarnedPoints(calculateTotal())));
        sb.append("--------------------------------\n");
        sb.append("   Thank you for shopping at\n");
        sb.append("       VelvetGlam! 💄\n");
        sb.append("--------------------------------\n");
        return sb.toString();
    }

    // ── Business Logic Helpers ───────────────────────────────

    /** Returns the sum of all line totals before discount. */
    public double getSubtotal() {
        return items.stream().mapToDouble(SaleItem::getLineTotal).sum();
    }

    /** @return true if the cart has at least one item */
    public boolean hasItems() { return !items.isEmpty(); }

    // ── Private Helpers ──────────────────────────────────────
    private String truncate(String s, int max) {
        if (s == null) return "";
        return (s.length() <= max) ? s : s.substring(0, max - 1) + "…";
    }

    // ── Getters ──────────────────────────────────────────────
    public int            getSaleId()       { return saleId; }
    public int            getCustomerId()   { return customerId; }
    public String         getCustomerName() { return customerName; }
    public int            getStaffId()      { return staffId; }
    public double         getDiscount()     { return discount; }
    public LocalDate      getSaleDate()     { return saleDate; }
    public List<SaleItem> getItems()        { return items; }

    // ── Setters ──────────────────────────────────────────────
    public void setSaleId(int id)          { this.saleId = id; }
    public void setCustomerName(String n)  { this.customerName = n; }

    public void setDiscount(double d) {
        if (d < 0) throw new IllegalArgumentException("Discount cannot be negative.");
        this.discount = d;
    }

    public void setItems(List<SaleItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    /**
     * Returns the total amount stored in the DB (populated by SaleDAO reads).
     * Returns calculateTotal() if not yet set (i.e., for a new in-memory sale).
     */
    public double getStoredTotal() {
        return (storedTotal >= 0) ? storedTotal : calculateTotal();
    }

    public void setStoredTotal(double t) { this.storedTotal = t; }
}
