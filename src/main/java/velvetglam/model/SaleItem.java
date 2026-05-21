package velvetglam.model;

/**
 * Represents a single line item inside a Sale.
 *
 * OOP concepts demonstrated:
 *  - Encapsulation  : all fields private with validated getters/setters
 *  - Composition    : Sale owns a List<SaleItem> — a Sale is composed of items
 *  - Exception Handling: quantity and price are validated in the constructor
 *
 * Maps to the sale_items table in the database.
 */
public class SaleItem {

    // ── Private Fields (Encapsulation) ───────────────────────
    private int    itemId;
    private int    saleId;
    private int    productId;
    private String productName;   // snapshot — not a FK join at runtime
    private int    quantity;
    private double unitPrice;     // price snapshot at the time of the sale

    // ── Constructor ──────────────────────────────────────────
    public SaleItem(int itemId, int saleId, int productId, String productName,
                    int quantity, double unitPrice) {

        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        if (unitPrice < 0)
            throw new IllegalArgumentException("Unit price cannot be negative.");

        this.itemId      = itemId;
        this.saleId      = saleId;
        this.productId   = productId;
        this.productName = (productName != null) ? productName : "";
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
    }

    // ── Business Logic ───────────────────────────────────────

    /** @return quantity × unitPrice */
    public double getLineTotal() {
        return quantity * unitPrice;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getItemId()      { return itemId; }
    public int    getSaleId()      { return saleId; }
    public int    getProductId()   { return productId; }
    public String getProductName() { return productName; }
    public int    getQuantity()    { return quantity; }
    public double getUnitPrice()   { return unitPrice; }

    // ── Setters ──────────────────────────────────────────────
    public void setItemId(int id)          { this.itemId = id; }
    public void setSaleId(int id)          { this.saleId = id; }
    public void setProductId(int id)       { this.productId = id; }
    public void setProductName(String n)   { this.productName = (n != null) ? n : ""; }

    public void setQuantity(int qty) {
        if (qty <= 0)
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        this.quantity = qty;
    }

    public void setUnitPrice(double p) {
        if (p < 0)
            throw new IllegalArgumentException("Unit price cannot be negative.");
        this.unitPrice = p;
    }

    @Override
    public String toString() {
        return String.format("%s × %d  @ PKR %.2f", productName, quantity, unitPrice);
    }
}
