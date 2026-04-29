package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.tools.McpClients;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class McpAmapAdapter implements TransitDataSource {
    
    private static final String SOURCE_NAME = "mcp-amap";
    
    private final McpClients mcpClients;
    
    public McpAmapAdapter(McpClients mcpClients) {
        this.mcpClients = mcpClients;
    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public TransitMode getSupportedMode() {
        return TransitMode.TRANSIT;
    }
    
    @Override
    public List<TransitOption> searchRoutes(TransitSearchRequest request) {
        return List.of(
            new TransitOption(
                "amap-transit-001",
                TransitMode.TRANSIT,
                request.fromCity(),
                request.toCity(),
                45,
                8.0,
                "地铁1号线",
                List.of(
                    new TransitStep("subway", "起点站", "人民广场", 20, "1号线", "09:00"),
                    new TransitStep("walk", "人民广场", "换乘站", 5, null, null),
                    new TransitStep("subway", "换乘站", "终点站", 15, "2号线", "09:30")
                ),
                SOURCE_NAME
            )
        );
    }
    
    @Override
    public Optional<TransitOption> getBestRoute(TransitSearchRequest request) {
        List<TransitOption> options = searchRoutes(request);
        return options.isEmpty() ? Optional.empty() : Optional.of(options.get(0));
    }
    
    @Override
    public int estimateDuration(String fromCity, String toCity, TransitMode mode) {
        if (mode != TransitMode.TRANSIT && mode != TransitMode.WALK && mode != TransitMode.DRIVE) {
            return -1;
        }
        return 60;
    }
    
    public String getAmapServerUrl() {
        return mcpClients.getAmapServer();
    }
}
