package smartmart.service;

import smartmart.dao.SupplierDAO;
import smartmart.exception.SmartMartException;
import smartmart.model.Role;
import smartmart.model.Supplier;

import java.sql.SQLException;
import java.util.List;

public class SupplierService {
    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAO();
    }

    public List<Supplier> getAllSuppliers() throws SmartMartException {
        try {
            return supplierDAO.getAllSuppliers();
        } catch (SQLException e) {
            throw new SmartMartException("Database error retrieving suppliers: " + e.getMessage(), e);
        }
    }

    public List<Supplier> searchSuppliers(String query) throws SmartMartException {
        try {
            return supplierDAO.searchSuppliers(query);
        } catch (SQLException e) {
            throw new SmartMartException("Database error searching suppliers: " + e.getMessage(), e);
        }
    }

    public void addSupplier(Supplier supplier) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new SmartMartException("Supplier name cannot be empty.");
        }
        try {
            supplierDAO.addSupplier(supplier);
        } catch (SQLException e) {
            throw new SmartMartException("Database error adding supplier: " + e.getMessage(), e);
        }
    }

    public void updateSupplier(Supplier supplier) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        if (supplier == null || supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new SmartMartException("Supplier name cannot be empty.");
        }
        try {
            supplierDAO.updateSupplier(supplier);
        } catch (SQLException e) {
            throw new SmartMartException("Database error updating supplier: " + e.getMessage(), e);
        }
    }

    public void deleteSupplier(int supplierId) throws SmartMartException {
        AuthService.requireRole(Role.ADMIN);
        try {
            supplierDAO.deleteSupplier(supplierId);
        } catch (SQLException e) {
            throw new SmartMartException("Database error deleting supplier: " + e.getMessage(), e);
        }
    }
}
