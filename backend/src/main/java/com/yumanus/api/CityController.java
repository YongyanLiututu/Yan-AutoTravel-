package com.Yan-AutoTravel.api;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/cities")
public class CityController {
    private static final List<String> HOT = List.of("北京","上海","广州","深圳","杭州","成都","西安","南京","重庆","武汉");

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam(required = false, defaultValue = "") String q){
        String s = q.trim();
        if (s.isEmpty()) return HOT;
        List<String> out = new ArrayList<>();
        for (String c: HOT){ if (c.contains(s)) out.add(c); }
        if (out.isEmpty()) out.add(s);
        return out;
    }
}


