package com.yumanus.aggregate.strategy;

import com.yumanus.aggregate.model.AggregatedTripData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class StrategySelectorFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(StrategySelectorFactory.class);
    
    private final Map<AggregatedTripData.DataSourceStrategy, DataSourceStrategySelector> strategyMap = 
        new EnumMap<>(AggregatedTripData.DataSourceStrategy.class);
    
    public StrategySelectorFactory(List<DataSourceStrategySelector> selectors) {
        for (DataSourceStrategySelector selector : selectors) {
            try {
                AggregatedTripData.DataSourceStrategy strategy = 
                    AggregatedTripData.DataSourceStrategy.valueOf(selector.getStrategyName());
                strategyMap.put(strategy, selector);
                logger.info("Registered strategy selector: {} for strategy: {}", 
                    selector.getClass().getSimpleName(), strategy);
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown strategy name: {}", selector.getStrategyName());
            }
        }
    }
    
    public DataSourceStrategySelector getSelector(AggregatedTripData.DataSourceStrategy strategy) {
        DataSourceStrategySelector selector = strategyMap.get(strategy);
        if (selector == null) {
            logger.warn("No selector found for strategy: {}, using default (COST_PRIORITY)", strategy);
            selector = strategyMap.get(AggregatedTripData.DataSourceStrategy.COST_PRIORITY);
        }
        return selector;
    }
    
    public DataSourceStrategySelector getDefaultSelector() {
        return getSelector(AggregatedTripData.DataSourceStrategy.COST_PRIORITY);
    }
    
    public boolean hasStrategy(AggregatedTripData.DataSourceStrategy strategy) {
        return strategyMap.containsKey(strategy);
    }
}
