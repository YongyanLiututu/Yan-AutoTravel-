package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.WeatherDataSource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
public class MockWeatherAdapter implements WeatherDataSource {
    
    private static final String SOURCE_NAME = "mock-weather";
    private final Random random = new Random();
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public Optional<WeatherInfo> getCurrentWeather(String city) {
        return Optional.of(generateWeather(city, LocalDate.now()));
    }
    
    @Override
    public List<WeatherInfo> getForecast(String city, int days) {
        List<WeatherInfo> forecast = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < days; i++) {
            forecast.add(generateWeather(city, today.plusDays(i)));
        }
        
        return forecast;
    }
    
    @Override
    public Optional<WeatherInfo> getWeatherForDate(String city, LocalDate date) {
        return Optional.of(generateWeather(city, date));
    }
    
    private WeatherInfo generateWeather(String city, LocalDate date) {
        WeatherCondition[] conditions = WeatherCondition.values();
        WeatherCondition condition = conditions[random.nextInt(conditions.length - 1)];
        
        int baseTemp = switch (city) {
            case "三亚", "海口" -> 28;
            case "哈尔滨", "长春" -> -5;
            case "北京", "天津" -> 15;
            case "上海", "杭州", "南京" -> 20;
            case "成都", "重庆" -> 22;
            default -> 18;
        };
        
        int tempVariation = random.nextInt(10) - 5;
        int minTemp = baseTemp - 5 + tempVariation;
        int maxTemp = baseTemp + 5 + tempVariation;
        
        String description = switch (condition) {
            case SUNNY -> "晴朗，适合户外活动";
            case CLOUDY -> "多云，天气舒适";
            case RAINY -> "有雨，建议携带雨具";
            case SNOWY -> "有雪，注意保暖";
            case STORMY -> "有暴风雨，建议待在室内";
            case FOGGY -> "有雾，注意出行安全";
            default -> "天气状况未知";
        };
        
        return new WeatherInfo(
            city,
            date,
            condition,
            minTemp,
            maxTemp,
            50 + random.nextInt(40),
            2.5 + random.nextDouble() * 10,
            description,
            SOURCE_NAME
        );
    }
}
