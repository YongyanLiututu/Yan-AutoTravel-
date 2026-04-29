package com.yumanus.core;

import com.yumanus.api.dto.TripPlan;
import com.yumanus.api.dto.TripRequirement;
import com.yumanus.datasource.port.*;
import com.yumanus.datasource.registry.DataSourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class TripPlannerCore {
    
    private static final Logger logger = LoggerFactory.getLogger(TripPlannerCore.class);
    
    private final DataSourceRegistry dataSourceRegistry;
    
    public TripPlannerCore(DataSourceRegistry dataSourceRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
    }
    
    public TripPlan generatePlan(TripRequirement req) {
        logger.info("Generating trip plan for city: {}, days: {}", req.getCity(), req.getDays());
        
        TripPlan plan = new TripPlan();
        
        TripPlan.Summary summary = buildSummary(req);
        plan.setSummary(summary);
        
        List<TripPlan.DayPlan> dayPlans = buildDayPlans(req);
        plan.setDays(dayPlans);
        
        List<TripPlan.Justification> justifications = buildJustifications(req);
        plan.setJustifications(justifications);
        
        List<TripPlan.Citation> citations = buildCitations(req);
        plan.setCitations(citations);
        
        logger.info("Trip plan generated successfully with {} days", dayPlans.size());
        return plan;
    }
    
    private TripPlan.Summary buildSummary(TripRequirement req) {
        TripPlan.Summary summary = new TripPlan.Summary();
        summary.setCity(req.getCity());
        summary.setDays(req.getDays());
        
        long estimatedCost = calculateEstimatedCost(req);
        summary.setEstCost(Map.of(req.getBudget().getCurrency(), estimatedCost));
        
        return summary;
    }
    
    private long calculateEstimatedCost(TripRequirement req) {
        long baseCost = req.getBudget().getTotal();
        int days = req.getDays();
        int adults = req.getParty().getAdults();
        int kids = req.getParty().getKids();
        
        long dailyPerPerson = 300;
        long estimatedCost = (adults + kids * 0.5) * days * dailyPerPerson;
        
        return Math.max(0, Math.min(baseCost, estimatedCost));
    }
    
    private List<TripPlan.DayPlan> buildDayPlans(TripRequirement req) {
        List<TripPlan.DayPlan> dayPlans = new ArrayList<>();
        
        Optional<PoiDataSource> poiDataSource = dataSourceRegistry.getDefaultPoiDataSource();
        Optional<WeatherDataSource> weatherDataSource = dataSourceRegistry.getDefaultWeatherDataSource();
        
        List<PoiDataSource.PoiInfo> pois = new ArrayList<>();
        if (poiDataSource.isPresent()) {
            PoiDataSource.PoiSearchCriteria criteria = new PoiDataSource.PoiSearchCriteria(
                null,
                req.getPreferences().getFood() != null ? req.getPreferences().getFood() : List.of(),
                4.0,
                20
            );
            pois = poiDataSource.get().searchByCity(req.getCity(), criteria);
            logger.info("Found {} POIs for city: {}", pois.size(), req.getCity());
        }
        
        for (int i = 0; i < req.getDays(); i++) {
            LocalDate date = req.getStartDate().plusDays(i);
            TripPlan.DayPlan dayPlan = new TripPlan.DayPlan();
            dayPlan.setDate(date);
            
            List<TripPlan.Slot> slots = buildSlotsForDay(req, pois, i);
            dayPlan.setSlots(slots);
            
            List<TripPlan.Transit> transit = buildTransitForDay(req, i);
            dayPlan.setTransit(transit);
            
            List<String> notes = buildNotesForDay(req, weatherDataSource, date);
            dayPlan.setNotes(notes);
            
            dayPlans.add(dayPlan);
        }
        
        return dayPlans;
    }
    
    private List<TripPlan.Slot> buildSlotsForDay(TripRequirement req, 
                                                   List<PoiDataSource.PoiInfo> pois, 
                                                   int dayIndex) {
        List<TripPlan.Slot> slots = new ArrayList<>();
        
        TripPlan.Slot morningSlot = new TripPlan.Slot();
        morningSlot.setTime("09:00-11:30");
        morningSlot.setActivity("sightseeing");
        
        if (!pois.isEmpty()) {
            int poiIndex = (dayIndex * 2) % pois.size();
            PoiDataSource.PoiInfo poi = pois.get(poiIndex);
            morningSlot.setPoiId(poi.id());
            morningSlot.setEvidenceIds(List.of(poi.sourceId()));
        } else {
            morningSlot.setPoiId("POI-PLACEHOLDER-" + dayIndex);
            morningSlot.setEvidenceIds(List.of());
        }
        
        TripPlan.Slot lunchSlot = new TripPlan.Slot();
        lunchSlot.setTime("12:00-13:30");
        lunchSlot.setActivity("lunch");
        
        List<PoiDataSource.PoiInfo> foodPois = pois.stream()
            .filter(p -> "lunch".equals(p.activityType()) || 
                        p.tags().contains("美食") || p.tags().contains("小吃"))
            .toList();
        
        if (!foodPois.isEmpty()) {
            int poiIndex = dayIndex % foodPois.size();
            PoiDataSource.PoiInfo poi = foodPois.get(poiIndex);
            lunchSlot.setPoiId(poi.id());
            lunchSlot.setEvidenceIds(List.of(poi.sourceId()));
        } else {
            lunchSlot.setPoiId("RESTAURANT-PLACEHOLDER-" + dayIndex);
            lunchSlot.setEvidenceIds(List.of());
        }
        
        TripPlan.Slot afternoonSlot = new TripPlan.Slot();
        afternoonSlot.setTime("14:30-17:00");
        afternoonSlot.setActivity("sightseeing");
        
        if (!pois.isEmpty()) {
            int poiIndex = (dayIndex * 2 + 1) % pois.size();
            PoiDataSource.PoiInfo poi = pois.get(poiIndex);
            afternoonSlot.setPoiId(poi.id());
            afternoonSlot.setEvidenceIds(List.of(poi.sourceId()));
        } else {
            afternoonSlot.setPoiId("POI-PLACEHOLDER-" + (dayIndex + 1));
            afternoonSlot.setEvidenceIds(List.of());
        }
        
        slots.add(morningSlot);
        slots.add(lunchSlot);
        slots.add(afternoonSlot);
        
        return slots;
    }
    
    private List<TripPlan.Transit> buildTransitForDay(TripRequirement req, int dayIndex) {
        List<TripPlan.Transit> transitList = new ArrayList<>();
        
        Optional<TransitDataSource> transitDataSource = 
            dataSourceRegistry.getDefaultTransitDataSource(TransitDataSource.TransitMode.TRANSIT);
        
        if (transitDataSource.isPresent()) {
            TransitDataSource.TransitSearchRequest searchRequest = new TransitDataSource.TransitSearchRequest(
                req.getCity(),
                req.getCity(),
                "酒店",
                "景点" + dayIndex,
                null, null, null, null,
                req.getStartDate().plusDays(dayIndex),
                TransitDataSource.TransitMode.TRANSIT,
                req.getPreferences().getPace()
            );
            
            Optional<TransitDataSource.TransitOption> bestRoute = 
                transitDataSource.get().getBestRoute(searchRequest);
            
            if (bestRoute.isPresent()) {
                TransitDataSource.TransitOption option = bestRoute.get();
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
    
    private List<String> buildNotesForDay(TripRequirement req, 
                                           Optional<WeatherDataSource> weatherDataSource,
                                           LocalDate date) {
        List<String> notes = new ArrayList<>();
        
        if (req.getConstraints().getRainPlan() != null && req.getConstraints().getRainPlan()) {
            if (weatherDataSource.isPresent()) {
                Optional<WeatherDataSource.WeatherInfo> weather = 
                    weatherDataSource.get().getWeatherForDate(req.getCity(), date);
                
                if (weather.isPresent()) {
                    WeatherDataSource.WeatherInfo info = weather.get();
                    if (info.condition() == WeatherDataSource.WeatherCondition.RAINY ||
                        info.condition() == WeatherDataSource.WeatherCondition.STORMY) {
                        notes.add("天气提示：" + info.description() + "，建议准备雨具或调整室内行程。");
                    } else {
                        notes.add("天气提示：" + info.description() + "，温度 " + 
                                  info.temperatureMin() + "°C ~ " + info.temperatureMax() + "°C。");
                    }
                }
            }
        }
        
        String pace = req.getPreferences().getPace();
        if ("easy".equals(pace)) {
            notes.add("行程节奏：轻松模式，预留充足休息时间。");
        } else if ("fast".equals(pace)) {
            notes.add("行程节奏：紧凑模式，建议提前规划交通。");
        }
        
        return notes;
    }
    
    private List<TripPlan.Justification> buildJustifications(TripRequirement req) {
        List<TripPlan.Justification> justifications = new ArrayList<>();
        
        TripPlan.Justification just = new TripPlan.Justification();
        just.setChoice("推荐行程组合");
        
        List<String> reasons = new ArrayList<>();
        reasons.add("根据您的偏好（" + req.getPreferences().getPace() + "节奏）精心规划。");
        
        if (req.getPreferences().getFood() != null && !req.getPreferences().getFood().isEmpty()) {
            reasons.add("已优先考虑您喜爱的美食类型：" + String.join("、", req.getPreferences().getFood()) + "。");
        }
        
        if (req.getParty().getKids() > 0) {
            reasons.add("考虑到有" + req.getParty().getKids() + "位儿童，行程安排相对轻松。");
        }
        
        if (req.getConstraints().getMinTransfers() != null && req.getConstraints().getMinTransfers()) {
            reasons.add("已尽量减少换乘次数，提升出行舒适度。");
        }
        
        just.setReasons(reasons);
        justifications.add(just);
        
        return justifications;
    }
    
    private List<TripPlan.Citation> buildCitations(TripRequirement req) {
        List<TripPlan.Citation> citations = new ArrayList<>();
        
        Optional<CitationDataSource> citationDataSource = dataSourceRegistry.getDefaultCitationDataSource();
        
        if (citationDataSource.isPresent()) {
            List<String> keywords = new ArrayList<>();
            keywords.add(req.getCity());
            if (req.getPreferences().getFood() != null) {
                keywords.addAll(req.getPreferences().getFood());
            }
            
            List<CitationDataSource.CitationInfo> foundCitations = 
                citationDataSource.get().searchByKeywords(keywords, 5);
            
            for (CitationDataSource.CitationInfo info : foundCitations) {
                TripPlan.Citation citation = new TripPlan.Citation();
                citation.setId(info.id());
                citation.setSource(info.sourceType());
                citations.add(citation);
            }
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
