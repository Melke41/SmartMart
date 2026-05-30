package smartmart.model;

import java.util.Objects;

public class Supplier implements Searchable {
    private int supplierId;
    private String name;
    private String contactPhone;
    private String email;
    private String address;

    public Supplier() {
    }

    public Supplier(int supplierId, String name, String contactPhone, String email, String address) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactPhone = contactPhone;
        this.email = email;
        this.address = address;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean matchesQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }
        String lowerQuery = query.toLowerCase();
        boolean nameMatch = name != null && name.toLowerCase().contains(lowerQuery);
        boolean emailMatch = email != null && email.toLowerCase().contains(lowerQuery);
        return nameMatch || emailMatch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return supplierId == supplier.supplierId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId);
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", name='" + name + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
