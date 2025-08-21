package com.Yan-AutoTravel.amap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.Yan-AutoTravel.tools.McpClients;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amap")
public class RouteController {
    private final McpClients mcp;
    public RouteController(McpClients mcp) { this.mcp = mcp; }

    @PostMapping(value = "/route", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public RouteMatrixResponse route(@Valid @RequestBody RouteMatrixRequest req) {
        // TODO: call MCP server via HTTP/WS per MCP spec; return mock for now
        RouteMatrixResponse resp = new RouteMatrixResponse();
        resp.mode = req.mode;
        resp.matrix = List.of(
                new RouteMatrixResponse.Edge(req.points.get(0).id, req.points.get(1).id, 25)
        );
        resp.source = mcp.getAmapServer();
        return resp;
    }

    public static class RouteMatrixRequest {
        @NotEmpty
        public List<Point> points;
        @NotNull
        public String mode; // walk/transit/drive
        public static class Point {
            public String id;
            public double lat;
            public double lng;
        }
    }

    public static class RouteMatrixResponse {
        public String mode;
        public String source;
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        public List<Edge> matrix;
        public static class Edge {
            @JsonProperty("from") public String fromId;
            @JsonProperty("to") public String toId;
            @JsonProperty("etaMin") public int etaMin;
            public Edge(String f, String t, int e) { this.fromId = f; this.toId = t; this.etaMin = e; }
        }
    }
}


