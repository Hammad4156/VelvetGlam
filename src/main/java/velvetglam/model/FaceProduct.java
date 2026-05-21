package velvetglam.model;

/**
 * Represents a face product (foundation, blush, concealer, primer …).
 * The shade field stores the undertone or coverage variant (e.g. "Honey 12").
 */
public class FaceProduct extends Product {

    public FaceProduct(int productId, String name, int brandId, int categoryId,
                       double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Face"; }
}
