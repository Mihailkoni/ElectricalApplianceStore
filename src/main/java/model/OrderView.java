package model;

import java.sql.Date;

public class OrderView {
    private int orderId;
    private String clientPassportNumber;
    private String applianceDetails;
    private String applianceArticleNumber;
    private String applianceName;
    private String clientFullName;
    private double pricePerUnit;
    private int quantity;
    private double totalPrice;
    private String managerEmail;
    private Date orderDate;

    public OrderView(int orderId, String applianceDetails, double totalPrice, Date orderDate, String managerEmail) {
        this.orderId = orderId;
        this.applianceDetails = applianceDetails;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.managerEmail = managerEmail;
    }

    public OrderView(int orderId, String clientPassportNumber, String clientFullName, String applianceDetails, double totalPrice, Date orderDate, String managerEmail) {
        this.orderId = orderId;
        this.clientPassportNumber = clientPassportNumber;
        this.clientFullName = clientFullName;
        this.applianceDetails = applianceDetails;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.managerEmail = managerEmail;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getClientFullName() {
        return clientFullName;
    }

    public String getApplianceDetails() {
        return applianceDetails;
    }

    public String getClientPassportNumber() {
        return clientPassportNumber;
    }

    public String getApplianceArticleNumber() {
        return applianceArticleNumber;
    }

    public String getApplianceName() {
        return applianceName;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setClientPassportNumber(String clientPassportNumber) {
        this.clientPassportNumber = clientPassportNumber;
    }

    public void setApplianceArticleNumber(String applianceArticleNumber) {
        this.applianceArticleNumber = applianceArticleNumber;
    }

    public void setApplianceName(String applianceName) {
        this.applianceName = applianceName;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
}