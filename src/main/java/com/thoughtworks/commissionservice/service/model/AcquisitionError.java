package com.thoughtworks.commissionservice.service.model;

import com.thoughtworks.commissionservice.service.ErrorMessage;

public class AcquisitionError {
    private String acquisitionId;
    private ErrorMessage errorMessage;

    public String getAcquisitionId() {
        return acquisitionId;
    }

    public void setAcquisitionId(String acquisitionId) {
        this.acquisitionId = acquisitionId;
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(ErrorMessage errorMessage) {
        this.errorMessage = errorMessage;
    }
}
