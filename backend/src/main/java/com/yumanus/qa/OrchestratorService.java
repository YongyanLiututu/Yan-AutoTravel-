package com.Yan-AutoTravel.qa;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OrchestratorService {

    public static class Intent {
        public String intent = "travel_plan";
        public String cityFrom;
        public String cityTo;
        public String date = "下个周六";
        public String seatPref = "高铁优先";
        public String budget = "�?;
        public boolean withKids = false;
        public List<String> plan = List.of("geocode", "train", "write");
    }

    private static final Pattern FROM_TO = Pattern.compile("(?<from>.+?)�??<to>.+?)(怎么去|如何去|坐什么|多长时间)");

    public Intent rewrite(String query, Set<String> userPref, Map<String, String> sessionCtx){
        Intent intent = new Intent();
        // 粗略解析 from/to
        Matcher m = FROM_TO.matcher(Optional.ofNullable(query).orElse(""));
        if (m.find()){
            intent.cityFrom = m.group("from");
            intent.cityTo = m.group("to");
        }
        // 偏好补全
        if (userPref != null && userPref.contains("亲子")) intent.withKids = true;
        if (userPref != null && userPref.contains("预算敏感")) intent.budget = "�?;
        // 会话上下文补�?
        if (intent.cityFrom == null) intent.cityFrom = sessionCtx.getOrDefault("recent_city", "广州");
        if (intent.cityTo == null) intent.cityTo = sessionCtx.getOrDefault("target_city", "九寨�?);
        return intent;
    }

    public Map<String,Object> toolchain(Intent intent){
        Map<String,Object> tool = new LinkedHashMap<>();
        // 占位：模拟工具链摘要
        tool.put("geocode", Map.of("from", intent.cityFrom, "to", intent.cityTo, "status", "ok"));
        tool.put("train", Map.of("candidates", 3, "best", Map.of("code", "G123", "eta", "5h23m")));
        return tool;
    }

    public Map<String,Object> compose(Intent intent, Map<String,Object> tool){
        Map<String,Object> finalOut = new LinkedHashMap<>();
        String text = String.format("建议�?s出发，乘坐高铁至成都，再前往%s，预计总时长约 %s�?,
                intent.cityFrom, intent.cityTo, ((Map)((Map)tool.get("train")).get("best")).get("eta"));
        finalOut.put("text", text);
        finalOut.put("actions", List.of(
                Map.of("type","addToPlan","slot","Day 2 下午","poiId","poi-001"),
                Map.of("type","createMiniDraft")
        ));
        return finalOut;
    }
}


