package com.yumanus.datasource.adapter;

import com.yumanus.datasource.port.TransitDataSource;
import com.yumanus.tools.McpClients;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class McpTrainAdapter implements TransitDataSource {
    
    private static final String SOURCE_NAME = "mcp-12306";
    
    private final McpClients mcpClients;
    
    public McpTrainAdapter(McpClients mcpClients) {
        this.mcpClients = mcpClients;
    }
    
    @Override
    public String getSourceName() {
        return SOURCE_NAME;
    }
    
    @Override
    public TransitMode getSupportedMode() {
        return TransitMode.TRAIN;
    }
    
    @Override
    public List<TransitOption> searchRoutes(TransitSearchRequest request) {
        return List.of(
            new TransitOption(
                "train-g123",
                TransitMode.TRAIN,
                request.fromCity(),
                request.toCity(),
                323,
                350.0,
                "G123",
                SOURCE_NAME
            ),
            new TransitOption(
                "train-d456",
                TransitMode.TRAIN,
                request.fromCity(),
                request.toCity(),
                420,
                280.0,
                "D456",
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
        if (mode != TransitMode.TRAIN) {
            return -1;
        }
        return 300;
    }
    
    public String getTrainServerUrl() {
        return mcpClients.getTrainServer();
    }
}
