package velvetglam.model;

/**
 * Represents an eye product (mascara, eyeliner, eyeshadow …).
 * The shade field stores the colour or finish (e.g. "Pitch Black").
 */
public class EyeProduct extends Product {

    public EyeProduct(int productId, String name, int brandId, int categoryId,
                      double price, int stockQty, String shade, String description) {
        super(productId, name, brandId, categoryId, price, stockQty, shade, description);
    }

    @Override
    public String getProductType() { return "Eye"; }
}
