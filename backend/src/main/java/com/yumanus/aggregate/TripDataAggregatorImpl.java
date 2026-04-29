package com.yumanus.aggregate;

import com.yumanus.aggregate.model.AggregatedTripData;
import com.yumanus.aggregate.model.AggregatedTripData.DailyAggregatedData;
import com.yumanus.aggregate.strategy.DataSourceStrategySelector;
import com.yumanus.aggregate.strategy.StrategySelectorFactory;
import com.yumanus.api.dto.TripRequirement;
import com.yumanus.datasource.port.*;
import com.yumanus.datasource.registry.DataSourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TripDataAggregatorImpl implements TripDataAggregator {
    
    private static final Logger logger = LoggerFactory.getLogger(TripDataAggregatorImpl.class);
    
    private final DataSourceRegistry dataSourceRegistry;
    private final StrategySelectorFactory strategySelectorFactory;
    
    public TripDataAggregatorImpl(DataSourceRegistry dataSourceRegistry,
                                   StrategySelectorFactory strategySelectorFactory) {
        this.dataSourceRegistry = dataSourceRegistry;
        this.strategySelectorFactory = strategySelectorFactory;
    }
    
    @Override
    public AggregatedTripData aggregate(TripRequirement requirement) {
        return aggregate(requirement, AggregatedTripData.DataSourceStrategy.COST_PRIORITY);
    }
    
    @Override
    public AggregatedTripData aggregate(TripRequirement requirement, 
                                         AggregatedTripData.DataSourceStrategy strategy) {
        logger.info("Starting data aggregation for city: {}, days: {}, strategy: {}", 
            requirement.getCity(), requirement.getDays(), strategy);
        
        AggregatedTripData aggregatedData = new AggregatedTripData(
            requirement.getCity(),
            requirement.getDays(),
            requirement.getStartDate()
        );
        aggregatedData.setStrategy(strategy);
        
        DataSourceStrategySelector selector = strategySelectorFactory.getSelector(strategy);
        
        List<PoiDataSource.PoiInfo> allPois = aggregatePoiData(requirement, selector);
        aggregatedData.setAllPois(allPois);
        
        List<DailyAggregatedData> dailyData = aggregateDailyData(requirement, selector);
        aggregatedData.setDailyData(dailyData);
        
        List<AggregatedTripData.CitationInfo> citations = aggregateCitations(requirement, selector);
        aggregatedData.setCitations(citations);
        
        logger.info("Data aggregation completed: {} POIs, {} daily records, {} citations",
            allPois.size(), dailyData.size(), citations.size());
        
        return aggregatedData;
    }
    
    private List<PoiDataSource.PoiInfo> aggregatePoiData(TripRequirement requirement,
                                                           DataSourceStrategySelector selector) {
        List<PoiDataSource> allPoiSources = dataSourceRegistry.getAllPoiDataSources();
        Optional<PoiDataSource> selectedSource = selector.selectPoiDataSource(allPoiSources);
        
        if (selectedSource.isEmpty()) {
            logger.warn("No POI data source available");
            return new ArrayList<>();
        }
        
        PoiDataSource poiSource = selectedSource.get();
        logger.info("Using POI data source: {}", poiSource.getSourceName());
        
        List<String> tags = new ArrayList<>();
        if (requirement.getPreferences().getFood() != null) {
            tags.addAll(requirement.getPreferences().getFood());
        }
        if (requirement.getPreferences().getAvoid() != null) {
        }
        
        PoiDataSource.PoiSearchCriteria criteria = new PoiDataSource.PoiSearchCriteria(
            null,
            tags,
            4.0,
            20
        );
        
        return poiSource.searchByCity(requirement.getCity(), criteria);
    }
    
    private List<DailyAggregatedData> aggregateDailyData(TripRequirement requirement,
                                                           DataSourceStrategySelector selector) {
        List<DailyAggregatedData> dailyDataList = new ArrayList<>();
        
        List<WeatherDataSource> weatherSources = List.of();
        Optional<WeatherDataSource> weatherSource = selector.selectWeatherDataSource(
            dataSourceRegistry.getDefaultWeatherDataSource().map(List::of).orElse(List.of())
        );
        
        for (int i = 0; i < requirement.getDays(); i++) {
            LocalDate date = requirement.getStartDate().plusDays(i);
            DailyAggregatedData dailyData = new DailyAggregatedData(date, i);
            
            if (weatherSource.isPresent()) {
                Optional<WeatherDataSource.WeatherInfo> weather = 
                    weatherSource.get().getWeatherForDate(requirement.getCity(), date);
                weather.ifPresent(dailyData::setWeather);
            }
            
            Optional<TransitDataSource> transitSource = selector.selectTransitDataSource(
                dataSourceRegistry.getTransitDataSources(TransitDataSource.TransitMode.TRANSIT),
                TransitDataSource.TransitMode.TRANSIT
            );
            
            if (transitSource.isPresent()) {
                TransitDataSource.TransitSearchRequest request = new TransitDataSource.TransitSearchRequest(
                    requirement.getCity(),
                    requirement.getCity(),
                    "酒店",
                    "景点",
                    null, null, null, null,
                    date,
                    TransitDataSource.TransitMode.TRANSIT,
                    requirement.getPreferences().getPace()
                );
                
                List<TransitDataSource.TransitOption> options = 
                    transitSource.get().searchRoutes(request);
                options.forEach(dailyData::addTransit);
            }
            
            dailyDataList.add(dailyData);
        }
        
        return dailyDataList;
    }
    
    private List<AggregatedTripData.CitationInfo> aggregateCitations(TripRequirement requirement,
                                                                      DataSourceStrategySelector selector) {
        List<AggregatedTripData.CitationInfo> citations = new ArrayList<>();
        
        Optional<CitationDataSource> citationSource = selector.selectCitationDataSource(
            dataSourceRegistry.getDefaultCitationDataSource().map(List::of).orElse(List.of())
        );
        
        if (citationSource.isEmpty()) {
            logger.warn("No Citation data source available");
            return citations;
        }
        
        CitationDataSource source = citationSource.get();
        logger.info("Using Citation data source: {}", source.getSourceName());
        
        List<String> keywords = new ArrayList<>();
        keywords.add(requirement.getCity());
        if (requirement.getPreferences().getFood() != null) {
            keywords.addAll(requirement.getPreferences().getFood());
        }
        
        List<CitationDataSource.CitationInfo> foundCitations = 
            source.searchByKeywords(keywords, 5);
        
        for (CitationDataSource.CitationInfo info : foundCitations) {
            citations.add(new AggregatedTripData.CitationInfo(
                info.id(),
                info.sourceType(),
                info.sourceType(),
                info.title()
            ));
        }
        
        return citations;
    }
}
