package com.Yan-AutoTravel.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class McpClients {

    @Value("${app.mcp.amap.server:https://www.modelscope.cn/mcp/servers/@amap/amap-maps}")
    private String amapServer;

    @Value("${app.mcp.train12306.server:https://www.modelscope.cn/mcp/servers/@Joooook/12306-mcp}")
    private String trainServer;

    @Value("${app.mcp.baidumap.server:}")
    private String baiduMapServer;

    @Value("${app.mcp.flight.server:}")
    private String flightServer;

    public String getAmapServer() { return amapServer; }
    public String getTrainServer() { return trainServer; }
    public String getBaiduMapServer() { return baiduMapServer; }
    public String getFlightServer() { return flightServer; }
}


