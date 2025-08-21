package com.Yan-AutoTravel.api;

import com.Yan-AutoTravel.api.dto.TripPlan;
import com.Yan-AutoTravel.api.dto.TripRequirement;
import com.Yan-AutoTravel.agent.PlannerService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PlanController {
    private final PlannerService plannerService;

    public PlanController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    @PostMapping(value = "/plan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public TripPlan plan(@Valid @RequestBody TripRequirement requirement) {
        return plannerService.generatePlan(requirement);
    }
}


