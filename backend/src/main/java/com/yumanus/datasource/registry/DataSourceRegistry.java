package com.yumanus.datasource.registry;

import com.yumanus.datasource.port.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataSourceRegistry {
    
    private final Map<String, PoiDataSource> poiDataSources = new ConcurrentHashMap<>();
    private final Map<TransitDataSource.TransitMode, List<TransitDataSource>> transitDataSources = new ConcurrentHashMap<>();
    private final Map<String, WeatherDataSource> weatherDataSources = new ConcurrentHashMap<>();
    private final Map<String, CitationDataSource> citationDataSources = new ConcurrentHashMap<>();
    
    public void registerPoiDataSource(PoiDataSource dataSource) {
        poiDataSources.put(dataSource.getSourceName(), dataSource);
    }
    
    public void registerTransitDataSource(TransitDataSource dataSource) {
        transitDataSources.computeIfAbsent(dataSource.getSupportedMode(), k -> new ArrayList<>())
                          .add(dataSource);
    }
    
    public void registerWeatherDataSource(WeatherDataSource dataSource) {
        weatherDataSources.put(dataSource.getSourceName(), dataSource);
    }
    
    public void registerCitationDataSource(CitationDataSource dataSource) {
        citationDataSources.put(dataSource.getSourceName(), dataSource);
    }
    
    public Optional<PoiDataSource> getPoiDataSource(String name) {
        return Optional.ofNullable(poiDataSources.get(name));
    }
    
    public List<PoiDataSource> getAllPoiDataSources() {
        return new ArrayList<>(poiDataSources.values());
    }
    
    public Optional<PoiDataSource> getDefaultPoiDataSource() {
        return poiDataSources.values().stream().findFirst();
    }
    
    public List<TransitDataSource> getTransitDataSources(TransitDataSource.TransitMode mode) {
        return transitDataSources.getOrDefault(mode, Collections.emptyList());
    }
    
    public Optional<TransitDataSource> getDefaultTransitDataSource(TransitDataSource.TransitMode mode) {
        List<TransitDataSource> sources = getTransitDataSources(mode);
        return sources.isEmpty() ? Optional.empty() : Optional.of(sources.get(0));
    }
    
    public Optional<WeatherDataSource> getWeatherDataSource(String name) {
        return Optional.ofNullable(weatherDataSources.get(name));
    }
    
    public Optional<WeatherDataSource> getDefaultWeatherDataSource() {
        return weatherDataSources.values().stream().findFirst();
    }
    
    public Optional<CitationDataSource> getCitationDataSource(String name) {
        return Optional.ofNullable(citationDataSources.get(name));
    }
    
    public Optional<CitationDataSource> getDefaultCitationDataSource() {
        return citationDataSources.values().stream().findFirst();
    }
    
    public Set<String> getRegisteredPoiSources() {
        return Collections.unmodifiableSet(poiDataSources.keySet());
    }
    
    public Set<TransitDataSource.TransitMode> getRegisteredTransitModes() {
        return Collections.unmodifiableSet(transitDataSources.keySet());
    }
}
