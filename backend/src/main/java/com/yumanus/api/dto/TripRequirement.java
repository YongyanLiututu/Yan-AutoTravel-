package com.Yan-AutoTravel.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TripRequirement {
    @NotBlank
    @JsonSetter(nulls = Nulls.SKIP)
    private String city = "Shanghai";

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd") // 解析 "2025-08-20"
    @JsonSetter(nulls = Nulls.SKIP)
    private LocalDate startDate = LocalDate.now();

    @Min(1) @Max(30)
    private int days = 3;

    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    private Party party = new Party();

    @NotNull
    @JsonSetter(nulls = Nulls.SKIP)
    private Budget budget = new Budget();

    @JsonSetter(nulls = Nulls.SKIP)
    private Preferences preferences = new Preferences();

    @JsonSetter(nulls = Nulls.SKIP)
    private Constraints constraints = new Constraints();

    // ----- 内部�?-----

    public static class Party {
        @Min(0) private int adults;
        @Min(0) private int kids;

        public Party() {                 // 默认值，避免�?null �?0/负导致校验不通过
            this.adults = 2;
            this.kids = 0;
        }

        public int getAdults() { return adults; }
        public void setAdults(int adults) { this.adults = adults; }
        public int getKids() { return kids; }
        public void setKids(int kids) { this.kids = kids; }
    }

    public static class Budget {
        @NotBlank private String currency;
        @Min(0) private long total;

        public Budget() {                // 默认值，避免�?null 触发 @NotNull/@NotBlank
            this.currency = "CNY";
            this.total = 3000;
        }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }

    public static class Preferences {
        private String pace;             // easy/normal/fast
        private List<String> food;
        private List<String> avoid;

        public Preferences() {           // 给个默认节奏
            this.pace = "normal";
        }

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

        public Constraints() {
            this.walkMaxKmPerDay = 8;
            this.minTransfers = Boolean.TRUE;
            this.rainPlan = Boolean.TRUE;
        }

        public Integer getWalkMaxKmPerDay() { return walkMaxKmPerDay; }
        public void setWalkMaxKmPerDay(Integer walkMaxKmPerDay) { this.walkMaxKmPerDay = walkMaxKmPerDay; }
        public Boolean getMinTransfers() { return minTransfers; }
        public void setMinTransfers(Boolean minTransfers) { this.minTransfers = minTransfers; }
        public Boolean getRainPlan() { return rainPlan; }
        public void setRainPlan(Boolean rainPlan) { this.rainPlan = rainPlan; }
    }

    // ----- 原有 getter/setter 保留 -----

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
