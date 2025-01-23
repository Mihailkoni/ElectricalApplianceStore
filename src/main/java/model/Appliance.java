package model;

public class Appliance {
    private int articleNumber;
    private String name;
    private int typeId;
    private String typeName;
    private double price;
    private int stockQuantity;
    private int quantity;

    public Appliance(int articleNumber, String name, int typeId, String typeName, double price, int stockQuantity) {
        this.articleNumber = articleNumber;
        this.name = name;
        this.typeId = typeId;
        this.typeName = typeName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.quantity = 0;
    }

    public int getArticleNumber() {
        return articleNumber;
    }

    public String getName() {
        return name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}