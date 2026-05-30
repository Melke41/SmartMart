package smartmart.model;

import java.util.Objects;

public class Product implements Searchable, Exportable {
    private int productId;
    private String productName;
    private Category category;
    private Supplier supplier;
    private double price;
    private int stockQty;
    private int lowStockLimit;

    public Product() {
    }

    public Product(int productId, String productName, Category category, Supplier supplier, double price, int stockQty, int lowStockLimit) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.supplier = supplier;
        this.price = price;
        this.stockQty = stockQty;
        this.lowStockLimit = lowStockLimit;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public int getLowStockLimit() {
        return lowStockLimit;
    }

    public void setLowStockLimit(int lowStockLimit) {
        this.lowStockLimit = lowStockLimit;
    }

    public boolean isLowStock() {
        return this.stockQty <= this.lowStockLimit;
    }

    public boolean isLowStock(int customLimit) {
        return this.stockQty <= customLimit;
    }

    @Override
    public boolean matchesQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        String lowerQuery = query.toLowerCase();
        boolean nameMatch = productName != null && productName.toLowerCase().contains(lowerQuery);
        boolean categoryMatch = category != null && category.getCategoryName() != null && category.getCategoryName().toLowerCase().contains(lowerQuery);
        return nameMatch || categoryMatch;
    }

    @Override
    public String toCSVRow() {
        String categoryName = (category != null) ? category.getCategoryName() : "";
        return productId + "," + productName + "," + categoryName + "," + price + "," + stockQty;
    }

    @Override
    public String getCSVHeader() {
        return "ID,Name,Category,Price,Stock";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", category=" + category +
                ", supplier=" + supplier +
                ", price=" + price +
                ", stockQty=" + stockQty +
                ", lowStockLimit=" + lowStockLimit +
                '}';
    }
}
