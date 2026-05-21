package velvetglam.model;

/**
 * Represents a hair-care product (shampoo, conditioner, treatment …).
 * The shade field stores size (e.g. "250ml") or formula type.
 */
public class HairProduct extends Product {

    public HairProduct(int productId, String name, int brandId, int categoryId,
                       double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Hair"; }
}
