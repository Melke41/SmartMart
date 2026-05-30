package smartmart.model;

public class Admin extends User {

    public Admin() {
        super();
    }

    public Admin(int userId, String username, String password, Role role, String fullName, boolean isActive) {
        super(userId, username, password, role, fullName, isActive);
    }

    @Override
    public String getDashboardTitle() {
        return "Admin Control Panel";
    }

    @Override
    public String getPermissionLevel() {
        return "FULL ACCESS";
    }

    public String getAdminCode() {
        return "ADM-" + getUserId();
    }
}
