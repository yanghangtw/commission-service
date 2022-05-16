package com.thoughtworks.commissionservice.infrasturcture.repository;

import com.thoughtworks.commissionservice.infrasturcture.repository.entity.AcquisitionHistoryEntity;
import com.thoughtworks.commissionservice.infrasturcture.repository.mapper.AcquisitionMapper;
import com.thoughtworks.commissionservice.service.model.AcquisitionHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Repository
public class AcquisitionRepositoryImpl implements AcquisitionRepository {
    @Autowired
    private AcquisitionMapper acquisitionMapper;

    @Override
    public Optional<AcquisitionHistory> findAcquisitionHistoryByOrderNo(String orderNo) {
        AcquisitionHistoryEntity example = new AcquisitionHistoryEntity();
        example.setOrderNo(orderNo);
        return acquisitionMapper.findOne(Example.of(example)).map(this::ofEntity);
    }

    @Override
    public void createAcquisitionHistory(AcquisitionHistory acquisitionHistory) {
        acquisitionMapper.save(ofModel(acquisitionHistory));
    }

    private AcquisitionHistory ofEntity(AcquisitionHistoryEntity entity) {
        AcquisitionHistory history = new AcquisitionHistory();
        history.setId(entity.getId());
        history.setSalesPersonId(entity.getSalesPersonId());
        history.setOrderNo(entity.getOrderNo());
        history.setCommissionAmount(entity.getCommissionAmount());
        history.setCreatedTime(LocalDateTime.ofInstant(entity.getCreatedTime(), ZoneId.systemDefault()));
        return history;
    }

    private AcquisitionHistoryEntity ofModel(AcquisitionHistory model) {
        Instant now = Instant.now();

        AcquisitionHistoryEntity entity = new AcquisitionHistoryEntity();
        entity.setId(model.getId());
        entity.setSalesPersonId(model.getSalesPersonId());
        entity.setOrderNo(model.getOrderNo());
        entity.setCommissionAmount(model.getCommissionAmount());
        entity.setCreatedTime(model.getCreatedTime().toInstant(ZoneId.systemDefault().getRules().getOffset(now)));
        return entity;
    }
}
