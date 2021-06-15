package com.redislabs.university.RU102J.dao;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FairnessJobElement {
    private String batchUuid;
    private String jobUuid;

    public FairnessJobElement() {
    }

    public FairnessJobElement(String batchUuid, String jobUuid) {
        this.batchUuid = batchUuid;
        this.jobUuid = jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }

    public void setBatchUuid(String batchUuid) {
        this.batchUuid = batchUuid;
    }

    @JsonProperty
    public String getBatchUuid() {
        return batchUuid;
    }

    @JsonProperty
    public String getJobUuid() {
        return jobUuid;
    }
}
