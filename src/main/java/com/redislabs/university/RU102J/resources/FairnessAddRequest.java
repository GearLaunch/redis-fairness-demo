package com.redislabs.university.RU102J.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class FairnessAddRequest {

    private List<FairnessJob> jobs;

    public FairnessAddRequest(List<FairnessJob> jobs) {
        this.jobs = jobs;
    }

    @JsonProperty
    List<FairnessJob> getJobs()
    {
        return jobs;
    }

    public void setJobs(List<FairnessJob> jobs) {
        this.jobs = ImmutableList.copyOf(jobs);
    }
}
