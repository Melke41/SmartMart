package smartmart.exception;

public class OutOfStockException extends SmartMartException {
    private final int requestedQty;

    public OutOfStockException(String productName, int requestedQty) {
        super("Product '" + productName + "' is out of stock. Requested: " + requestedQty);
        this.requestedQty = requestedQty;
    }

    public int getRequestedQty() {
        return requestedQty;
    }
}
