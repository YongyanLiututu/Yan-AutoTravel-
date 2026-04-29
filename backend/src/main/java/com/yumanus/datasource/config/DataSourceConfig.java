package com.yumanus.datasource.config;

import com.yumanus.datasource.port.*;
import com.yumanus.datasource.registry.DataSourceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSourceRegistry dataSourceRegistry(List<PoiDataSource> poiDataSources,
                                                  List<TransitDataSource> transitDataSources,
                                                  List<WeatherDataSource> weatherDataSources,
                                                  List<CitationDataSource> citationDataSources) {
        DataSourceRegistry registry = new DataSourceRegistry();
        
        poiDataSources.forEach(registry::registerPoiDataSource);
        transitDataSources.forEach(registry::registerTransitDataSource);
        weatherDataSources.forEach(registry::registerWeatherDataSource);
        citationDataSources.forEach(registry::registerCitationDataSource);
        
        return registry;
    }
}
