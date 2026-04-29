package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.PoiDataSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ExternalApiPoiAdapter implements PoiDataSource {
    
    private static final String SOURCE_NAME = "external-api-poi";
    
    private final Map<String, PoiInfo> poiCache = new HashMap<>();
    
    public ExternalApiPoiAdapter() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        poiCache.put("ext-sh-001", new PoiInfo(
            "ext-sh-001", "上海迪士尼度假区", "上海", "上海市浦东新区川沙镇黄赵路310号",
            31.1416, 121.6570, "sightseeing",
            List.of("主题乐园", "亲子", "迪士尼"), 4.9,
            "上海迪士尼度假区是中国内地首座迪士尼主题乐园，拥有全球最高的奇幻童话城堡。",
            SOURCE_NAME
        ));
        poiCache.put("ext-sh-002", new PoiInfo(
            "ext-sh-002", "上海科技馆", "上海", "上海市浦东新区世纪大道2000号",
            31.2170, 121.5390, "sightseeing",
            List.of("科技馆", "亲子", "教育"), 4.7,
            "上海科技馆是中国大陆规模最大的科技馆，以自然、人和科技为主题。",
            SOURCE_NAME
        ));
    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public List<PoiInfo> searchByCity(String city, PoiSearchCriteria criteria) {
        List<PoiInfo> result = new ArrayList<>();
        
        for (PoiInfo poi : poiCache.values()) {
            if (poi.city().equalsIgnoreCase(city) || 
                poi.city().contains(city) ||
                city.contains(poi.city())) {
                
                if (criteria.activityType() != null && 
                    !criteria.activityType().isEmpty() &&
                    !poi.activityType().equals(criteria.activityType())) {
                    continue;
                }
                
                if (criteria.tags() != null && !criteria.tags().isEmpty()) {
                    boolean hasMatchingTag = criteria.tags().stream()
                        .anyMatch(tag -> poi.tags().contains(tag));
                    if (!hasMatchingTag) {
                        continue;
                    }
                }
                
                if (criteria.minRating() != null && poi.rating() < criteria.minRating()) {
                    continue;
                }
                
                result.add(poi);
            }
        }
        
        if (criteria.limit() != null && criteria.limit() > 0 && result.size() > criteria.limit()) {
            return result.subList(0, criteria.limit());
        }
        
        return result;
    }
    
    @Override
    public Optional<PoiInfo> getById(String poiId) {
        return Optional.ofNullable(poiCache.get(poiId));
    }
    
    @Override
    public List<PoiInfo> recommendByTags(String city, List<String> tags, int limit) {
        return searchByCity(city, new PoiSearchCriteria(null, tags, null, limit));
    }
}
