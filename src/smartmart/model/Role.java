package smartmart.model;

public enum Role {
    ADMIN("Administrator"),
    MANAGER("Store Manager"),
    CASHIER("Cashier");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
