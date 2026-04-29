package com.yumanus.planner;

import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;

public interface TripPlanAlgorithm {
    
    TripPlan generatePlan(TripRequirement requirement, AggregatedTripData aggregatedData);
}
