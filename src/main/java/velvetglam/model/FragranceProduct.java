package velvetglam.model;

/**
 * Represents a fragrance product (EDT, EDP, body mist …).
 * The shade field stores the volume variant (e.g. "100ml").
 */
public class FragranceProduct extends Product {

    public FragranceProduct(int productId, String name, int brandId, int categoryId,
                            double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Fragrance"; }
}
