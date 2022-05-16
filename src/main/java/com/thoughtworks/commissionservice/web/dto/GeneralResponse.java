package com.thoughtworks.commissionservice.web.dto;

import java.util.Map;

public class GeneralResponse<T> {
    private T data;
    private String errMessage;
    private Map<String, String> links;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }
}
