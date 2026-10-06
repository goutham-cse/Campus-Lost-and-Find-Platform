package com.campus.lostfound.controller;

import com.campus.lostfound.dto.VisionAnalysisResult;
import com.campus.lostfound.model.ObjectItem;
import com.campus.lostfound.repository.ObjectItemRepository;
import com.campus.lostfound.service.AiVisionService;
import com.campus.lostfound.service.ItemMatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final AiVisionService aiVisionService;
    private final ObjectItemRepository itemRepository;
    private final ItemMatchingService matchingService;

    @Autowired
    public ApiController(AiVisionService aiVisionService, ObjectItemRepository itemRepository, ItemMatchingService matchingService) {
        this.aiVisionService = aiVisionService;
        this.itemRepository = itemRepository;
        this.matchingService = matchingService;
    }

    @PostMapping("/analyze-image")
    public ResponseEntity<VisionAnalysisResult> analyzeImage(@RequestParam("image") MultipartFile imageFile) {
        VisionAnalysisResult result = aiVisionService.analyzeImage(imageFile);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Map<String, Object>> getItemDetails(@PathVariable Long id) {
        ObjectItem item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("title", item.getTitle());
        response.put("category", item.getCategory().getDisplayName());
        response.put("description", item.getDescription());
        response.put("location", item.getLocation());
        response.put("imagePath", item.getImagePath());
        response.put("attributes", matchingService.parseAttributes(item.getAttributesJson()));
        response.put("userFullName", item.getUser().getFullName());
        response.put("userEmail", item.getUser().getEmail());
        response.put("userPhone", item.getUser().getPhone());

        return ResponseEntity.ok(response);
    }
}
