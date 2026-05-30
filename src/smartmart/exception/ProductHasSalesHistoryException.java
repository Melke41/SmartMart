package smartmart.exception;

public class ProductHasSalesHistoryException extends SmartMartException {
    public ProductHasSalesHistoryException(String productName) {
        super("Cannot delete '" + productName + "' — it has existing sales history.");
    }
}
