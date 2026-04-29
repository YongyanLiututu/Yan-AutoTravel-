package com.yumanus.datasource.port;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransitDataSource {
    
    String getSourceName();
    
    TransitMode getSupportedMode();
    
    List<TransitOption> searchRoutes(TransitSearchRequest request);
    
    Optional<TransitOption> getBestRoute(TransitSearchRequest request);
    
    int estimateDuration(String fromCity, String toCity, TransitMode mode);
    
    enum TransitMode {
        TRAIN, FLIGHT, BUS, WALK, DRIVE, TRANSIT
    }
    
    record TransitSearchRequest(
        String fromCity,
        String toCity,
        String fromAddress,
        String toAddress,
        Double fromLat,
        Double fromLng,
        Double toLat,
        Double toLng,
        LocalDate date,
        TransitMode mode,
        String preferType
    ) {}
    
    record TransitOption(
        String id,
        TransitMode mode,
        String from,
        String to,
        int durationMinutes,
        double price,
        String code,
        List<TransitStep> steps,
        String sourceId
    ) {
        public TransitOption(String id, TransitMode mode, String from, String to, 
                            int durationMinutes, double price, String code, String sourceId) {
            this(id, mode, from, to, durationMinutes, price, code, List.of(), sourceId);
        }
    }
    
    record TransitStep(
        String mode,
        String from,
        String to,
        int durationMinutes,
        String line,
        String departureTime
    ) {}
}
