package smartmart.model;

import java.util.Objects;

public class SaleItem {
    private int itemId;
    private Product product;
    private int quantity;
    private double unitPrice;
    private double subtotal;

    public SaleItem() {
    }

    public SaleItem(int itemId, Product product, int quantity, double unitPrice) {
        this.itemId = itemId;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        recalculateSubtotal();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        recalculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void recalculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleItem saleItem = (SaleItem) o;
        return itemId == saleItem.itemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "itemId=" + itemId +
                ", product=" + product +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}
