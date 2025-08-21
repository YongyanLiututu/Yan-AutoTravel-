package com.Yan-AutoTravel.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/user/pref")
public class UserPrefController {
    private static final Map<String, Set<String>> PREFS = new ConcurrentHashMap<>();

    public record PrefPayload(Set<String> values){}

    @PostMapping
    public Map<String,String> save(@RequestParam String sessionId, @RequestBody PrefPayload payload){
        PREFS.put(sessionId, payload.values()==null? new HashSet<>(): new HashSet<>(payload.values()));
        return Map.of("status","ok");
    }

    @GetMapping
    public PrefPayload get(@RequestParam String sessionId){
        return new PrefPayload(new HashSet<>(PREFS.getOrDefault(sessionId, Set.of())));
    }
}


