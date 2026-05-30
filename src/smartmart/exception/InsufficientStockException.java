package smartmart.exception;

public class InsufficientStockException extends SmartMartException {
    private final int requestedQty;
    private final int availableQty;

    public InsufficientStockException(String productName, int requestedQty, int availableQty) {
        super("Insufficient stock for '" + productName + "'. Requested: " + requestedQty + ", Available: " + availableQty);
        this.requestedQty = requestedQty;
        this.availableQty = availableQty;
    }

    public int getRequestedQty() {
        return requestedQty;
    }

    public int getAvailableQty() {
        return availableQty;
    }
}
