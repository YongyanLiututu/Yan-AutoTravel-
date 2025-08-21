package com.Yan-AutoTravel.train12306;

import com.Yan-AutoTravel.tools.McpClients;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/train")
public class TrainController {
    private final McpClients mcp;
    public TrainController(McpClients mcp) { this.mcp = mcp; }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public TrainSearchResponse search(@RequestParam @NotBlank String from,
                                      @RequestParam @NotBlank String to,
                                      @RequestParam @NotBlank String date,
                                      @RequestParam(defaultValue = "fast") String prefer) {
        // TODO call MCP 12306 server; return mock
        TrainSearchResponse r = new TrainSearchResponse();
        r.server = mcp.getTrainServer();
        r.items = List.of(new TrainItem("G123", from, to, date, 130, 350.0));
        return r;
    }

    public static class TrainSearchResponse {
        public String server;
        public List<TrainItem> items;
    }

    public static class TrainItem {
        public String code;
        public String from;
        public String to;
        public String date;
        public int minutes;
        public double price;
        public TrainItem(String c, String f, String t, String d, int m, double p) {
            this.code = c; this.from = f; this.to = t; this.date = d; this.minutes = m; this.price = p;
        }
    }
}


