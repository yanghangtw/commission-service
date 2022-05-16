package com.thoughtworks.commissionservice.infrasturcture.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class AcquisitionHistoryEntity {
    @Id
    @Column
    private String id;
    @Column(name = "order_no", length = 50, unique = true)
    private String orderNo;
    @Column(name = "sales_person_id", length = 50)
    private String salesPersonId;
    @Column(name = "commission_amount", precision = 2)
    private Double commissionAmount;
    @Column(name = "created_time", precision = 3)
    private Instant createdTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSalesPersonId() {
        return salesPersonId;
    }

    public void setSalesPersonId(String salesPersonId) {
        this.salesPersonId = salesPersonId;
    }

    public Double getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(Double commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
}
