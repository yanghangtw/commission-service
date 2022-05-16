package com.thoughtworks.commissionservice.service.model;

public class OrderInfo {
    private String orderNo;
    private Boolean isReceived;
    private Double amount;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Boolean getConfirmedReceive() {
        return isReceived;
    }

    public void setConfirmedReceive(Boolean confirmedReceive) {
        isReceived = confirmedReceive;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
