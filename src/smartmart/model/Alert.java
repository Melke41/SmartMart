package smartmart.model;

import java.util.Objects;

public class Alert {
    private int alertId;
    private Product product;
    private String message;
    private boolean isResolved;
    private String createdAt;

    public Alert() {
    }

    public Alert(int alertId, Product product, String message, boolean isResolved, String createdAt) {
        this.alertId = alertId;
        this.product = product;
        this.message = message;
        this.isResolved = isResolved;
        this.createdAt = createdAt;
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alert alert = (Alert) o;
        return alertId == alert.alertId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(alertId);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "alertId=" + alertId +
                ", product=" + product +
                ", message='" + message + '\'' +
                ", isResolved=" + isResolved +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
