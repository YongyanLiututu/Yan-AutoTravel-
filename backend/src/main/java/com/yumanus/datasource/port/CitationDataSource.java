package com.yumanus.datasource.port;

import java.util.List;
import java.util.Optional;

public interface CitationDataSource {
    
    String getSourceName();
    
    List<CitationInfo> searchByKeywords(List<String> keywords, int limit);
    
    Optional<CitationInfo> getById(String id);
    
    List<CitationInfo> getBySourceType(String sourceType);
    
    record CitationInfo(
        String id,
        String source,
        String sourceType,
        String title,
        String content,
        String url,
        List<String> tags,
        String sourceId
    ) {}
}
