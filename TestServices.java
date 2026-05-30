import smartmart.exception.UnauthorizedAccessException;
import smartmart.model.Product;
import smartmart.model.Sale;
import smartmart.model.User;
import smartmart.service.AuthService;
import smartmart.service.ProductService;
import smartmart.service.SaleService;

import java.util.List;

public class TestServices {
    public static void main(String[] args) {
        System.out.println("Starting Service Layer Integration Test...");
        try {
            AuthService authService = new AuthService();
            ProductService productService = new ProductService();
            SaleService saleService = new SaleService();

            // 1. Call AuthService.login("admin", "admin123") and print dashboard title
            System.out.println("\n--- Testing Login (Admin) ---");
            User adminUser = authService.login("admin", "admin123");
            System.out.println("Logged in user: " + adminUser.getFullName() + " (Role: " + adminUser.getRole() + ")");
            System.out.println("Dashboard Title: " + adminUser.getDashboardTitle());
            System.out.println("Permission Level: " + adminUser.getPermissionLevel());

            // 2. Call ProductService.getAllProducts() and print count
            System.out.println("\n--- Testing Product Fetching ---");
            List<Product> products = productService.getAllProducts();
            System.out.println("Total products in system: " + products.size());

            // 3. Call AuthService.login("cashier1", "cashier123")
            System.out.println("\n--- Testing Login Switch (Cashier) ---");
            User cashierUser = authService.login("cashier1", "cashier123");
            System.out.println("Logged in user: " + cashierUser.getFullName() + " (Role: " + cashierUser.getRole() + ")");

            // 4. Create a new Sale, add 2 items to it using SaleService.addItemToSale()
            System.out.println("\n--- Testing Sale Creation and Item Staging ---");
            Sale sale = saleService.createNewSale();
            System.out.println("New sale initialized for Cashier: " + sale.getCashier().getFullName());

            if (products.size() >= 2) {
                Product p1 = products.get(0);
                Product p2 = products.get(1);
                System.out.println("Adding to sale: " + p1.getProductName() + " (Qty: 2, Stock before: " + p1.getStockQty() + ")");
                saleService.addItemToSale(sale, p1, 2);
                System.out.println("Adding to sale: " + p2.getProductName() + " (Qty: 5, Stock before: " + p2.getStockQty() + ")");
                saleService.addItemToSale(sale, p2, 5);
            } else {
                throw new Exception("Test failed: Less than 2 products found in database.");
            }

            // 5. Print the sale total
            System.out.println("Staged Sale Total Amount: ETB " + String.format("%.2f", sale.getTotalAmount()));

            // Process the sale so we can check that it executes cleanly
            System.out.println("Processing sale...");
            saleService.processSale(sale);
            System.out.println("Processed Sale ID: " + sale.getSaleId() + " (Processed successfully!)");

            // 6. Test exception: try to addProduct while logged in as cashier1
            System.out.println("\n--- Testing Access Control Exception (Unauthorized Access) ---");
            try {
                Product dummyProduct = new Product(0, "Test Product", products.get(0).getCategory(), products.get(0).getSupplier(), 10.0, 10, 5);
                System.out.println("Attempting to add product as Cashier (should fail)...");
                productService.addProduct(dummyProduct);
                System.err.println("Test Failed: Product was successfully added by a Cashier!");
                System.exit(1);
            } catch (UnauthorizedAccessException e) {
                System.out.println("Caught Expected Exception: " + e.getMessage());
                System.out.println("Username: " + e.getUsername() + " | Action: " + e.getAction());
            }

            // 7. Print "SERVICE LAYER TEST PASSED"
            System.out.println("\nSERVICE LAYER TEST PASSED");

        } catch (Exception e) {
            System.err.println("Test Failed with Exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
