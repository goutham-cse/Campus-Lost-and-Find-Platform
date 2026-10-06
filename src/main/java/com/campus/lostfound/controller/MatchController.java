package com.campus.lostfound.controller;

import com.campus.lostfound.model.ItemStatus;
import com.campus.lostfound.model.MatchResult;
import com.campus.lostfound.model.ObjectItem;
import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.ObjectItemRepository;
import com.campus.lostfound.service.ItemMatchingService;
import com.campus.lostfound.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private final ObjectItemRepository itemRepository;
    private final ItemMatchingService matchingService;
    private final UserService userService;

    @Autowired
    public MatchController(ObjectItemRepository itemRepository, ItemMatchingService matchingService, UserService userService) {
        this.itemRepository = itemRepository;
        this.matchingService = matchingService;
        this.userService = userService;
    }

    @GetMapping
    public String showMatches(@RequestParam(value = "itemId", required = false) Long itemId,
                              Authentication authentication,
                              Model model) {
        User currentUser = userService.findByEmail(authentication.getName()).orElseThrow();
        List<MatchResult> matches = new ArrayList<>();

        if (itemId != null) {
            ObjectItem item = itemRepository.findById(itemId).orElse(null);
            if (item != null) {
                matches = matchingService.findMatchesForItem(item);
                model.addAttribute("targetItem", item);
                model.addAttribute("targetItemAttributes", matchingService.parseAttributes(item.getAttributesJson()));
            }
        } else {
            List<ObjectItem> userItems = itemRepository.findByUserOrderByCreatedAtDesc(currentUser);
            for (ObjectItem item : userItems) {
                if (item.getStatus() == ItemStatus.ACTIVE) {
                    matches.addAll(matchingService.findMatchesForItem(item));
                }
            }
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("matches", matches);
        return "matches";
    }

    @PostMapping("/connect")
    public String connectUsers(@RequestParam("lostItemId") Long lostItemId,
                               @RequestParam("foundItemId") Long foundItemId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        ObjectItem lostItem = itemRepository.findById(lostItemId).orElse(null);
        ObjectItem foundItem = itemRepository.findById(foundItemId).orElse(null);

        if (lostItem != null && foundItem != null) {
            lostItem.setStatus(ItemStatus.RESOLVED);
            foundItem.setStatus(ItemStatus.RESOLVED);

            itemRepository.save(lostItem);
            itemRepository.save(foundItem);

            redirectAttributes.addFlashAttribute("successMsg", "Congratulations! The lost item match has been confirmed and resolved. Contact details exchanged!");
        }

        return "redirect:/dashboard";
    }
}
