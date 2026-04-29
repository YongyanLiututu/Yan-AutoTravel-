package com.yumanus.aggregate;

import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.api.dto.TripRequirement;

public interface TripDataAggregator {
    
    AggregatedTripData aggregate(TripRequirement requirement);
    
    AggregatedTripData aggregate(TripRequirement requirement, 
                                  AggregatedTripData.DataSourceStrategy strategy);
}
