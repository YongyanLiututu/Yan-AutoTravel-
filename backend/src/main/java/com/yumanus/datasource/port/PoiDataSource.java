package com.yumanus.datasource.port;

import java.util.List;
import java.util.Optional;

public interface PoiDataSource {
    
    String getSourceName();
    
    List<PoiInfo> searchByCity(String city, PoiSearchCriteria criteria);
    
    Optional<PoiInfo> getById(String poiId);
    
    List<PoiInfo> recommendByTags(String city, List<String> tags, int limit);
    
    record PoiSearchCriteria(
        String activityType,
        List<String> tags,
        Double minRating,
        Integer limit
    ) {}
    
    record PoiInfo(
        String id,
        String name,
        String city,
        String address,
        Double lat,
        Double lng,
        String activityType,
        List<String> tags,
        Double rating,
        String description,
        String sourceId
    ) {}
}
