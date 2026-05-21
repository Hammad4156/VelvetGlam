package velvetglam.model;

/**
 * Abstract base class for all VelvetGlam products.
 *
 * OOP concepts demonstrated:
 *  - Abstraction  : abstract class with abstract getProductType()
 *  - Encapsulation: all fields are private; access only via getters/setters
 *  - Inheritance  : LipProduct, EyeProduct, FaceProduct, SkinProduct,
 *                   HairProduct, FragranceProduct all extend this class
 *  - Exception Handling: constructor validates inputs and throws
 *                         IllegalArgumentException for bad data
 */
public abstract class Product {

    // ── Private Fields (Encapsulation) ───────────────────────
    private int    productId;
    private String name;
    private int    brandId;
    private int    categoryId;
    private double price;
    private int    stockQty;
    private String shade;
    private String description;

    // Populated by DAO via JOIN — not stored in products table directly
    private String brandName;
    private String categoryName;

    /** Products with stock below this value trigger a low-stock alert. */
    public static final int LOW_STOCK_THRESHOLD = 5;

    // ── Constructor ──────────────────────────────────────────
    protected Product(int productId, String name, int brandId, int categoryId,
                      double price, int stockQty, String shade, String description) {
        // Validate — IllegalArgumentException is caught by the form dialog
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty.");
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative.");
        if (stockQty < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative.");

        this.productId   = productId;
        this.name        = name.trim();
        this.brandId     = brandId;
        this.categoryId  = categoryId;
        this.price       = price;
        this.stockQty    = stockQty;
        this.shade       = (shade != null) ? shade.trim() : "";
        this.description = (description != null) ? description.trim() : "";
    }

    // ── Abstract Method (Abstraction) ────────────────────────
    /**
     * Each subclass must return a human-readable product type label.
     * e.g., "Lip", "Eye", "Fragrance" …
     */
    public abstract String getProductType();

    // ── Business Logic ───────────────────────────────────────
    /** @return true if stock falls below the LOW_STOCK_THRESHOLD */
    public boolean isLowStock() {
        return stockQty < LOW_STOCK_THRESHOLD;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getProductId()    { return productId; }
    public String getName()         { return name; }
    public int    getBrandId()      { return brandId; }
    public int    getCategoryId()   { return categoryId; }
    public double getPrice()        { return price; }
    public int    getStockQty()     { return stockQty; }
    public String getShade()        { return shade; }
    public String getDescription()  { return description; }
    public String getBrandName()    { return brandName; }
    public String getCategoryName() { return categoryName; }

    // ── Setters ──────────────────────────────────────────────
    public void setProductId(int id) { this.productId = id; }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty())
            throw new IllegalArgumentException("Product name cannot be empty.");
        this.name = name.trim();
    }

    public void setBrandId(int brandId)    { this.brandId = brandId; }
    public void setCategoryId(int catId)   { this.categoryId = catId; }

    public void setPrice(double price) {
        if (price < 0)
            throw new IllegalArgumentException("Price cannot be negative.");
        this.price = price;
    }

    public void setStockQty(int qty) {
        if (qty < 0)
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        this.stockQty = qty;
    }

    public void setShade(String shade)          { this.shade = (shade != null) ? shade.trim() : ""; }
    public void setDescription(String desc)     { this.description = (desc != null) ? desc.trim() : ""; }
    public void setBrandName(String brandName)  { this.brandName = brandName; }
    public void setCategoryName(String catName) { this.categoryName = catName; }

    @Override
    public String toString() {
        return String.format("[%s] %s — PKR %.2f  (Stock: %d)",
                getProductType(), name, price, stockQty);
    }
}
