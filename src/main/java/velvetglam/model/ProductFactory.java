package velvetglam.model;

/**
 * Factory that creates the correct Product subclass based on a category name.
 *
 * Used by ProductDAO when mapping ResultSet rows, so the rest of the code
 * works with the right concrete type without needing a big if-else block
 * scattered around the application.
 */
public class ProductFactory {

    private ProductFactory() {}   // utility class — no instances

    /**
     * Creates a Product subclass whose type matches the given categoryName.
     * If the categoryName is unrecognised, a FaceProduct is returned as default.
     *
     * @param productId    DB primary key (0 for new unsaved products)
     * @param name         product display name
     * @param brandId      FK to brands table
     * @param categoryId   FK to categories table
     * @param price        selling price
     * @param stockQty     current stock level
     * @param shade        shade / size / scent variant (nullable)
     * @param description  product description (nullable)
     * @param categoryName the category name string from the DB
     * @return the appropriate Product subclass instance
     */
    public static Product create(int productId, String name,
                                 int brandId, int categoryId,
                                 double price, int stockQty,
                                 String shade, String description,
                                 String categoryName) {

        String cat = (categoryName != null) ? categoryName.trim().toLowerCase() : "";

        return switch (cat) {
            case "lip"       -> new LipProduct      (productId, name, brandId, categoryId, price, stockQty, shade, description);
            case "eye"       -> new EyeProduct      (productId, name, brandId, categoryId, price, stockQty, shade, description);
            case "face"      -> new FaceProduct     (productId, name, brandId, categoryId, price, stockQty, shade, description);
            case "skin"      -> new SkinProduct     (productId, name, brandId, categoryId, price, stockQty, shade, description);
            case "hair"      -> new HairProduct     (productId, name, brandId, categoryId, price, stockQty, shade, description);
            case "fragrance" -> new FragranceProduct(productId, name, brandId, categoryId, price, stockQty, shade, description);
            default          -> new FaceProduct     (productId, name, brandId, categoryId, price, stockQty, shade, description);
        };
    }
}
