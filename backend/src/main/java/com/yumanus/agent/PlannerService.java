package com.yumanus.agent;

import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;
import com.yumanus.core.TripPlannerCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlannerService {
    
    private static final Logger logger = LoggerFactory.getLogger(PlannerService.class);
    
    private final TripPlannerCore tripPlannerCore;
    
    public PlannerService(TripPlannerCore tripPlannerCore) {
        this.tripPlannerCore = tripPlannerCore;
    }
    
    public TripPlan generatePlan(TripRequirement req) {
        logger.info("Generating trip plan via PlannerService (delegating to TripPlannerCore)");
        return tripPlannerCore.generatePlan(req);
    }
}


