package velvetglam.model;

/**
 * Represents a product category (Lip, Eye, Face, Skin, Hair, Fragrance).
 * Used in Module 1 and as a filter in Module 2.
 */
public class Category {

    private int    categoryId;
    private String name;

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name       = name;
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getCategoryId() { return categoryId; }
    public String getName()       { return name; }

    // ── Setters ──────────────────────────────────────────────
    public void setCategoryId(int id)  { this.categoryId = id; }
    public void setName(String name)   { this.name = name; }

    /** Returns the name so JComboBox displays it cleanly. */
    @Override
    public String toString() { return name; }
}
