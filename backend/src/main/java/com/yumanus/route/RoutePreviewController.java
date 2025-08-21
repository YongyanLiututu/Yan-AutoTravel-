package com.Yan-AutoTravel.route;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/route/eta")
public class RoutePreviewController {

    public static record PreviewReq(double originLng, double originLat, List<Point> pois){}
    public static record Point(String id, double lng, double lat){}
    public static record PreviewResp(Map<String,String> etaText){}

    @PostMapping(value = "/preview", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public PreviewResp preview(@RequestBody PreviewReq req){
        Map<String,String> map = new HashMap<>();
        if (req.pois()!=null){
            for (Point p: req.pois()){
                map.put(p.id(), "�?5分钟");
            }
        }
        return new PreviewResp(map);
    }
}


