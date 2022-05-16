package com.thoughtworks.commissionservice.web.client;

import com.thoughtworks.commissionservice.service.exception.NotFoundException;
import com.thoughtworks.commissionservice.service.model.OrderInfo;
import com.thoughtworks.commissionservice.web.dto.RetrieveOrderResponse;
import io.vavr.control.Try;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClientImpl implements OrderClient {
    private static final String RETRIEVE_ORDER_URI = "/order-service/orders/";
    private static final String RECEIVED = "RECEIVED";

    private String orderServiceAddress;
    private RestTemplate restTemplate;

    public OrderClientImpl(@Value("${order-service.address}") String orderServiceAddress) {
        this.orderServiceAddress = orderServiceAddress;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public Try<OrderInfo> retrieveOrderInfo(String orderNo) {
        Try<RetrieveOrderResponse> responseTry = Try.of(
                () -> restTemplate.getForObject(
                        orderServiceAddress + RETRIEVE_ORDER_URI + orderNo, RetrieveOrderResponse.class)
        );
        if (responseTry.isSuccess()) {
            return responseTry.map(this::ofRetrieveOrderResponse);
        } else {
            if (responseTry.getCause() instanceof HttpClientErrorException) {
                HttpClientErrorException ex = (HttpClientErrorException) responseTry.getCause();
                if (HttpStatus.NOT_FOUND.equals(ex.getStatusCode())) {
                    return Try.failure(new NotFoundException());
                }
            }
            return Try.failure(responseTry.getCause());
        }
    }

    private OrderInfo ofRetrieveOrderResponse(RetrieveOrderResponse response) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(response.getId());
        orderInfo.setConfirmedReceive(RECEIVED.equals(response.getStatus()));
        orderInfo.setAmount(response.getAmount());
        return orderInfo;
    }
}
