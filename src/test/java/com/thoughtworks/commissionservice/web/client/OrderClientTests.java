package com.thoughtworks.commissionservice.web.client;

import com.thoughtworks.commissionservice.service.exception.NotFoundException;
import com.thoughtworks.commissionservice.service.model.OrderInfo;
import io.vavr.control.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.client.MockServerClient;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.MediaType;
import org.mockserver.verify.VerificationTimes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(MockServerExtension.class)
public class OrderClientTests {
    private MockServerClient mockServer;
    private OrderClient orderClient;

    private static final String ORDER_RESPONSE_BODY = "{\"id\":\"%s\", \"status\":\"%s\", \"amount\":\"100\"}";

    @BeforeEach
    public void setUp(MockServerClient mockServer) {
        this.mockServer = mockServer;
        this.orderClient = new OrderClientImpl("http://localhost:" + mockServer.getPort());
    }

    @Test
    public void retrieveReceivedOrder_returnOrderInfo() {
        String orderNo = "1";
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/order-service/orders/" + orderNo)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(String.format(ORDER_RESPONSE_BODY, orderNo, "RECEIVED"))
                );

        Try<OrderInfo> orderInfoTry = orderClient.retrieveOrderInfo(orderNo);

        assertTrue(orderInfoTry.isSuccess());
        assertTrue(orderInfoTry.get().getConfirmedReceive());
        mockServer
                .verify(
                        request()
                                .withPath("/order-service/orders/" + orderNo),
                        VerificationTimes.exactly(1)
                );
    }

    @Test
    public void retrieveNonReceivedOrder_returnOrderInfo() {
        String orderNo = "2";
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/order-service/orders/" + orderNo)
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withContentType(MediaType.APPLICATION_JSON)
                                .withBody(String.format(ORDER_RESPONSE_BODY, orderNo, "NOT_RECEIVED"))
                );

        Try<OrderInfo> orderInfoTry = orderClient.retrieveOrderInfo(orderNo);

        assertTrue(orderInfoTry.isSuccess());
        assertFalse(orderInfoTry.get().getConfirmedReceive());
        mockServer
                .verify(
                        request()
                                .withPath("/order-service/orders/" + orderNo),
                        VerificationTimes.exactly(1)
                );
    }

    @Test
    public void retrieveNonExistedOrder_returnNotFoundException() {
        String orderNo = "3";
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/order-service/orders/" + orderNo)
                )
                .respond(
                        response()
                                .withStatusCode(404)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        Try<OrderInfo> orderInfoTry = orderClient.retrieveOrderInfo(orderNo);

        assertTrue(orderInfoTry.isFailure());
        assertTrue(orderInfoTry.getCause() instanceof NotFoundException);
        mockServer
                .verify(
                        request()
                                .withPath("/order-service/orders/" + orderNo),
                        VerificationTimes.exactly(1)
                );
    }

    @Test
    public void retrieveFailed_returnOriginalException() {
        String orderNo = "4";
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/order-service/orders/" + orderNo)
                )
                .respond(
                        response()
                                .withStatusCode(500)
                                .withContentType(MediaType.APPLICATION_JSON)
                );

        Try<OrderInfo> orderInfoTry = orderClient.retrieveOrderInfo(orderNo);

        assertTrue(orderInfoTry.isFailure());
        assertFalse(orderInfoTry.getCause() instanceof NotFoundException);
        mockServer
                .verify(
                        request()
                                .withPath("/order-service/orders/" + orderNo),
                        VerificationTimes.exactly(1)
                );
    }
}
