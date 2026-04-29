package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.tools.McpClients;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class McpFlightAdapter implements TransitDataSource {
    
    private static final String SOURCE_NAME = "mcp-flight";
    private final McpClients mcpClients;
    
    public McpFlightAdapter(McpClients mcpClients) {
        this.mcpClients = mcpClients;
    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public TransitMode getSupportedMode() {
        return TransitMode.FLIGHT;
    }
    
    @Override
    public List<TransitOption> searchRoutes(TransitSearchRequest request) {
        return List.of(
            new TransitOption(
                "flight-ca1234",
                TransitMode.FLIGHT,
                request.fromCity(),
                request.toCity(),
                120,
                1200.0,
                "CA1234",
                SOURCE_NAME
            ),
            new TransitOption(
                "flight-mu5678",
                TransitMode.FLIGHT,
                request.fromCity(),
                request.toCity(),
                130,
                1100.0,
                "MU5678",
                SOURCE_NAME
            )
        );
    }
    
    @Override
    public Optional<TransitOption> getBestRoute(TransitSearchRequest request) {
        List<TransitOption> options = searchRoutes(request);
        return options.stream()
            .min((a, b) -> {
                if ("fast".equals(request.preferType())) {
                    return Integer.compare(a.durationMinutes(), b.durationMinutes());
                } else {
                    return Double.compare(a.price(), b.price());
                }
            });
    }
    
    @Override
    public int estimateDuration(String fromCity, String toCity, TransitMode mode) {
        if (mode != TransitMode.FLIGHT) {
            return -1;
        }
        return 120;
    }
    
    public String getFlightServerUrl() {
        return mcpClients.getFlightServer();
    }
}
