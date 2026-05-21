package velvetglam.ui.module2;

import velvetglam.dao.CustomerDAO;
import velvetglam.dao.ProductDAO;
import velvetglam.dao.SaleDAO;
import velvetglam.model.Customer;
import velvetglam.model.Product;
import velvetglam.model.Sale;
import velvetglam.model.SaleItem;

import javax.swing.*;
import velvetglam.util.VGTheme;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Module 2 — Customer Management, Sales & Billing Panel.
 *
 * Designed to be embedded in the Module 4 dashboard or run standalone
 * via Module2Launcher for testing.
 *
 * Layout:
 *  ┌───────────────────────────────────────────────────────┐
 *  │  Header bar (purple)                                  │
 *  ├───────────────────────────────────────────────────────┤
 *  │  JTabbedPane                                          │
 *  │    Tab 1 — 👥 Customers                               │
 *  │      [Search bar]                                     │
 *  │      [Customer JTable]                                │
 *  │      [CRUD buttons]                                   │
 *  │    Tab 2 — 🧾 New Sale (POS)                          │
 *  │      [Customer selector]                              │
 *  │      [Product search + Product list  |  Cart table]  │
 *  │      [Discount + Total + Complete Sale button]        │
 *  │    Tab 3 — 📋 Sales History                           │
 *  │      [Date / customer filter bar]                     │
 *  │      [Sales JTable]                                   │
 *  │      [View Receipt + Refresh buttons]                 │
 *  └───────────────────────────────────────────────────────┘
 *
 * OOP concepts demonstrated:
 *  - Composition    : Module2Panel contains CustomerDAO, SaleDAO, ProductDAO
 *  - Encapsulation  : all UI state fields are private
 *  - Interface      : Sale implements Billable (calculateTotal, generateReceipt)
 *  - Exception Handling: every DAO call wrapped in try/catch with user feedback
 */
public class Module2Panel extends JPanel {

    // ── Colour Palette (consistent with Module 1) ────────────
    private static final Color C_PRIMARY  = new Color(108,  52, 168);
    private static final Color C_SELECT   = new Color(206, 147, 216);
    private static final Color C_BG       = new Color(250, 248, 255);
    private static final Color C_ADD      = new Color( 56, 142,  60);
    private static final Color C_EDIT     = new Color( 25, 118, 210);
    private static final Color C_DEL      = new Color(211,  47,  47);
    private static final Color C_MISC     = new Color( 69,  90, 100);
    private static final Color C_COMPLETE = new Color( 46, 125,  50);
    private static final Color C_ROW_ALT  = new Color(245, 240, 255);

    // ── DAOs ─────────────────────────────────────────────────
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final ProductDAO  productDAO  = new ProductDAO();
    private final SaleDAO     saleDAO     = new SaleDAO();

    // ════════════════════════════════════════════════
    //  TAB 1 — CUSTOMERS
    // ════════════════════════════════════════════════
    private DefaultTableModel customerModel;
    private JTable            customerTable;
    private JTextField        tfCustSearch;

    // ════════════════════════════════════════════════
    //  TAB 2 — NEW SALE (POS)
    // ════════════════════════════════════════════════
    private JComboBox<Customer> cbCustomer;
    private JTextField          tfProductSearch;
    private DefaultTableModel   productModel;
    private JTable              productTable;
    private JSpinner            spQty;
    private DefaultTableModel   cartModel;
    private JTable              cartTable;
    /** In-memory cart for the current sale. */
    private Sale                currentSale;
    private JTextField          tfDiscount;
    private JLabel              lblSubtotal;
    private JLabel              lblTotal;

    // ════════════════════════════════════════════════
    //  TAB 3 — SALES HISTORY
    // ════════════════════════════════════════════════
    private DefaultTableModel historyModel;
    private JTable            historyTable;
    private JTextField        tfDateFilter;
    private JComboBox<Object> cbCustFilter;

    // ── Entry point ──────────────────────────────────────────
    public Module2Panel() {
        setLayout(new BorderLayout());
        setBackground(C_BG);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTabs(),   BorderLayout.CENTER);

        loadCustomers();
        loadCustomerCombo();
        loadProducts("");
        loadSalesHistory();
        resetSale();
    }

    // ════════════════════════════════════════════════
    //  HEADER
    // ════════════════════════════════════════════════
    private JPanel buildHeader() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(C_PRIMARY);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));

        JLabel title = new JLabel("🧾   Module 2 — Customer Management & Sales / Billing");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        bar.add(title, BorderLayout.WEST);

        JLabel sub = new JLabel("VelvetGlam  |  Muhammad Hammad Ali  ");
        sub.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        sub.setForeground(new Color(220, 200, 255));
        bar.add(sub, BorderLayout.EAST);

        return bar;
    }

    // ════════════════════════════════════════════════
    //  TABS
    // ════════════════════════════════════════════════
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.setBackground(C_BG);
        tabs.addTab("  👥  Customers    ", buildCustomerTab());
        tabs.addTab("  🧾  New Sale      ", buildPOSTab());
        tabs.addTab("  📋  Sales History ", buildHistoryTab());
        return tabs;
    }

    // ════════════════════════════════════════════════
    //  TAB 1 — CUSTOMERS
    // ════════════════════════════════════════════════
    private JPanel buildCustomerTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.add(buildCustSearchBar(), BorderLayout.NORTH);
        p.add(buildCustomerTable(), BorderLayout.CENTER);
        p.add(buildCustButtons(),   BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildCustSearchBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(C_BG);

        tfCustSearch = new JTextField(18);
        tfCustSearch.setFont(fieldFont());
        tfCustSearch.putClientProperty("JTextField.placeholderText", "Search by name or contact…");

        JButton btnSearch = actionBtn("🔍  Search", C_PRIMARY);
        JButton btnReset  = actionBtn("↺  Reset",   C_MISC);

        btnSearch.addActionListener(e -> searchCustomers());
        btnReset.addActionListener(e -> {
            tfCustSearch.setText("");
            loadCustomers();
        });

        bar.add(label("Search:")); bar.add(tfCustSearch);
        bar.add(btnSearch); bar.add(btnReset);
        return bar;
    }

    private JScrollPane buildCustomerTable() {
        String[] cols = {"ID", "Name", "Contact", "Email",
                         "Loyalty Points", "Registered"};
        customerModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        customerTable = new JTable(customerModel);
        styleTable(customerTable);
        customerTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer(customerModel));

        int[] widths = {45, 180, 130, 200, 110, 110};
        for (int i = 0; i < widths.length; i++)
            customerTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        return new JScrollPane(customerTable);
    }

    private JPanel buildCustButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        p.setBackground(C_BG);

        JButton btnAdd     = actionBtn("➕  Add",      C_ADD);
        JButton btnEdit    = actionBtn("✏️  Edit",     C_EDIT);
        JButton btnDelete  = actionBtn("🗑️  Delete",   C_DEL);
        JButton btnRefresh = actionBtn("↺  Refresh",  C_MISC);

        btnAdd.addActionListener(e -> openCustomerForm(null));
        btnEdit.addActionListener(e -> editSelectedCustomer());
        btnDelete.addActionListener(e -> deleteSelectedCustomer());
        btnRefresh.addActionListener(e -> loadCustomers());

        p.add(btnAdd); p.add(btnEdit); p.add(btnDelete); p.add(btnRefresh);
        return p;
    }

    // ── Customer data methods ────────────────────────────────

    void loadCustomers() {
        customerModel.setRowCount(0);
        try {
            for (Customer c : customerDAO.getAllCustomers()) {
                customerModel.addRow(new Object[]{
                    c.getCustomerId(), c.getName(), c.getContact(),
                    c.getEmail(), c.getLoyaltyPoints(),
                    c.getDateRegistered().toString()
                });
            }
        } catch (SQLException ex) { error("Failed to load customers:\n" + ex.getMessage()); }
    }

    private void searchCustomers() {
        customerModel.setRowCount(0);
        try {
            for (Customer c : customerDAO.searchCustomers(tfCustSearch.getText())) {
                customerModel.addRow(new Object[]{
                    c.getCustomerId(), c.getName(), c.getContact(),
                    c.getEmail(), c.getLoyaltyPoints(),
                    c.getDateRegistered().toString()
                });
            }
        } catch (SQLException ex) { error("Search failed:\n" + ex.getMessage()); }
    }

    private void openCustomerForm(Customer existing) {
        CustomerFormDialog dlg = new CustomerFormDialog(
            SwingUtilities.getWindowAncestor(this), existing);
        dlg.setVisible(true);
        if (dlg.isSaved()) { loadCustomers(); loadCustomerCombo(); }
    }

    private void editSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row < 0) { warn("Please select a customer to edit."); return; }
        int id = (int) customerModel.getValueAt(row, 0);
        try {
            Customer c = customerDAO.findById(id);
            if (c != null) openCustomerForm(c);
        } catch (SQLException ex) { error(ex.getMessage()); }
    }

    private void deleteSelectedCustomer() {
        int row = customerTable.getSelectedRow();
        if (row < 0) { warn("Please select a customer to delete."); return; }
        int    id   = (int)    customerModel.getValueAt(row, 0);
        String name = (String) customerModel.getValueAt(row, 1);
        int choice = JOptionPane.showConfirmDialog(this,
            "Delete customer \"" + name + "\"?\n"
          + "All their sales records will also be affected.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            try {
                customerDAO.deleteCustomer(id);
                loadCustomers();
                loadCustomerCombo();
            } catch (SQLException ex) { error(ex.getMessage()); }
        }
    }

    // ════════════════════════════════════════════════
    //  TAB 2 — NEW SALE (POS)
    // ════════════════════════════════════════════════
    private JPanel buildPOSTab() {
        JPanel p = new JPanel(new BorderLayout(8, 8));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.add(buildPOSTop(),    BorderLayout.NORTH);
        p.add(buildPOSMiddle(), BorderLayout.CENTER);
        p.add(buildPOSBottom(), BorderLayout.SOUTH);
        return p;
    }

    // ── Customer selector row ────────────────────────────────
    private JPanel buildPOSTop() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(C_PRIMARY),
            " Customer ", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY));

        cbCustomer = new JComboBox<>();
        cbCustomer.setFont(fieldFont());
        cbCustomer.setPreferredSize(new Dimension(280, 28));

        JButton btnRefreshCust = actionBtn("↺", C_MISC);
        btnRefreshCust.setToolTipText("Reload customers");
        btnRefreshCust.addActionListener(e -> loadCustomerCombo());

        p.add(label("Select Customer:"));
        p.add(cbCustomer);
        p.add(btnRefreshCust);
        return p;
    }

    // ── Product panel (left) + Cart panel (right) ────────────
    private JSplitPane buildPOSMiddle() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            buildProductPanel(), buildCartPanel());
        split.setDividerLocation(420);
        split.setDividerSize(6);
        split.setBackground(C_BG);
        return split;
    }

    private JPanel buildProductPanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 220)),
            " Products ", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY));

        // Search bar
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        searchRow.setBackground(C_BG);
        tfProductSearch = new JTextField(14);
        tfProductSearch.setFont(fieldFont());
        tfProductSearch.putClientProperty("JTextField.placeholderText", "Search products…");
        JButton btnSearch = actionBtn("🔍", C_PRIMARY);
        btnSearch.setToolTipText("Search products");
        JButton btnAll    = actionBtn("All", C_MISC);
        btnAll.setToolTipText("Show all products");
        btnSearch.addActionListener(e -> loadProducts(tfProductSearch.getText()));
        btnAll.addActionListener(e -> {
            tfProductSearch.setText("");
            loadProducts("");
        });
        searchRow.add(tfProductSearch); searchRow.add(btnSearch); searchRow.add(btnAll);
        p.add(searchRow, BorderLayout.NORTH);

        // Product table
        String[] cols = {"ID", "Product Name", "Brand", "Price (PKR)", "Stock"};
        productModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productModel);
        styleTable(productTable);
        productTable.setDefaultRenderer(Object.class, new LowStockCellRenderer());
        int[] w = {40, 160, 100, 90, 50};
        for (int i = 0; i < w.length; i++)
            productTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        p.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Qty row
        JPanel qtyRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        qtyRow.setBackground(C_BG);
        spQty = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spQty.setFont(fieldFont());
        spQty.setPreferredSize(new Dimension(70, 28));
        JButton btnAdd = actionBtn("➕  Add to Cart", C_ADD);
        btnAdd.addActionListener(e -> addToCart());
        qtyRow.add(label("Qty:")); qtyRow.add(spQty); qtyRow.add(btnAdd);
        p.add(qtyRow, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildCartPanel() {
        JPanel p = new JPanel(new BorderLayout(4, 4));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 150, 220)),
            " Cart ", 0, 0,
            new Font("Segoe UI", Font.BOLD, 12), C_PRIMARY));

        // Cart table
        String[] cols = {"Product", "Qty", "Unit Price", "Line Total"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartModel);
        styleTable(cartTable);
        cartTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer(cartModel));
        int[] w = {170, 45, 95, 100};
        for (int i = 0; i < w.length; i++)
            cartTable.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        p.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        // Remove button
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        south.setBackground(C_BG);
        JButton btnRemove = actionBtn("🗑️  Remove Item", C_DEL);
        JButton btnClear  = actionBtn("✕  Clear Cart",   C_MISC);
        btnRemove.addActionListener(e -> removeCartItem());
        btnClear.addActionListener(e -> {
            currentSale.clearItems();
            refreshCart();
        });
        south.add(btnClear); south.add(btnRemove);
        p.add(south, BorderLayout.SOUTH);

        return p;
    }

    // ── Totals + Complete Sale ────────────────────────────────
    private JPanel buildPOSBottom() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 180, 220)),
            BorderFactory.createEmptyBorder(8, 8, 4, 8)));

        // Discount + subtotal + total row
        JPanel totals = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 4));
        totals.setBackground(C_BG);

        tfDiscount = new JTextField("0", 7);
        tfDiscount.setFont(fieldFont());
        tfDiscount.getDocument().addDocumentListener(
            new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e)  { updateTotals(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e)  { updateTotals(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { updateTotals(); }
            });

        lblSubtotal = new JLabel("Subtotal:  PKR 0.00");
        lblSubtotal.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        lblTotal = new JLabel("TOTAL:  PKR 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setForeground(C_PRIMARY);

        totals.add(label("Discount (PKR):")); totals.add(tfDiscount);
        totals.add(Box.createHorizontalStrut(20));
        totals.add(lblSubtotal);
        totals.add(Box.createHorizontalStrut(20));
        totals.add(lblTotal);

        p.add(totals, BorderLayout.CENTER);

        JButton btnComplete = actionBtn("✅  Complete Sale", C_COMPLETE);
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnComplete.setPreferredSize(new Dimension(190, 38));
        btnComplete.addActionListener(e -> completeSale());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        right.setBackground(C_BG);
        right.add(btnComplete);
        p.add(right, BorderLayout.EAST);

        return p;
    }

    // ── POS data methods ─────────────────────────────────────

    private void loadCustomerCombo() {
        cbCustomer.removeAllItems();
        try {
            for (Customer c : customerDAO.getAllCustomers())
                cbCustomer.addItem(c);
        } catch (SQLException ex) { error("Could not load customers: " + ex.getMessage()); }
    }

    void loadProducts(String keyword) {
        productModel.setRowCount(0);
        try {
            List<Product> list = keyword.isBlank()
                ? productDAO.getAllProducts()
                : productDAO.searchProducts(keyword, 0);
            for (Product p : list) {
                productModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getBrandName(),
                    String.format("%.2f", p.getPrice()), p.getStockQty()
                });
            }
        } catch (SQLException ex) { error("Could not load products: " + ex.getMessage()); }
    }

    private void addToCart() {
        int row = productTable.getSelectedRow();
        if (row < 0) { warn("Please select a product from the list."); return; }

        int    productId   = (int)    productModel.getValueAt(row, 0);
        String productName = (String) productModel.getValueAt(row, 1);
        double unitPrice;
        int    stock;

        try {
            unitPrice = Double.parseDouble(productModel.getValueAt(row, 3).toString());
            stock     = (int) productModel.getValueAt(row, 4);
        } catch (NumberFormatException ex) {
            error("Could not read product price.");
            return;
        }

        int qty = (int) spQty.getValue();

        if (qty > stock) {
            warn("Not enough stock! Available: " + stock + ", Requested: " + qty);
            return;
        }

        // If the same product is already in the cart, just increase qty
        List<SaleItem> items = currentSale.getItems();
        for (SaleItem item : items) {
            if (item.getProductId() == productId) {
                int newQty = item.getQuantity() + qty;
                if (newQty > stock) {
                    warn("Adding this would exceed available stock (" + stock + ").");
                    return;
                }
                item.setQuantity(newQty);
                refreshCart();
                return;
            }
        }

        currentSale.addItem(new SaleItem(0, 0, productId, productName, qty, unitPrice));
        refreshCart();
    }

    private void removeCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) { warn("Please select a cart item to remove."); return; }
        currentSale.removeItem(row);
        refreshCart();
    }

    private void refreshCart() {
        cartModel.setRowCount(0);
        for (SaleItem item : currentSale.getItems()) {
            cartModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                String.format("%.2f", item.getUnitPrice()),
                String.format("%.2f", item.getLineTotal())
            });
        }
        updateTotals();
    }

    private void updateTotals() {
        double discount = parseDiscount();
        currentSale.setDiscount(discount < 0 ? 0 : discount);
        double sub   = currentSale.getSubtotal();
        double total = currentSale.calculateTotal();
        lblSubtotal.setText(String.format("Subtotal:  PKR %,.2f", sub));
        lblTotal.setText(String.format("TOTAL:  PKR %,.2f", total));
    }

    private double parseDiscount() {
        try { return Double.parseDouble(tfDiscount.getText().trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private void completeSale() {
        // Validate customer
        Customer selectedCustomer = (Customer) cbCustomer.getSelectedItem();
        if (selectedCustomer == null) {
            warn("Please select a customer before completing the sale.");
            return;
        }

        // Validate cart
        if (!currentSale.hasItems()) {
            warn("The cart is empty. Please add at least one product.");
            return;
        }

        // Validate discount
        double discount = parseDiscount();
        if (discount < 0) {
            warn("Discount cannot be negative.");
            tfDiscount.requestFocus();
            return;
        }
        if (discount > currentSale.getSubtotal()) {
            warn("Discount (PKR " + discount + ") cannot exceed subtotal (PKR "
                + String.format("%.2f", currentSale.getSubtotal()) + ").");
            return;
        }

        // Set sale metadata
        currentSale.setDiscount(discount);

        // Build a fully populated Sale for the DAO
        Sale sale = new Sale(
            0,
            selectedCustomer.getCustomerId(),
            1,          // Default staff_id=1 — replaced by session in Module 4
            discount,
            LocalDate.now()
        );
        sale.setCustomerName(selectedCustomer.getName());
        for (SaleItem item : currentSale.getItems()) sale.addItem(item);

        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Confirm sale for %s?%n%n"
                + "Items  : %d%n"
                + "Total  : PKR %,.2f%n"
                + "Points Earned: +%d%n",
                selectedCustomer.getName(),
                sale.getItems().size(),
                sale.calculateTotal(),
                Customer.calculateEarnedPoints(sale.calculateTotal())),
            "Confirm Sale", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int saleId = saleDAO.createSale(sale);
            if (saleId > 0) {
                // Show receipt
                SaleReceiptDialog receipt = new SaleReceiptDialog(
                    SwingUtilities.getWindowAncestor(this), sale);
                receipt.setVisible(true);

                // Reset the POS for the next sale
                resetSale();
                loadProducts("");
                loadCustomers();     // refresh loyalty points in customer tab
                loadSalesHistory();  // refresh history tab
            }
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (SQLException ex) {
            error("Failed to save sale:\n" + ex.getMessage());
        }
    }

    private void resetSale() {
        currentSale = new Sale(0, 0, 1, 0.0, LocalDate.now());
        if (cartModel != null) cartModel.setRowCount(0);
        if (tfDiscount != null) tfDiscount.setText("0");
        if (lblSubtotal != null) lblSubtotal.setText("Subtotal:  PKR 0.00");
        if (lblTotal    != null) lblTotal.setText("TOTAL:  PKR 0.00");
    }

    // ════════════════════════════════════════════════
    //  TAB 3 — SALES HISTORY
    // ════════════════════════════════════════════════
    private JPanel buildHistoryTab() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(C_BG);
        p.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        p.add(buildHistoryFilter(),  BorderLayout.NORTH);
        p.add(buildHistoryTable(),   BorderLayout.CENTER);
        p.add(buildHistoryButtons(), BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildHistoryFilter() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        bar.setBackground(C_BG);

        tfDateFilter = new JTextField(10);
        tfDateFilter.setFont(fieldFont());
        tfDateFilter.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");

        cbCustFilter = new JComboBox<>();
        cbCustFilter.setFont(fieldFont());
        cbCustFilter.setPreferredSize(new Dimension(200, 26));
        cbCustFilter.addItem("All Customers");
        try { customerDAO.getAllCustomers().forEach(cbCustFilter::addItem); }
        catch (SQLException ignored) {}

        JButton btnFilter  = actionBtn("🔍  Filter",  C_PRIMARY);
        JButton btnShowAll = actionBtn("↺  Show All", C_MISC);

        btnFilter.addActionListener(e -> filterHistory());
        btnShowAll.addActionListener(e -> {
            tfDateFilter.setText("");
            cbCustFilter.setSelectedIndex(0);
            loadSalesHistory();
        });

        bar.add(label("Date:"));     bar.add(tfDateFilter);
        bar.add(label("Customer:")); bar.add(cbCustFilter);
        bar.add(btnFilter); bar.add(btnShowAll);
        return bar;
    }

    private JScrollPane buildHistoryTable() {
        String[] cols = {"Sale #", "Customer", "Date", "Items",
                         "Discount (PKR)", "Total (PKR)"};
        historyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyModel);
        styleTable(historyTable);
        historyTable.setDefaultRenderer(Object.class,
            new AlternatingRowRenderer(historyModel));

        int[] widths = {65, 200, 110, 60, 120, 120};
        for (int i = 0; i < widths.length; i++)
            historyTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        return new JScrollPane(historyTable);
    }

    private JPanel buildHistoryButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        p.setBackground(C_BG);

        JButton btnReceipt = actionBtn("🧾  View Receipt", C_PRIMARY);
        JButton btnRefresh = actionBtn("↺  Refresh",       C_MISC);

        btnReceipt.addActionListener(e -> viewReceipt());
        btnRefresh.addActionListener(e -> loadSalesHistory());

        p.add(btnReceipt); p.add(btnRefresh);
        return p;
    }

    // ── History data methods ─────────────────────────────────

    void loadSalesHistory() {
        historyModel.setRowCount(0);
        try {
            for (Sale s : saleDAO.getAllSales()) {
                List<SaleItem> items = saleDAO.getSaleItems(s.getSaleId());
                historyModel.addRow(new Object[]{
                    s.getSaleId(),
                    s.getCustomerName(),
                    s.getSaleDate().toString(),
                    items.size(),
                    String.format("%.2f", s.getDiscount()),
                    String.format("%.2f", s.getStoredTotal())
                });
            }
        } catch (SQLException ex) { error("Failed to load sales history:\n" + ex.getMessage()); }
    }

    private void filterHistory() {
        historyModel.setRowCount(0);
        String   dateStr = tfDateFilter.getText().trim();
        Object   custObj = cbCustFilter.getSelectedItem();

        try {
            List<Sale> sales;

            // If a specific customer is selected
            if (custObj instanceof Customer c) {
                sales = saleDAO.getSalesByCustomer(c.getCustomerId());
            } else if (!dateStr.isEmpty()) {
                // Filter by date
                LocalDate date;
                try { date = LocalDate.parse(dateStr); }
                catch (Exception ex) {
                    warn("Invalid date format. Use YYYY-MM-DD.");
                    return;
                }
                sales = saleDAO.getSalesByDate(date);
            } else {
                sales = saleDAO.getAllSales();
            }

            for (Sale s : sales) {
                List<SaleItem> items = saleDAO.getSaleItems(s.getSaleId());
                historyModel.addRow(new Object[]{
                    s.getSaleId(),
                    s.getCustomerName(),
                    s.getSaleDate().toString(),
                    items.size(),
                    String.format("%.2f", s.getDiscount()),
                    String.format("%.2f", s.getStoredTotal())
                });
            }
        } catch (SQLException ex) { error("Filter failed:\n" + ex.getMessage()); }
    }

    private void viewReceipt() {
        int row = historyTable.getSelectedRow();
        if (row < 0) { warn("Please select a sale to view its receipt."); return; }
        int saleId = (int) historyModel.getValueAt(row, 0);

        try {
            // Reconstruct a Sale object with items for receipt generation
            List<Sale> all = saleDAO.getAllSales();
            Sale found = all.stream()
                .filter(s -> s.getSaleId() == saleId)
                .findFirst().orElse(null);

            if (found == null) { error("Sale #" + saleId + " not found."); return; }

            List<SaleItem> items = saleDAO.getSaleItems(saleId);
            found.setItems(items);

            SaleReceiptDialog dlg = new SaleReceiptDialog(
                SwingUtilities.getWindowAncestor(this), found);
            dlg.setVisible(true);

        } catch (SQLException ex) { error("Could not load receipt:\n" + ex.getMessage()); }
    }

    // ════════════════════════════════════════════════
    //  SHARED HELPERS
    // ════════════════════════════════════════════════

    private void styleTable(JTable t) {
        VGTheme.styleTable(t);
    }

    private JButton actionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel label(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private Font fieldFont() { return new Font("Segoe UI", Font.PLAIN, 13); }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // ── Custom cell renderers ────────────────────────────────

    /**
     * Alternates row background between white and a soft lavender.
     */
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        private final DefaultTableModel model;
        AlternatingRowRenderer(DefaultTableModel m) { this.model = m; }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);
            if (!isSelected)
                c.setBackground(row % 2 == 0 ? Color.WHITE : C_ROW_ALT);
            return c;
        }
    }

    /**
     * Highlights product rows that are out of stock (stock = 0) in pale red,
     * and low-stock rows (stock < threshold) in pale yellow.
     */
    private class LowStockCellRenderer extends DefaultTableCellRenderer {
        private static final Color C_ZERO = new Color(255, 205, 210);
        private static final Color C_LOW  = new Color(255, 249, 196);

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);
            if (!isSelected) {
                try {
                    int stock = (int) productModel.getValueAt(row, 4);
                    if (stock == 0)
                        c.setBackground(C_ZERO);
                    else if (stock < Product.LOW_STOCK_THRESHOLD)
                        c.setBackground(C_LOW);
                    else
                        c.setBackground(row % 2 == 0 ? Color.WHITE : C_ROW_ALT);
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                }
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
