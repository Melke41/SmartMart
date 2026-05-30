package smartmart.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Sale {
    private int saleId;
    private User cashier;
    private List<SaleItem> items = new ArrayList<>();
    private double totalAmount;
    private String saleDate;

    public Sale() {
    }

    public Sale(int saleId, User cashier, List<SaleItem> items, double totalAmount, String saleDate) {
        this.saleId = saleId;
        this.cashier = cashier;
        this.items = (items != null) ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
        this.totalAmount = calculateTotal();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public void addItem(SaleItem item) {
        if (item != null) {
            if (this.items == null) {
                this.items = new ArrayList<>();
            }
            this.items.add(item);
            this.totalAmount = calculateTotal();
        }
    }

    public void removeItem(SaleItem item) {
        if (item != null && this.items != null) {
            this.items.remove(item);
            this.totalAmount = calculateTotal();
        }
    }

    public double calculateTotal() {
        double sum = 0.0;
        if (this.items != null) {
            for (SaleItem item : this.items) {
                if (item != null) {
                    sum += item.getSubtotal();
                }
            }
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return saleId == sale.saleId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(saleId);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId=" + saleId +
                ", cashier=" + cashier +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", totalAmount=" + totalAmount +
                ", saleDate='" + saleDate + '\'' +
                '}';
    }
}
