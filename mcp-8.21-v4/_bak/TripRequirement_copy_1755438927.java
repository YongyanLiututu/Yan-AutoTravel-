package com.Yan-AutoTravel.api.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class TripRequirement {
    @NotBlank
    private String city;
    @NotNull
    private LocalDate startDate;
    @Min(1)
    @Max(30)
    private int days;
    @NotNull
    private Party party;
    @NotNull
    private Budget budget;
    private Preferences preferences;
    private Constraints constraints;

    public static class Party {
        @Min(0) private int adults;
        @Min(0) private int kids;

        public int getAdults() { return adults; }
        public void setAdults(int adults) { this.adults = adults; }
        public int getKids() { return kids; }
        public void setKids(int kids) { this.kids = kids; }
    }

    public static class Budget {
        @NotBlank private String currency;
        @Min(0) private long total;
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }

    public static class Preferences {
        private String pace; // easy/normal/fast
        private List<String> food;
        private List<String> avoid;
        public String getPace() { return pace; }
        public void setPace(String pace) { this.pace = pace; }
        public List<String> getFood() { return food; }
        public void setFood(List<String> food) { this.food = food; }
        public List<String> getAvoid() { return avoid; }
        public void setAvoid(List<String> avoid) { this.avoid = avoid; }
    }

    public static class Constraints {
        private Integer walkMaxKmPerDay;
        private Boolean minTransfers;
        private Boolean rainPlan;
        public Integer getWalkMaxKmPerDay() { return walkMaxKmPerDay; }
        public void setWalkMaxKmPerDay(Integer walkMaxKmPerDay) { this.walkMaxKmPerDay = walkMaxKmPerDay; }
        public Boolean getMinTransfers() { return minTransfers; }
        public void setMinTransfers(Boolean minTransfers) { this.minTransfers = minTransfers; }
        public Boolean getRainPlan() { return rainPlan; }
        public void setRainPlan(Boolean rainPlan) { this.rainPlan = rainPlan; }
    }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public Party getParty() { return party; }
    public void setParty(Party party) { this.party = party; }
    public Budget getBudget() { return budget; }
    public void setBudget(Budget budget) { this.budget = budget; }
    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }
    public Constraints getConstraints() { return constraints; }
    public void setConstraints(Constraints constraints) { this.constraints = constraints; }
}


