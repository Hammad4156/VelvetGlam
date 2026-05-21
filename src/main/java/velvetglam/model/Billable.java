package velvetglam.model;

/**
 * Billable — interface that defines the billing contract.
 *
 * OOP concept demonstrated:
 *  - Interface : any class that represents a billable transaction must
 *                implement calculateTotal() and generateReceipt().
 *  - Polymorphism: the Sale class implements this interface, so any code
 *                  that works with Billable works for Sales too (and any
 *                  future QuotationSale, ReturnSale, etc.).
 *
 * Implemented by: Sale
 */
public interface Billable {

    /**
     * Calculates and returns the final payable amount after discounts.
     * @return total amount in PKR
     */
    double calculateTotal();

    /**
     * Generates a human-readable receipt string for display or printing.
     * @return multi-line receipt text
     */
    String generateReceipt();
}
