package com.Yan-AutoTravel.agent;

import com.Yan-AutoTravel.api.dto.TripPlan;
import com.Yan-AutoTravel.api.dto.TripRequirement;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
// sss
@Service
public class PlannerService {
    public TripPlan generatePlan(TripRequirement req) {
        TripPlan plan = new TripPlan();
        TripPlan.Summary summary = new TripPlan.Summary();
        summary.setCity(req.getCity());
        summary.setDays(req.getDays());
        summary.setEstCost(Map.of(req.getBudget().getCurrency(), Math.max(0, req.getBudget().getTotal() - 1000)));
        plan.setSummary(summary);

        for (int i = 0; i < req.getDays(); i++) {
            TripPlan.DayPlan day = new TripPlan.DayPlan();
            LocalDate date = req.getStartDate().plusDays(i);
            day.setDate(date);

            TripPlan.Slot morning = new TripPlan.Slot();
            morning.setTime("09:00-11:30");
            morning.setPoiId("POI-PLACEHOLDER");
            morning.setActivity("sightseeing");
            morning.setEvidenceIds(List.of());

            TripPlan.Slot lunch = new TripPlan.Slot();
            lunch.setTime("12:00-13:00");
            lunch.setPoiId("RESTAURANT-PLACEHOLDER");
            lunch.setActivity("lunch");

            day.setSlots(List.of(morning, lunch));
            plan.getDays().add(day);
        }

        TripPlan.Justification just = new TripPlan.Justification();
        just.setChoice("POI-PLACEHOLDER");
        just.setReasons(List.of("示例占位：后续由 ReAct 决策替换"));
        plan.setJustifications(List.of(just));

        TripPlan.Citation cit = new TripPlan.Citation();
        cit.setId("doc:seed:placeholder");
        cit.setSource("local-csv");
        plan.setCitations(List.of(cit));

        return plan;
    }
}


