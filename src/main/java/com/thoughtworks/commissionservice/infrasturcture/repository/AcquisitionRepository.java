package com.thoughtworks.commissionservice.infrasturcture.repository;

import com.thoughtworks.commissionservice.service.model.AcquisitionHistory;

import java.util.Optional;

public interface AcquisitionRepository {
    Optional<AcquisitionHistory> findAcquisitionHistoryByOrderNo(String orderNo);

    void createAcquisitionHistory(AcquisitionHistory acquisitionHistory);
}
