package com.yumanus.aggregate.strategy;

import com.yumanus.datasource.port.PoiDataSource;
import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.datasource.port.WeatherDataSource;
import com.yumanus.datasource.port.CitationDataSource;

import java.util.List;
import java.util.Optional;

public interface DataSourceStrategySelector {
    
    String getStrategyName();
    
    int getPriority();
    
    default Optional<PoiDataSource> selectPoiDataSource(List<PoiDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(0));
    }
    
    default Optional<TransitDataSource> selectTransitDataSource(
            List<TransitDataSource> candidates, 
            TransitDataSource.TransitMode mode) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(0));
    }
    
    default Optional<WeatherDataSource> selectWeatherDataSource(List<WeatherDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(0));
    }
    
    default Optional<CitationDataSource> selectCitationDataSource(List<CitationDataSource> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(0));
    }
}
