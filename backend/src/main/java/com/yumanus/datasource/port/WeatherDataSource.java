package com.yumanus.datasource.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeatherDataSource {
    
    String getSourceName();
    
    Optional<WeatherInfo> getCurrentWeather(String city);
    
    List<WeatherInfo> getForecast(String city, int days);
    
    Optional<WeatherInfo> getWeatherForDate(String city, LocalDate date);
    
    enum WeatherCondition {
        SUNNY, CLOUDY, RAINY, SNOWY, STORMY, FOGGY, UNKNOWN
    }
    
    record WeatherInfo(
        String city,
        LocalDate date,
        WeatherCondition condition,
        int temperatureMin,
        int temperatureMax,
        int humidity,
        double windSpeed,
        String description,
        String sourceId
    ) {}
}
