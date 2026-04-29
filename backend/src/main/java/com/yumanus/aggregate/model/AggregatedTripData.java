package com.yumanus.aggregate.model;

import com.yumanus.datasource.port.PoiDataSource;
import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.datasource.port.WeatherDataSource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AggregatedTripData {
    
    private String city;
    private int days;
    private LocalDate startDate;
    private List<DailyAggregatedData> dailyData = new ArrayList<>();
    private List<PoiDataSource.PoiInfo> allPois = new ArrayList<>();
    private List<TransitDataSource.TransitOption> transitOptions = new ArrayList<>();
    private List<CitationInfo> citations = new ArrayList<>();
    private DataSourceStrategy strategy;
    
    public AggregatedTripData() {}
    
    public AggregatedTripData(String city, int days, LocalDate startDate) {
        this.city = city;
        this.days = days;
        this.startDate = startDate;
    }
    
    public void addDailyData(DailyAggregatedData daily) {
        this.dailyData.add(daily);
    }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public List<DailyAggregatedData> getDailyData() { return dailyData; }
    public void setDailyData(List<DailyAggregatedData> dailyData) { this.dailyData = dailyData; }
    public List<PoiDataSource.PoiInfo> getAllPois() { return allPois; }
    public void setAllPois(List<PoiDataSource.PoiInfo> allPois) { this.allPois = allPois; }
    public List<TransitDataSource.TransitOption> getTransitOptions() { return transitOptions; }
    public void setTransitOptions(List<TransitDataSource.TransitOption> transitOptions) { this.transitOptions = transitOptions; }
    public List<CitationInfo> getCitations() { return citations; }
    public void setCitations(List<CitationInfo> citations) { this.citations = citations; }
    public DataSourceStrategy getStrategy() { return strategy; }
    public void setStrategy(DataSourceStrategy strategy) { this.strategy = strategy; }
    
    public static class DailyAggregatedData {
        private LocalDate date;
        private int dayIndex;
        private List<PoiDataSource.PoiInfo> pois = new ArrayList<>();
        private List<TransitDataSource.TransitOption> transit = new ArrayList<>();
        private WeatherDataSource.WeatherInfo weather;
        
        public DailyAggregatedData() {}
        
        public DailyAggregatedData(LocalDate date, int dayIndex) {
            this.date = date;
            this.dayIndex = dayIndex;
        }
        
        public void addPoi(PoiDataSource.PoiInfo poi) { this.pois.add(poi); }
        public void addTransit(TransitDataSource.TransitOption option) { this.transit.add(option); }
        
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public int getDayIndex() { return dayIndex; }
        public void setDayIndex(int dayIndex) { this.dayIndex = dayIndex; }
        public List<PoiDataSource.PoiInfo> getPois() { return pois; }
        public void setPois(List<PoiDataSource.PoiInfo> pois) { this.pois = pois; }
        public List<TransitDataSource.TransitOption> getTransit() { return transit; }
        public void setTransit(List<TransitDataSource.TransitOption> transit) { this.transit = transit; }
        public WeatherDataSource.WeatherInfo getWeather() { return weather; }
        public void setWeather(WeatherDataSource.WeatherInfo weather) { this.weather = weather; }
    }
    
    public static class CitationInfo {
        private String id;
        private String source;
        private String sourceType;
        private String title;
        
        public CitationInfo() {}
        
        public CitationInfo(String id, String source, String sourceType, String title) {
            this.id = id;
            this.source = source;
            this.sourceType = sourceType;
            this.title = title;
        }
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getSourceType() { return sourceType; }
        public void setSourceType(String sourceType) { this.sourceType = sourceType; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }
    
    public enum DataSourceStrategy {
        COST_PRIORITY("成本优先", "优先选择查询成本较低的数据源，可能数据新鲜度稍低"),
        FRESHNESS_PRIORITY("新鲜度优先", "优先选择数据最新的数据源，可能查询成本较高");
        
        private final String name;
        private final String description;
        
        DataSourceStrategy(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() { return name; }
        public String getDescription() { return description; }
    }
}
