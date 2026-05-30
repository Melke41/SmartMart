package smartmart.model;

public class Cashier extends User {

    public Cashier() {
        super();
    }

    public Cashier(int userId, String username, String password, Role role, String fullName, boolean isActive) {
        super(userId, username, password, role, fullName, isActive);
    }

    @Override
    public String getDashboardTitle() {
        return "Point of Sale";
    }

    @Override
    public String getPermissionLevel() {
        return "SALES ONLY";
    }

    public String getCashierCode() {
        return "CSH-" + getUserId();
    }
}
