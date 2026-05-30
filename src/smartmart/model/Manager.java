package smartmart.model;

public class Manager extends User {

    public Manager() {
        super();
    }

    public Manager(int userId, String username, String password, Role role, String fullName, boolean isActive) {
        super(userId, username, password, role, fullName, isActive);
    }

    @Override
    public String getDashboardTitle() {
        return "Manager Dashboard";
    }

    @Override
    public String getPermissionLevel() {
        return "REPORTS & MONITORING";
    }

    public String getManagerBadge() {
        return "MGR-" + getUserId();
    }
}
