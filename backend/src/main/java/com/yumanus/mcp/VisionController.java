package com.yumanus.mcp;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/vision")
public class VisionController {

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> analyze(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "mode", required = false, defaultValue = "both") String mode
    ) throws Exception {
        Map<String, Object> resp = new HashMap<>();
        resp.put("captions", List.of("图片上传成功，待多模态模型解析（占位）"));
        resp.put("ocr", "");
        resp.put("tags", List.of("image"));
        return resp;
    }
}


