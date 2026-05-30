package smartmart.model;

import java.util.Objects;

public class RestockOrder {
    public enum OrderStatus {
        PENDING, RECEIVED, CANCELLED
    }

    private int orderId;
    private Product product;
    private Supplier supplier;
    private int quantity;
    private String status;
    private String orderDate;
    private String receivedDate;

    public RestockOrder() {
    }

    public RestockOrder(int orderId, Product product, Supplier supplier, int quantity, String status, String orderDate, String receivedDate) {
        this.orderId = orderId;
        this.product = product;
        this.supplier = supplier;
        this.quantity = quantity;
        this.status = status;
        this.orderDate = orderDate;
        this.receivedDate = receivedDate;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(OrderStatus orderStatus) {
        this.status = (orderStatus != null) ? orderStatus.name() : null;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(this.status) || 
               (this.status != null && this.status.equalsIgnoreCase(OrderStatus.PENDING.name()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestockOrder that = (RestockOrder) o;
        return orderId == that.orderId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "RestockOrder{" +
                "orderId=" + orderId +
                ", product=" + product +
                ", supplier=" + supplier +
                ", quantity=" + quantity +
                ", status='" + status + '\'' +
                ", orderDate='" + orderDate + '\'' +
                ", receivedDate='" + receivedDate + '\'' +
                '}';
    }
}
