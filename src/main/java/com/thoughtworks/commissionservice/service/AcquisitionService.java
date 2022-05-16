package com.thoughtworks.commissionservice.service;

import com.thoughtworks.commissionservice.infrasturcture.repository.AcquisitionRepository;
import com.thoughtworks.commissionservice.service.exception.NotFoundException;
import com.thoughtworks.commissionservice.service.model.AcquisitionError;
import com.thoughtworks.commissionservice.service.model.AcquisitionHistory;
import com.thoughtworks.commissionservice.service.model.OrderInfo;
import com.thoughtworks.commissionservice.web.client.OrderClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AcquisitionService {

    private final OrderClient orderClient;
    private final AcquisitionRepository acquisitionRepository;
    private final IdGenerator idGenerator;

    public static final Double COMMISSION_RATE = 0.05;

    public AcquisitionService(OrderClient orderClient, AcquisitionRepository acquisitionRepository, IdGenerator idGenerator) {
        this.orderClient = orderClient;
        this.acquisitionRepository = acquisitionRepository;
        this.idGenerator = idGenerator;
    }

    public Either<AcquisitionError, String> acquisitionCommission(String salesPersonId, String orderNo) {
        Try<OrderInfo> orderInfoTry = orderClient.retrieveOrderInfo(orderNo);

        if (orderInfoTry.isFailure()) {
            AcquisitionError error = new AcquisitionError();
            if (orderInfoTry.getCause() instanceof NotFoundException) {
                error.setErrorMessage(ErrorMessage.ORDER_NOT_FOUND);
                return Either.left(error);
            }
            error.setErrorMessage(ErrorMessage.RETRIEVE_ORDER_FAILED);
            return Either.left(error);
        }

        OrderInfo orderInfo = orderInfoTry.get();

        if (!orderInfo.getConfirmedReceive()) {
            AcquisitionError error = new AcquisitionError();
            error.setErrorMessage(ErrorMessage.ORDER_NOT_RECEIVED);
            return Either.left(error);
        }

        Optional<AcquisitionHistory> historyOpt = acquisitionRepository.findAcquisitionHistoryByOrderNo(orderNo);
        if (historyOpt.isEmpty()) {
            String id = idGenerator.next();
            AcquisitionHistory history = new AcquisitionHistory();
            history.setId(id);
            history.setSalesPersonId(salesPersonId);
            history.setOrderNo(orderNo);
            history.setCommissionAmount(orderInfo.getAmount() * COMMISSION_RATE);
            history.setCreatedTime(LocalDateTime.now());
            acquisitionRepository.createAcquisitionHistory(history);
            return Either.right(id);
        }

        AcquisitionError error = new AcquisitionError();
        error.setErrorMessage(ErrorMessage.COMMISSION_ALREADY_ACQUIRED);
        error.setAcquisitionId(historyOpt.get().getId());
        return Either.left(error);
    }
}
