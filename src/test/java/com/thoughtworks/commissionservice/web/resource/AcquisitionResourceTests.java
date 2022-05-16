package com.thoughtworks.commissionservice.web.resource;

import com.thoughtworks.commissionservice.service.AcquisitionService;
import com.thoughtworks.commissionservice.service.ErrorMessage;
import com.thoughtworks.commissionservice.service.model.AcquisitionError;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AcquisitionResource.class)
public class AcquisitionResourceTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AcquisitionService service;

    private static final String SALES_PERSON_ID = "SALES_PERSON_ID";
    private static final String ACQUISITION_URI = "/commission-service/v1/sales-persons/{salesPersonId}/commission-acquisitions";

    @Test
    public void acquisitionSuccessfully_returnCreatedURI() throws Exception {
        String orderNo = "orderNo";
        String acquisitionId = "acquisitionId";
        when(service.acquisitionCommission(SALES_PERSON_ID, orderNo)).thenReturn(Either.right(acquisitionId));

        this.mockMvc.perform(getPost(orderNo))
                .andExpect(status().is(201))
                .andExpect(header().string("Location", mergeURI("/sales-persons", SALES_PERSON_ID, "commission-acquisitions", acquisitionId)));
    }

    @Test
    public void acquisitionFailedIfOrderNonReceived_return400() throws Exception {
        String orderNo = "orderNo";
        when(service.acquisitionCommission(SALES_PERSON_ID, orderNo)).thenReturn(getError(ErrorMessage.ORDER_NOT_RECEIVED, null));

        this.mockMvc.perform(getPost(orderNo))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.errMessage", is(ErrorMessage.ORDER_NOT_RECEIVED.getMessage())));
    }

    @Test
    public void acquisitionFailedIfAlreadyHadCommission_return409AndAcquisitionLink() throws Exception {
        String orderNo = "orderNo";
        String acquisitionId = "acquisitionId";
        when(service.acquisitionCommission(SALES_PERSON_ID, orderNo)).thenReturn(getError(ErrorMessage.COMMISSION_ALREADY_ACQUIRED, acquisitionId));

        this.mockMvc.perform(getPost(orderNo))
                .andExpect(status().is(409))
                .andExpect(jsonPath("$.errMessage", is(ErrorMessage.COMMISSION_ALREADY_ACQUIRED.getMessage())))
                .andExpect(jsonPath("$.links.getAcquisitionInfo", is(mergeURI("/sales-persons", SALES_PERSON_ID, "commission-acquisitions", acquisitionId))));
    }

    @Test
    public void acquisitionFailedIfOrderNotFound_return404() throws Exception {
        String orderNo = "orderNo";
        when(service.acquisitionCommission(SALES_PERSON_ID, orderNo)).thenReturn(getError(ErrorMessage.ORDER_NOT_FOUND, null));

        this.mockMvc.perform(getPost(orderNo))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$.errMessage", is(ErrorMessage.ORDER_NOT_FOUND.getMessage())));
    }

    @Test
    public void acquisitionFailedIfCallingOrderServiceFailed_return502() throws Exception {
        String orderNo = "orderNo";
        when(service.acquisitionCommission(SALES_PERSON_ID, orderNo)).thenReturn(getError(ErrorMessage.RETRIEVE_ORDER_FAILED, null));

        this.mockMvc.perform(getPost(orderNo))
                .andExpect(status().is(502))
                .andExpect(jsonPath("$.errMessage", is(ErrorMessage.RETRIEVE_ORDER_FAILED.getMessage())));
    }

    private String mergeURI(String... parts) {
        return String.join("/", parts);
    }

    private MockHttpServletRequestBuilder getPost(String orderNo) {
        return post(ACQUISITION_URI, SALES_PERSON_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(String.format("{\"orderNo\":\"%s\"}", orderNo));
    }

    private <T> Either<AcquisitionError, T> getError(ErrorMessage errorMessage, String acquisitionId) {
        AcquisitionError error = new AcquisitionError();
        error.setErrorMessage(errorMessage);
        error.setAcquisitionId(acquisitionId);
        return Either.left(error);
    }
}
