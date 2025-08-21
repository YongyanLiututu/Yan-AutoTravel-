package com.Yan-AutoTravel.api.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TripPlan {
    private Summary summary;
    private List<DayPlan> days = new ArrayList<>();
    private List<Justification> justifications = new ArrayList<>();
    private List<Citation> citations = new ArrayList<>();
    private List<Alternative> alternatives = new ArrayList<>();

    public static class Summary {
        private String city;
        private int days;
        private Map<String, Long> estCost;
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public int getDays() { return days; }
        public void setDays(int days) { this.days = days; }
        public Map<String, Long> getEstCost() { return estCost; }
        public void setEstCost(Map<String, Long> estCost) { this.estCost = estCost; }
    }

    public static class DayPlan {
        private LocalDate date;
        private List<Slot> slots = new ArrayList<>();
        private List<Transit> transit = new ArrayList<>();
        private List<String> notes = new ArrayList<>();
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public List<Slot> getSlots() { return slots; }
        public void setSlots(List<Slot> slots) { this.slots = slots; }
        public List<Transit> getTransit() { return transit; }
        public void setTransit(List<Transit> transit) { this.transit = transit; }
        public List<String> getNotes() { return notes; }
        public void setNotes(List<String> notes) { this.notes = notes; }
    }

    public static class Slot {
        private String time;
        private String poiId;
        private String activity;
        private List<String> evidenceIds;
        private List<String> alt;
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getPoiId() { return poiId; }
        public void setPoiId(String poiId) { this.poiId = poiId; }
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }
        public List<String> getEvidenceIds() { return evidenceIds; }
        public void setEvidenceIds(List<String> evidenceIds) { this.evidenceIds = evidenceIds; }
        public List<String> getAlt() { return alt; }
        public void setAlt(List<String> alt) { this.alt = alt; }
    }

    public static class Transit {
        private String from;
        private String to;
        private String mode;
        private int etaMin;
        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public int getEtaMin() { return etaMin; }
        public void setEtaMin(int etaMin) { this.etaMin = etaMin; }
    }

    public static class Justification {
        private String choice;
        private List<String> reasons;
        public String getChoice() { return choice; }
        public void setChoice(String choice) { this.choice = choice; }
        public List<String> getReasons() { return reasons; }
        public void setReasons(List<String> reasons) { this.reasons = reasons; }
    }

    public static class Citation {
        private String id;
        private String source;
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
    }

    public static class Alternative {
        private String _for;
        private List<String> options;
        public String getFor() { return _for; }
        public void setFor(String _for) { this._for = _for; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
    }

    public Summary getSummary() { return summary; }
    public void setSummary(Summary summary) { this.summary = summary; }
    public List<DayPlan> getDays() { return days; }
    public void setDays(List<DayPlan> days) { this.days = days; }
    public List<Justification> getJustifications() { return justifications; }
    public void setJustifications(List<Justification> justifications) { this.justifications = justifications; }
    public List<Citation> getCitations() { return citations; }
    public void setCitations(List<Citation> citations) { this.citations = citations; }
    public List<Alternative> getAlternatives() { return alternatives; }
    public void setAlternatives(List<Alternative> alternatives) { this.alternatives = alternatives; }
}


