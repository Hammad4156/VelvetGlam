package velvetglam.model;

/**
 * Represents a cosmetic brand (e.g. MAC, Maybelline, Chanel).
 * Encapsulates brand data with private fields and public getters/setters.
 */
public class Brand {

    private int    brandId;
    private String name;
    private String supplierContact;
    private String country;

    public Brand(int brandId, String name, String supplierContact, String country) {
        this.brandId         = brandId;
        this.name            = (name != null) ? name.trim() : "";
        this.supplierContact = (supplierContact != null) ? supplierContact.trim() : "";
        this.country         = (country != null) ? country.trim() : "";
    }

    // ── Getters ──────────────────────────────────────────────
    public int    getBrandId()         { return brandId; }
    public String getName()            { return name; }
    public String getSupplierContact() { return supplierContact; }
    public String getCountry()         { return country; }

    // ── Setters ──────────────────────────────────────────────
    public void setBrandId(int id)              { this.brandId = id; }
    public void setName(String name)            { this.name = (name != null) ? name.trim() : ""; }
    public void setSupplierContact(String sc)   { this.supplierContact = (sc != null) ? sc.trim() : ""; }
    public void setCountry(String country)      { this.country = (country != null) ? country.trim() : ""; }

    /** JComboBox display. */
    @Override
    public String toString() { return name; }
}
