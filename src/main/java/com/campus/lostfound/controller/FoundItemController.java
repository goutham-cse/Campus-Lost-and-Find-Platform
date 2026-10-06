package com.campus.lostfound.controller;

import com.campus.lostfound.dto.ItemUploadForm;
import com.campus.lostfound.dto.VisionAnalysisResult;
import com.campus.lostfound.model.*;
import com.campus.lostfound.repository.ObjectItemRepository;
import com.campus.lostfound.service.AiVisionService;
import com.campus.lostfound.service.ItemMatchingService;
import com.campus.lostfound.service.StorageService;
import com.campus.lostfound.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/found")
public class FoundItemController {

    private final UserService userService;
    private final ObjectItemRepository itemRepository;
    private final StorageService storageService;
    private final AiVisionService aiVisionService;
    private final ItemMatchingService matchingService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public FoundItemController(UserService userService,
                               ObjectItemRepository itemRepository,
                               StorageService storageService,
                               AiVisionService aiVisionService,
                               ItemMatchingService matchingService) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.storageService = storageService;
        this.aiVisionService = aiVisionService;
        this.matchingService = matchingService;
    }

    @GetMapping("/new")
    public String showFoundForm(Model model) {
        ItemUploadForm form = new ItemUploadForm();
        form.setItemType(ItemType.FOUND);
        model.addAttribute("form", form);
        model.addAttribute("categories", ItemCategory.values());
        return "found_form";
    }

    @PostMapping("/new")
    public String createFoundItem(@ModelAttribute ItemUploadForm form,
                                  @RequestParam Map<String, String> allParams,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByEmail(authentication.getName()).orElseThrow();

            ObjectItem item = new ObjectItem();
            item.setUser(user);
            item.setItemType(ItemType.FOUND);
            item.setCategory(form.getCategory());
            item.setTitle(form.getTitle());
            item.setDescription(form.getDescription());
            item.setLocation(form.getLocation());
            item.setDateOccurred(form.getDateOccurred());

            if (form.getImageFile() != null && !form.getImageFile().isEmpty()) {
                String imagePath = storageService.store(form.getImageFile());
                item.setImagePath(imagePath);
                item.setAiProcessed(true);
            }

            // Collect category dynamic attribute parameters
            Map<String, String> attributes = form.getAttributes();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("attr_")) {
                    String attrKey = entry.getKey().substring(5);
                    attributes.put(attrKey, entry.getValue());
                }
            }

            item.setAttributesJson(objectMapper.writeValueAsString(attributes));
            item.setStatus(ItemStatus.ACTIVE);

            ObjectItem savedItem = itemRepository.save(item);

            // Check for immediate matches
            List<MatchResult> matches = matchingService.findMatchesForItem(savedItem);
            if (!matches.isEmpty()) {
                redirectAttributes.addFlashAttribute("successMsg", "Found item listed successfully! " + matches.size() + " potential match(es) detected!");
                return "redirect:/matches?itemId=" + savedItem.getId();
            }

            redirectAttributes.addFlashAttribute("successMsg", "Found item successfully published! We will notify you if a matching owner claims it.");
            return "redirect:/dashboard";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Failed to publish found item: " + e.getMessage());
            return "redirect:/found/new";
        }
    }
}
