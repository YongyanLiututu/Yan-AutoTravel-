package com.yumanus.core;

import com.yumanus.aggregate.TripDataAggregator;
import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;
import com.yumanus.planner.TripPlanAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TripPlannerCore {
    
    private static final Logger logger = LoggerFactory.getLogger(TripPlannerCore.class);
    
    private final TripDataAggregator dataAggregator;
    private final TripPlanAlgorithm planAlgorithm;
    
    public TripPlannerCore(TripDataAggregator dataAggregator,
                           TripPlanAlgorithm planAlgorithm) {
        this.dataAggregator = dataAggregator;
        this.planAlgorithm = planAlgorithm;
    }
    
    public TripPlan generatePlan(TripRequirement req) {
        return generatePlan(req, AggregatedTripData.DataSourceStrategy.COST_PRIORITY);
    }
    
    public TripPlan generatePlan(TripRequirement req, 
                                   AggregatedTripData.DataSourceStrategy strategy) {
        logger.info("Generating trip plan for city: {}, days: {}, strategy: {}", 
            req.getCity(), req.getDays(), strategy);
        
        AggregatedTripData aggregatedData = dataAggregator.aggregate(req, strategy);
        
        TripPlan plan = planAlgorithm.generatePlan(req, aggregatedData);
        
        logger.info("Trip plan generated successfully, using strategy: {}", strategy);
        return plan;
    }
    
    public TripPlan generatePlanWithFreshnessPriority(TripRequirement req) {
        return generatePlan(req, AggregatedTripData.DataSourceStrategy.FRESHNESS_PRIORITY);
    }
    
    public TripPlan generatePlanWithCostPriority(TripRequirement req) {
        return generatePlan(req, AggregatedTripData.DataSourceStrategy.COST_PRIORITY);
    }
}
