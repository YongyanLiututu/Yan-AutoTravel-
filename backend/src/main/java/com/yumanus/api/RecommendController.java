package com.Yan-AutoTravel.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RecommendController {

    public record RecoCard(String id, String city, String title, List<String> tags, double score) {}

    @GetMapping("/recommendations")
    public List<RecoCard> recs(@RequestParam(required = false) String city,
                               @RequestParam(required = false) String season,
                               @RequestParam(required = false) List<String> tags,
                               @RequestParam(required = false, defaultValue = "1") int page,
                               @RequestParam(required = false, defaultValue = "10") int size) {
        return List.of(
                new RecoCard("sh-01", city != null ? city : "上海", "魔都亲子一日精�?, List.of("亲子","地铁便捷"), 0.92),
                new RecoCard("cd-01", city != null ? city : "成都", "夏日美食三日", List.of("美食","小吃"), 0.89)
        );
    }
}


