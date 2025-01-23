package model;

public class OrderDetail {
    private int orderId;
    private String applianceArticleNumber;
    private int quantity;

    public OrderDetail(int orderId, String applianceArticleNumber, int quantity) {
        this.orderId = orderId;
        this.applianceArticleNumber = applianceArticleNumber;
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getApplianceArticleNumber() {
        return applianceArticleNumber;
    }

    public void setApplianceArticleNumber(String applianceArticleNumber) {
        this.applianceArticleNumber = applianceArticleNumber;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderId=" + orderId +
                ", applianceArticleNumber='" + applianceArticleNumber + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}