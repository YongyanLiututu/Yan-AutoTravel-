package com.yumanus.aggregate.strategy;

import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.datasource.port.PoiDataSource;
import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.datasource.port.WeatherDataSource;
import com.yumanus.datasource.port.CitationDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FreshnessPriorityStrategy implements DataSourceStrategySelector {
    
    private static final Logger logger = LoggerFactory.getLogger(FreshnessPriorityStrategy.class);
    
    private static final String STRATEGY_NAME = "FRESHNESS_PRIORITY";
    
    private static final Map<String, Integer> SOURCE_FRESHNESS_RANKING = new ConcurrentHashMap<>();
    
    static {
        SOURCE_FRESHNESS_RANKING.put("external-api-poi", 1);
        SOURCE_FRESHNESS_RANKING.put("mcp-flight", 2);
        SOURCE_FRESHNESS_RANKING.put("mcp-12306", 3);
        SOURCE_FRESHNESS_RANKING.put("mcp-amap", 4);
        SOURCE_FRESHNESS_RANKING.put("rag-local", 5);
        SOURCE_FRESHNESS_RANKING.put("local-csv", 6);
        SOURCE_FRESHNESS_RANKING.put("mock-weather", 7);
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    @Override
    public int getPriority() {
        return AggregatedTripData.DataSourceStrategy.FRESHNESS_PRIORITY.ordinal();
    }
    
    @Override
    public Optional<PoiDataSource> selectPoiDataSource(List<PoiDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting POI data source with FRESHNESS_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(PoiDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_FRESHNESS_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<TransitDataSource> selectTransitDataSource(
            List<TransitDataSource> candidates, 
            TransitDataSource.TransitMode mode) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Transit data source with FRESHNESS_PRIORITY strategy for mode: {}, candidates: {}", 
            mode, candidates.stream().map(TransitDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_FRESHNESS_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<WeatherDataSource> selectWeatherDataSource(List<WeatherDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Weather data source with FRESHNESS_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(WeatherDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_FRESHNESS_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<CitationDataSource> selectCitationDataSource(List<CitationDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Citation data source with FRESHNESS_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(CitationDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_FRESHNESS_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    public static int getSourceFreshness(String sourceName) {
        return SOURCE_FRESHNESS_RANKING.getOrDefault(sourceName, Integer.MAX_VALUE);
    }
}
