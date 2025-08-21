package com.Yan-AutoTravel.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/drafts")
public class DraftsApiController {

    public static class DraftMeta {
        public String id;
        public String city;
        public Integer days;
        public String budget; // low/mid/high
        public List<String> themes;
        public long createdAt;
        public DraftMeta(){}
        public DraftMeta(String id,String city,Integer days,String budget,List<String> themes,long ts){
            this.id=id;this.city=city;this.days=days;this.budget=budget;this.themes=themes;this.createdAt=ts;
        }
    }

    private static final Map<String, Deque<DraftMeta>> SESSION_RECENTS = new ConcurrentHashMap<>();
    private static final Map<String, DraftMeta> STORE = new ConcurrentHashMap<>();

    @PostMapping
    public Map<String,String> create(@RequestBody DraftMeta body, @RequestParam(required=false) String sessionId){
        String id = Optional.ofNullable(body.id).filter(s->!s.isEmpty()).orElse(UUID.randomUUID().toString().substring(0,8));
        DraftMeta m = new DraftMeta(id, body.city, body.days, body.budget, body.themes==null? List.of(): body.themes, System.currentTimeMillis());
        STORE.put(id, m);
        if (sessionId != null) {
            SESSION_RECENTS.computeIfAbsent(sessionId, k-> new ArrayDeque<>()).addFirst(m);
            while (SESSION_RECENTS.get(sessionId).size() > 20) SESSION_RECENTS.get(sessionId).removeLast();
        }
        return Map.of("id", id);
    }

    @GetMapping
    public List<DraftMeta> recent(@RequestParam String sessionId, @RequestParam(defaultValue="5") int recent){
        Deque<DraftMeta> q = SESSION_RECENTS.getOrDefault(sessionId, new ArrayDeque<>());
        List<DraftMeta> out = new ArrayList<>();
        int i=0; for (DraftMeta m: q){ if (i++>=recent) break; out.add(m); }
        return out;
    }

    @GetMapping("/{id}")
    public DraftMeta get(@PathVariable String id){
        return STORE.get(id);
    }
}


