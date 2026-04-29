package com.yumanus.api;

import com.yumanus.qa.OrchestratorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;

@RestController
@RequestMapping("/api/qa")
public class QaController {

    public record AskRequest(String sessionId, String message) {}

    private final OrchestratorService orchestrator;
    public QaController(OrchestratorService orchestrator){ this.orchestrator = orchestrator; }

    @GetMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askGet(@RequestParam(name = "q", required = false) String q,
                               @RequestParam(name = "sessionId", required = false) String sessionId) {
        return runPipeline(sessionId, q);
    }

    @PostMapping(value = "/ask", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> askPost(@RequestBody AskRequest req) {
        return runPipeline(req.sessionId(), req.message());
    }

    private Flux<String> runPipeline(String sessionId, String message){
        Set<String> userPref = new HashSet<>();
        Map<String,String> ctx = new HashMap<>();
        if (sessionId != null) ctx.put("recent_city","广州");

        var intent = orchestrator.rewrite(message, userPref, ctx);
        var status = String.format("status: 默认补全=时间:%s 出发:%s 偏好:%s", intent.date, intent.cityFrom, intent.seatPref);
        var steps = "status: 将做的步骤：①地理编码（Baidu MCP）② 车次检索（12306 MCP）③ 行程写作";
        var tool = orchestrator.toolchain(intent);
        var toolMsg = "tool: 已找到" + ((Map)tool.get("train")).get("candidates") + " 条候选，高铁优先";
        var finalOut = orchestrator.compose(intent, tool);
        var finalMsg = "final: " + finalOut.get("text");

        return Flux.just("START", status, steps, toolMsg, finalMsg, "DONE").delayElements(Duration.ofMillis(200));
    }
}


