package com.thoughtworks.commissionservice.service;

import com.thoughtworks.commissionservice.infrasturcture.repository.AcquisitionRepository;
import com.thoughtworks.commissionservice.service.exception.NotFoundException;
import com.thoughtworks.commissionservice.service.model.AcquisitionError;
import com.thoughtworks.commissionservice.service.model.AcquisitionHistory;
import com.thoughtworks.commissionservice.service.model.OrderInfo;
import com.thoughtworks.commissionservice.web.client.OrderClient;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class AcquisitionServiceTests {

    private final OrderClient orderClient = mock(OrderClient.class);

    private final AcquisitionRepository acquisitionRepository = mock(AcquisitionRepository.class);

    private final IdGenerator idGenerator = new IdGenerator();

    private final AcquisitionService service = new AcquisitionService(orderClient, acquisitionRepository, idGenerator);

    private static final String SALES_PERSON_ID = "SALES_PERSON_ID";

    @Test
    public void testSuccessfulAcquisitionProcess() {
        String orderNo = "1";
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setConfirmedReceive(true);
        orderInfo.setAmount(100D);
        when(orderClient.retrieveOrderInfo(orderNo)).thenReturn(Try.success(orderInfo));
        when(acquisitionRepository.findAcquisitionHistoryByOrderNo(orderNo)).thenReturn(Optional.empty());

        String acquisitionId = service.acquisitionCommission(SALES_PERSON_ID, orderNo).get();

        verify(orderClient, times(1)).retrieveOrderInfo(orderNo);
        verify(acquisitionRepository, times(1)).findAcquisitionHistoryByOrderNo(orderNo);
        ArgumentCaptor<AcquisitionHistory> input = ArgumentCaptor.forClass(AcquisitionHistory.class);
        verify(acquisitionRepository, times(1)).createAcquisitionHistory(input.capture());
        assertEquals(orderInfo.getAmount() * AcquisitionService.COMMISSION_RATE, input.getValue().getCommissionAmount());
    }

    @Test
    public void testNonReceivedOrderAcquisitionProcess() {
        String orderNo = "2";
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setConfirmedReceive(false);
        orderInfo.setAmount(100D);
        when(orderClient.retrieveOrderInfo(orderNo)).thenReturn(Try.success(orderInfo));

        Either<AcquisitionError, String> result = service.acquisitionCommission(SALES_PERSON_ID, orderNo);

        assertTrue(result.isLeft());
        assertEquals(ErrorMessage.ORDER_NOT_RECEIVED, result.getLeft().getErrorMessage());
        verify(acquisitionRepository, times(0)).findAcquisitionHistoryByOrderNo(anyString());
        verify(acquisitionRepository, times(0)).createAcquisitionHistory(any(AcquisitionHistory.class));
    }

    @Test
    public void testAlreadyAcquiredOrderProcess() {
        String orderNo = "3";
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setConfirmedReceive(true);
        orderInfo.setAmount(100D);
        when(orderClient.retrieveOrderInfo(orderNo)).thenReturn(Try.success(orderInfo));

        String acquisitionId = "acquisitionId";
        AcquisitionHistory history = new AcquisitionHistory();
        history.setId(acquisitionId);
        when(acquisitionRepository.findAcquisitionHistoryByOrderNo(orderNo)).thenReturn(Optional.of(history));

        Either<AcquisitionError, String> result = service.acquisitionCommission(SALES_PERSON_ID, orderNo);

        assertTrue(result.isLeft());
        assertEquals(ErrorMessage.COMMISSION_ALREADY_ACQUIRED, result.getLeft().getErrorMessage());
        assertEquals(acquisitionId, result.getLeft().getAcquisitionId());
        verify(acquisitionRepository, times(1)).findAcquisitionHistoryByOrderNo(orderNo);
        verify(acquisitionRepository, times(0)).createAcquisitionHistory(any(AcquisitionHistory.class));
    }

    @Test
    public void testOrderNotFoundProcess() {
        String orderNo = "4";
        when(orderClient.retrieveOrderInfo(orderNo)).thenReturn(Try.failure(new NotFoundException()));

        Either<AcquisitionError, String> result = service.acquisitionCommission(SALES_PERSON_ID, orderNo);

        assertTrue(result.isLeft());
        assertEquals(ErrorMessage.ORDER_NOT_FOUND, result.getLeft().getErrorMessage());
        verify(acquisitionRepository, times(0)).findAcquisitionHistoryByOrderNo(anyString());
        verify(acquisitionRepository, times(0)).createAcquisitionHistory(any(AcquisitionHistory.class));
    }

    @Test
    public void testOrderServiceFailedProcess() {
        String orderNo = "5";
        when(orderClient.retrieveOrderInfo(orderNo)).thenReturn(Try.failure(new RuntimeException()));

        Either<AcquisitionError, String> result = service.acquisitionCommission(SALES_PERSON_ID, orderNo);

        assertTrue(result.isLeft());
        assertEquals(ErrorMessage.RETRIEVE_ORDER_FAILED, result.getLeft().getErrorMessage());
        verify(acquisitionRepository, times(0)).findAcquisitionHistoryByOrderNo(anyString());
        verify(acquisitionRepository, times(0)).createAcquisitionHistory(any(AcquisitionHistory.class));
    }
}
