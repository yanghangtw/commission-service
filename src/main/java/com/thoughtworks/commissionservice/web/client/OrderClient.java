package com.thoughtworks.commissionservice.web.client;

import com.thoughtworks.commissionservice.service.model.OrderInfo;
import io.vavr.control.Try;

public interface OrderClient {
    Try<OrderInfo> retrieveOrderInfo(String orderNo);
}
