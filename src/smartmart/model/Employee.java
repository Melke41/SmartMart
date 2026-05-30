package smartmart.model;

import java.util.Objects;

public class Employee implements Exportable {
    private int employeeId;
    private User user;
    private String phone;
    private double salary;
    private String hireDate;

    public Employee() {
    }

    public Employee(int employeeId, User user, String phone, double salary, String hireDate) {
        this.employeeId = employeeId;
        this.user = user;
        this.phone = phone;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    @Override
    public String toCSVRow() {
        String fullName = (user != null) ? user.getFullName() : "";
        String roleStr = (user != null && user.getRole() != null) ? user.getRole().name() : "";
        return employeeId + "," + fullName + "," + roleStr + "," + salary + "," + hireDate;
    }

    @Override
    public String getCSVHeader() {
        return "ID,Name,Role,Salary,Hire Date";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return employeeId == employee.employeeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", user=" + user +
                ", phone='" + phone + '\'' +
                ", salary=" + salary +
                ", hireDate='" + hireDate + '\'' +
                '}';
    }
}
