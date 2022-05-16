package com.thoughtworks.commissionservice.infrasturcture.repository.mapper;

import com.thoughtworks.commissionservice.infrasturcture.repository.entity.AcquisitionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface AcquisitionMapper extends JpaRepository<AcquisitionHistoryEntity, String> {
}
