package velvetglam.model;

/**
 * Represents a skincare product (serum, moisturiser, cleanser …).
 * The shade field stores the size/volume variant (e.g. "30ml").
 */
public class SkinProduct extends Product {

    public SkinProduct(int productId, String name, int brandId, int categoryId,
                       double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Skin"; }
}
