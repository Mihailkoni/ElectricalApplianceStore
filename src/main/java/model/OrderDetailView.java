package model;

public class OrderDetailView {
    private int articleNumber;
    private String applianceName;
    private int quantity;
    private int stockQuantity;

    public OrderDetailView(int articleNumber, String applianceName, int quantity, int stockQuantity) {
        this.articleNumber = articleNumber;
        this.applianceName = applianceName;
        this.quantity = quantity;
        this.stockQuantity = stockQuantity;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(int articleNumber) {
        this.articleNumber = articleNumber;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}