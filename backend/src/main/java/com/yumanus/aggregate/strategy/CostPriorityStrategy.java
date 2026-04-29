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
public class CostPriorityStrategy implements DataSourceStrategySelector {
    
    private static final Logger logger = LoggerFactory.getLogger(CostPriorityStrategy.class);
    
    private static final String STRATEGY_NAME = "COST_PRIORITY";
    
    private static final Map<String, Integer> SOURCE_COST_RANKING = new ConcurrentHashMap<>();
    
    static {
        SOURCE_COST_RANKING.put("local-csv", 1);
        SOURCE_COST_RANKING.put("mock-weather", 1);
        SOURCE_COST_RANKING.put("rag-local", 2);
        SOURCE_COST_RANKING.put("mcp-amap", 3);
        SOURCE_COST_RANKING.put("mcp-12306", 3);
        SOURCE_COST_RANKING.put("mcp-flight", 4);
        SOURCE_COST_RANKING.put("external-api-poi", 5);
    }
    
    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }
    
    @Override
    public int getPriority() {
        return AggregatedTripData.DataSourceStrategy.COST_PRIORITY.ordinal();
    }
    
    @Override
    public Optional<PoiDataSource> selectPoiDataSource(List<PoiDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting POI data source with COST_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(PoiDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_COST_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<TransitDataSource> selectTransitDataSource(
            List<TransitDataSource> candidates, 
            TransitDataSource.TransitMode mode) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Transit data source with COST_PRIORITY strategy for mode: {}, candidates: {}", 
            mode, candidates.stream().map(TransitDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_COST_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<WeatherDataSource> selectWeatherDataSource(List<WeatherDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Weather data source with COST_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(WeatherDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_COST_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    @Override
    public Optional<CitationDataSource> selectCitationDataSource(List<CitationDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        
        logger.info("Selecting Citation data source with COST_PRIORITY strategy, candidates: {}", 
            candidates.stream().map(CitationDataSource::getSourceName).toList());
        
        return candidates.stream()
            .min(Comparator.comparingInt(
                ds -> SOURCE_COST_RANKING.getOrDefault(ds.getSourceName(), Integer.MAX_VALUE)
            ));
    }
    
    public static int getSourceCost(String sourceName) {
        return SOURCE_COST_RANKING.getOrDefault(sourceName, Integer.MAX_VALUE);
    }
}
