package model;

import java.sql.Date;
import java.util.List;

public class Order {
    private int orderId;
    private String clientPassportNumber;
    private String managerEmail;
    private Date orderDate;
    private List<OrderDetail> orderDetails;

    public Order(int orderId, String clientPassportNumber, String managerEmail, Date orderDate, List<OrderDetail> orderDetails) {
        this.orderId = orderId;
        this.clientPassportNumber = clientPassportNumber;
        this.managerEmail = managerEmail;
        this.orderDate = orderDate;
        this.orderDetails = orderDetails;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientPassportNumber() {
        return clientPassportNumber;
    }

    public void setClientPassportNumber(String clientPassportNumber) {
        this.clientPassportNumber = clientPassportNumber;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", clientPassportNumber='" + clientPassportNumber + '\'' +
                ", managerEmail='" + managerEmail + '\'' +
                ", orderDate=" + orderDate +
                ", orderDetails=" + orderDetails +
                '}';
    }
}