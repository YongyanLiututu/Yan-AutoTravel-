package com.Yan-AutoTravel.api;

import com.Yan-AutoTravel.agent.PlannerService;
import com.Yan-AutoTravel.api.dto.TripPlan;
import com.Yan-AutoTravel.api.dto.TripRequirement;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class DraftController {
    private final PlannerService plannerService;
    private static final Map<String, VersionedPlan> DRAFTS = new ConcurrentHashMap<>();

    public DraftController(PlannerService plannerService) {
        this.plannerService = plannerService;
    }

    public record DraftResponse(String id, long version, TripPlan plan) {}

    private static class VersionedPlan {
        long version;
        TripPlan plan;
        VersionedPlan(long v, TripPlan p) { this.version = v; this.plan = p; }
    }

    @PostMapping(value = "/plan/draft", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public DraftResponse saveDraft(@Valid @RequestBody TripRequirement requirement,
                                   @RequestParam(required = false) String id,
                                   @RequestParam(required = false, defaultValue = "0") long version) {
        TripPlan plan = plannerService.generatePlan(requirement);
        String draftId = id != null ? id : UUID.randomUUID().toString();
        VersionedPlan vp = DRAFTS.get(draftId);
        long nextVer = (vp == null) ? 1 : vp.version + 1;
        DRAFTS.put(draftId, new VersionedPlan(nextVer, plan));
        return new DraftResponse(draftId, nextVer, plan);
    }

    @GetMapping(value = "/plan/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public TripPlan getPlan(@PathVariable String id) {
        VersionedPlan vp = DRAFTS.get(id);
        if (vp == null) { throw new IllegalArgumentException("Draft not found: " + id); }
        return vp.plan;
    }
}


