package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.CitationDataSource;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RagCitationAdapter implements CitationDataSource {
    
    private static final String SOURCE_NAME = "rag-local";
    
    private final Map<String, CitationInfo> citationCache = new HashMap<>();
    
    public RagCitationAdapter() {
        initializeMockData();
    }
    
    private void initializeMockData() {
        citationCache.put("doc:sh:001", new CitationInfo(
            "doc:sh:001",
            "上海旅游指南",
            "guide",
            "上海三日游最佳路线",
            "上海作为国际化大都市，拥有丰富的旅游资源。推荐路线：Day1: 外滩-豫园-南京路；Day2: 迪士尼乐园；Day3: 朱家角古镇。",
            "https://example.com/shanghai-guide",
            List.of("上海", "旅游", "路线"),
            SOURCE_NAME
        ));
        
        citationCache.put("doc:cd:001", new CitationInfo(
            "doc:cd:001",
            "成都美食攻略",
            "food",
            "成都必吃美食推荐",
            "成都被誉为美食之都，必吃美食包括：火锅、串串香、担担面、龙抄手、钟水饺等。推荐火锅店：大龙燚、小龙坎、蜀大侠。",
            "https://example.com/chengdu-food",
            List.of("成都", "美食", "火锅"),
            SOURCE_NAME
        ));
        
        citationCache.put("doc:tip:001", new CitationInfo(
            "doc:tip:001",
            "旅行小贴士",
            "tip",
            "夏季出行注意事项",
            "夏季出行建议：1. 携带防晒霜和遮阳伞；2. 注意补充水分；3. 选择透气轻薄的衣物；4. 避开正午高温时段外出。",
            "https://example.com/travel-tips",
            List.of("旅行", "夏季", "注意事项"),
            SOURCE_NAME
        ));
    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public List<CitationInfo> searchByKeywords(List<String> keywords, int limit) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>(citationCache.values());
        }
        
        List<CitationInfo> result = new ArrayList<>();
        
        for (CitationInfo citation : citationCache.values()) {
            boolean matches = keywords.stream()
                .anyMatch(keyword -> 
                    citation.title().toLowerCase().contains(keyword.toLowerCase()) ||
                    citation.content().toLowerCase().contains(keyword.toLowerCase()) ||
                    citation.tags().stream().anyMatch(tag -> 
                        tag.toLowerCase().contains(keyword.toLowerCase()))
                );
            
            if (matches) {
                result.add(citation);
            }
        }
        
        if (limit > 0 && result.size() > limit) {
            return result.subList(0, limit);
        }
        
        return result;
    }
    
    @Override
    public Optional<CitationInfo> getById(String id) {
        return Optional.ofNullable(citationCache.get(id));
    }
    
    @Override
    public List<CitationInfo> getBySourceType(String sourceType) {
        return citationCache.values().stream()
            .filter(c -> c.sourceType().equalsIgnoreCase(sourceType))
            .toList();
    }
}
