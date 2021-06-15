package com.redislabs.university.RU102J.dao;

import com.redislabs.university.RU102J.api.CapacityReport;
import com.redislabs.university.RU102J.api.MeterReading;
import com.redislabs.university.RU102J.resources.FairnessJob;

import java.util.List;

public interface FairnessDao {
    void update(MeterReading reading);
    CapacityReport getReport(Integer limit);
    Long getRank(Long siteId);

    List<FairnessJob> list();

    /**
     *
     * @return String job id
     */
    String add(List<FairnessJob> jobs);

    Object run();
}
