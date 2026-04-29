package com.yumanus.planner;

import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.aggregate.model.AggregatedTripData.DailyAggregatedData;
import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;
import com.yumanus.datasource.port.PoiDataSource;
import com.yumanus.datasource.port.WeatherDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TripPlanAlgorithmImpl implements TripPlanAlgorithm {
    
    private static final Logger logger = LoggerFactory.getLogger(TripPlanAlgorithmImpl.class);
    
    @Override
    public TripPlan generatePlan(TripRequirement requirement, AggregatedTripData aggregatedData) {
        logger.info("Generating trip plan using aggregated data, strategy: {}", 
            aggregatedData.getStrategy());
        
        TripPlan plan = new TripPlan();
        
        TripPlan.Summary summary = buildSummary(requirement, aggregatedData);
        plan.setSummary(summary);
        
        List<TripPlan.DayPlan> dayPlans = buildDayPlans(requirement, aggregatedData);
        plan.setDays(dayPlans);
        
        List<TripPlan.Justification> justifications = buildJustifications(requirement);
        plan.setJustifications(justifications);
        
        List<TripPlan.Citation> citations = buildCitations(aggregatedData);
        plan.setCitations(citations);
        
        logger.info("Trip plan generated successfully with {} days", dayPlans.size());
        return plan;
    }
    
    private TripPlan.Summary buildSummary(TripRequirement requirement, 
                                            AggregatedTripData aggregatedData) {
        TripPlan.Summary summary = new TripPlan.Summary();
        summary.setCity(requirement.getCity());
        summary.setDays(requirement.getDays());
        
        long estimatedCost = calculateEstimatedCost(requirement);
        summary.setEstCost(Map.of(requirement.getBudget().getCurrency(), estimatedCost));
        
        return summary;
    }
    
    private long calculateEstimatedCost(TripRequirement requirement) {
        long baseCost = requirement.getBudget().getTotal();
        int days = requirement.getDays();
        int adults = requirement.getParty().getAdults();
        int kids = requirement.getParty().getKids();
        
        long dailyPerPerson = 300;
        long estimatedCost = (adults + (long)(kids * 0.5)) * days * dailyPerPerson;
        
        return Math.max(0, Math.min(baseCost, estimatedCost));
    }
    
    private List<TripPlan.DayPlan> buildDayPlans(TripRequirement requirement,
                                                   AggregatedTripData aggregatedData) {
        List<TripPlan.DayPlan> dayPlans = new ArrayList<>();
        List<PoiDataSource.PoiInfo> pois = aggregatedData.getAllPois();
        List<DailyAggregatedData> dailyDataList = aggregatedData.getDailyData();
        
        for (int i = 0; i < requirement.getDays(); i++) {
            LocalDate date = requirement.getStartDate().plusDays(i);
            TripPlan.DayPlan dayPlan = new TripPlan.DayPlan();
            dayPlan.setDate(date);
            
            List<TripPlan.Slot> slots = buildSlotsForDay(requirement, pois, i);
            dayPlan.setSlots(slots);
            
            List<TripPlan.Transit> transit = buildTransitForDay(dailyDataList, i);
            dayPlan.setTransit(transit);
            
            List<String> notes = buildNotesForDay(requirement, dailyDataList, i);
            dayPlan.setNotes(notes);
            
            dayPlans.add(dayPlan);
        }
        
        return dayPlans;
    }
    
    private List<TripPlan.Slot> buildSlotsForDay(TripRequirement requirement,
                                                   List<PoiDataSource.PoiInfo> pois,
                                                   int dayIndex) {
        List<TripPlan.Slot> slots = new ArrayList<>();
        
        TripPlan.Slot morningSlot = createSlot("09:00-11:30", "sightseeing", pois, dayIndex * 2);
        slots.add(morningSlot);
        
        TripPlan.Slot lunchSlot = createLunchSlot(pois, dayIndex);
        slots.add(lunchSlot);
        
        TripPlan.Slot afternoonSlot = createSlot("14:30-17:00", "sightseeing", pois, dayIndex * 2 + 1);
        slots.add(afternoonSlot);
        
        return slots;
    }
    
    private TripPlan.Slot createSlot(String time, String activity, 
                                       List<PoiDataSource.PoiInfo> pois, int poiIndex) {
        TripPlan.Slot slot = new TripPlan.Slot();
        slot.setTime(time);
        slot.setActivity(activity);
        
        if (!pois.isEmpty()) {
            int idx = poiIndex % pois.size();
            PoiDataSource.PoiInfo poi = pois.get(idx);
            slot.setPoiId(poi.id());
            slot.setEvidenceIds(List.of(poi.sourceId()));
        } else {
            slot.setPoiId("POI-PLACEHOLDER-" + poiIndex);
            slot.setEvidenceIds(List.of());
        }
        
        return slot;
    }
    
    private TripPlan.Slot createLunchSlot(List<PoiDataSource.PoiInfo> pois, int dayIndex) {
        TripPlan.Slot slot = new TripPlan.Slot();
        slot.setTime("12:00-13:30");
        slot.setActivity("lunch");
        
        List<PoiDataSource.PoiInfo> foodPois = pois.stream()
            .filter(p -> "lunch".equals(p.activityType()) || 
                        p.tags().contains("美食") || p.tags().contains("小吃"))
            .toList();
        
        if (!foodPois.isEmpty()) {
            int idx = dayIndex % foodPois.size();
            PoiDataSource.PoiInfo poi = foodPois.get(idx);
            slot.setPoiId(poi.id());
            slot.setEvidenceIds(List.of(poi.sourceId()));
        } else {
            slot.setPoiId("RESTAURANT-PLACEHOLDER-" + dayIndex);
            slot.setEvidenceIds(List.of());
        }
        
        return slot;
    }
    
    private List<TripPlan.Transit> buildTransitForDay(List<DailyAggregatedData> dailyDataList, 
                                                         int dayIndex) {
        List<TripPlan.Transit> transitList = new ArrayList<>();
        
        if (dayIndex < dailyDataList.size()) {
            DailyAggregatedData dailyData = dailyDataList.get(dayIndex);
            for (var option : dailyData.getTransit()) {
                TripPlan.Transit transit = new TripPlan.Transit();
                transit.setFrom(option.from());
                transit.setTo(option.to());
                transit.setMode(option.mode().name().toLowerCase());
                transit.setEtaMin(option.durationMinutes());
                transitList.add(transit);
            }
        }
        
        return transitList;
    }
    
    private List<String> buildNotesForDay(TripRequirement requirement,
                                            List<DailyAggregatedData> dailyDataList,
                                            int dayIndex) {
        List<String> notes = new ArrayList<>();
        
        if (dayIndex < dailyDataList.size()) {
            DailyAggregatedData dailyData = dailyDataList.get(dayIndex);
            WeatherDataSource.WeatherInfo weather = dailyData.getWeather();
            
            if (requirement.getConstraints().getRainPlan() != null && 
                requirement.getConstraints().getRainPlan() && 
                weather != null) {
                if (weather.condition() == WeatherDataSource.WeatherCondition.RAINY ||
                    weather.condition() == WeatherDataSource.WeatherCondition.STORMY) {
                    notes.add("天气提示：" + weather.description() + "，建议准备雨具或调整室内行程。");
                } else {
                    notes.add("天气提示：" + weather.description() + "，温度 " + 
                              weather.temperatureMin() + "°C ~ " + weather.temperatureMax() + "°C。");
                }
            }
        }
        
        String pace = requirement.getPreferences().getPace();
        if ("easy".equals(pace)) {
            notes.add("行程节奏：轻松模式，预留充足休息时间。");
        } else if ("fast".equals(pace)) {
            notes.add("行程节奏：紧凑模式，建议提前规划交通。");
        }
        
        return notes;
    }
    
    private List<TripPlan.Justification> buildJustifications(TripRequirement requirement) {
        List<TripPlan.Justification> justifications = new ArrayList<>();
        
        TripPlan.Justification just = new TripPlan.Justification();
        just.setChoice("推荐行程组合");
        
        List<String> reasons = new ArrayList<>();
        reasons.add("根据您的偏好（" + requirement.getPreferences().getPace() + "节奏）精心规划。");
        
        if (requirement.getPreferences().getFood() != null && 
            !requirement.getPreferences().getFood().isEmpty()) {
            reasons.add("已优先考虑您喜爱的美食类型：" + 
                String.join("、", requirement.getPreferences().getFood()) + "。");
        }
        
        if (requirement.getParty().getKids() > 0) {
            reasons.add("考虑到有" + requirement.getParty().getKids() + "位儿童，行程安排相对轻松。");
        }
        
        if (requirement.getConstraints().getMinTransfers() != null && 
            requirement.getConstraints().getMinTransfers()) {
            reasons.add("已尽量减少换乘次数，提升出行舒适度。");
        }
        
        just.setReasons(reasons);
        justifications.add(just);
        
        return justifications;
    }
    
    private List<TripPlan.Citation> buildCitations(AggregatedTripData aggregatedData) {
        List<TripPlan.Citation> citations = new ArrayList<>();
        
        for (var citation : aggregatedData.getCitations()) {
            TripPlan.Citation tripCitation = new TripPlan.Citation();
            tripCitation.setId(citation.getId());
            tripCitation.setSource(citation.getSource());
            citations.add(tripCitation);
        }
        
        if (citations.isEmpty()) {
            TripPlan.Citation cit = new TripPlan.Citation();
            cit.setId("doc:seed:placeholder");
            cit.setSource("local-csv");
            citations.add(cit);
        }
        
        return citations;
    }
}
