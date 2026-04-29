package com.yumanus.api;

import com.yumanus.agent.PlannerService;
import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;
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


