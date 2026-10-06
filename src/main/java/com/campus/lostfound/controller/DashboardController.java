package com.campus.lostfound.controller;

import com.campus.lostfound.model.ItemStatus;
import com.campus.lostfound.model.ItemType;
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
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final ObjectItemRepository itemRepository;
    private final ItemMatchingService matchingService;

    @Autowired
    public DashboardController(UserService userService, ObjectItemRepository itemRepository, ItemMatchingService matchingService) {
        this.userService = userService;
        this.itemRepository = itemRepository;
        this.matchingService = matchingService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName()).orElseThrow();
        List<ObjectItem> userItems = itemRepository.findByUserOrderByCreatedAtDesc(user);
        List<ObjectItem> recentFoundItems = itemRepository.findByItemTypeAndStatusOrderByCreatedAtDesc(ItemType.FOUND, ItemStatus.ACTIVE);
        List<ObjectItem> recentLostItems = itemRepository.findByItemTypeAndStatusOrderByCreatedAtDesc(ItemType.LOST, ItemStatus.ACTIVE);

        List<MatchResult> userMatches = new ArrayList<>();
        for (ObjectItem item : userItems) {
            if (item.getStatus() == ItemStatus.ACTIVE) {
                userMatches.addAll(matchingService.findMatchesForItem(item));
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("userItems", userItems);
        model.addAttribute("recentFoundItems", recentFoundItems);
        model.addAttribute("recentLostItems", recentLostItems);
        model.addAttribute("userMatches", userMatches);

        return "dashboard";
    }
}
