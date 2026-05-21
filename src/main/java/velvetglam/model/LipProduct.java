package velvetglam.model;

/**
 * Represents a lip product (lipstick, lip gloss, lip liner …).
 * The shade field stores the colour variant (e.g. "Ruby Red").
 */
public class LipProduct extends Product {

    public LipProduct(int productId, String name, int brandId, int categoryId,
                      double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Lip"; }
}
