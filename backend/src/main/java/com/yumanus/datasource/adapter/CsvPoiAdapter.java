package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.PoiDataSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CsvPoiAdapter implements PoiDataSource {
    
    private static final String SOURCE_NAME = "local-csv";
    
    private final Map<String, PoiInfo> poiCache = new HashMap<>();
    
    public CsvPoiAdapter() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        poiCache.put("poi-sh-001", new PoiInfo(
            "poi-sh-001", "外滩", "上海", "上海市黄浦区中山东一路",
            31.2400, 121.4900, "sightseeing",
            List.of("地标", "夜景", "历史建筑"), 4.8,
            "上海外滩是中国著名的观光景点，位于黄浦江畔。",
            SOURCE_NAME
        ));
        poiCache.put("poi-sh-002", new PoiInfo(
            "poi-sh-002", "豫园", "上海", "上海市黄浦区安仁街132号",
            31.2280, 121.4920, "sightseeing",
            List.of("园林", "历史", "文化"), 4.7,
            "豫园是明代私家园林，是上海著名的古典园林。",
            SOURCE_NAME
        ));
        poiCache.put("poi-sh-003", new PoiInfo(
            "poi-sh-003", "南翔小笼馆", "上海", "上海市黄浦区南京东路",
            31.2350, 121.4800, "lunch",
            List.of("小笼包", "上海菜", "小吃"), 4.5,
            "正宗的上海小笼包，皮薄馅多汤鲜。",
            SOURCE_NAME
        ));
        poiCache.put("poi-cd-001", new PoiInfo(
            "poi-cd-001", "锦里古街", "成都", "四川省成都市武侯区武侯祠大街231号",
            30.6460, 104.0480, "sightseeing",
            List.of("古街", "美食", "夜景"), 4.6,
            "锦里是成都著名的古街，汇集了四川特色小吃和手工艺品。",
            SOURCE_NAME
        ));
        poiCache.put("poi-cd-002", new PoiInfo(
            "poi-cd-002", "宽窄巷子", "成都", "四川省成都市青羊区长顺上街127号",
            30.6630, 104.0460, "sightseeing",
            List.of("老成都", "茶馆", "文化"), 4.5,
            "宽窄巷子是成都三大历史文化保护区之一，由宽巷子、窄巷子和井巷子组成。",
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
