package com.campus.lostfound.service;

import com.campus.lostfound.model.ItemCategory;
import com.campus.lostfound.model.ItemStatus;
import com.campus.lostfound.model.ItemType;
import com.campus.lostfound.model.MatchResult;
import com.campus.lostfound.model.ObjectItem;
import com.campus.lostfound.repository.ObjectItemRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ItemMatchingService {

    private final ObjectItemRepository itemRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ItemMatchingService(ObjectItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<MatchResult> findMatchesForItem(ObjectItem targetItem) {
        List<MatchResult> matches = new ArrayList<>();
        ItemType oppositeType = (targetItem.getItemType() == ItemType.LOST) ? ItemType.FOUND : ItemType.LOST;

        // Fetch candidate items of opposite type with same category & active status
        List<ObjectItem> candidateItems = itemRepository.findByItemTypeAndCategoryAndStatus(
                oppositeType,
                targetItem.getCategory(),
                ItemStatus.ACTIVE
        );

        Map<String, String> targetAttributes = parseAttributes(targetItem.getAttributesJson());

        for (ObjectItem candidate : candidateItems) {
            // Skip items owned by same user
            if (candidate.getUser().getId().equals(targetItem.getUser().getId())) {
                continue;
            }

            Map<String, String> candidateAttributes = parseAttributes(candidate.getAttributesJson());
            List<String> matchReasons = new ArrayList<>();
            int score = 40; // Base score for same category

            matchReasons.add("Matched Item Category: " + targetItem.getCategory().getDisplayName());

            // Compare dynamic attributes
            int attrMatches = 0;
            int totalAttrs = 0;

            for (Map.Entry<String, String> entry : targetAttributes.entrySet()) {
                String key = entry.getKey();
                String val1 = entry.getValue();

                if (candidateAttributes.containsKey(key)) {
                    totalAttrs++;
                    String val2 = candidateAttributes.get(key);

                    if (val1 != null && val2 != null && !val1.isBlank() && !val2.isBlank()) {
                        double similarity = calculateStringSimilarity(val1, val2);
                        if (similarity > 0.7) {
                            attrMatches++;
                            score += Math.round(similarity * 20); // Add up to 20 points per attribute match
                            matchReasons.add("Matching Attribute '" + formatKey(key) + "': \"" + val1 + "\" ~ \"" + val2 + "\"");
                        }
                    }
                }
            }

            // Location correlation bonus
            if (targetItem.getLocation() != null && candidate.getLocation() != null) {
                if (calculateStringSimilarity(targetItem.getLocation(), candidate.getLocation()) > 0.6) {
                    score += 15;
                    matchReasons.add("Matching Campus Location: " + targetItem.getLocation());
                }
            }

            // Cap match percentage at 99%
            int matchPercentage = Math.min(score, 99);

            // High threshold for match inclusion
            if (matchPercentage >= 50) {
                ObjectItem lostItem = (targetItem.getItemType() == ItemType.LOST) ? targetItem : candidate;
                ObjectItem foundItem = (targetItem.getItemType() == ItemType.FOUND) ? targetItem : candidate;
                matches.add(new MatchResult(lostItem, foundItem, matchPercentage, matchReasons));
            }
        }

        // Sort by match percentage descending
        matches.sort((m1, m2) -> Integer.compare(m2.getMatchPercentage(), m1.getMatchPercentage()));
        return matches;
    }

    public Map<String, String> parseAttributes(String attributesJson) {
        if (attributesJson == null || attributesJson.isBlank()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(attributesJson, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private double calculateStringSimilarity(String s1, String s2) {
        String str1 = s1.toLowerCase().trim();
        String str2 = s2.toLowerCase().trim();

        if (str1.equals(str2)) return 1.0;
        if (str1.contains(str2) || str2.contains(str1)) return 0.85;

        // Levenshtein distance similarity calculation
        int distance = computeLevenshteinDistance(str1, str2);
        int maxLen = Math.max(str1.length(), str2.length());
        if (maxLen == 0) return 1.0;
        return 1.0 - ((double) distance / maxLen);
    }

    private int computeLevenshteinDistance(String lhs, String rhs) {
        int len0 = lhs.length() + 1;
        int len1 = rhs.length() + 1;
        int[] cost = new int[len0];
        int[] newcost = new int[len0];

        for (int i = 0; i < len0; i++) cost[i] = i;

        for (int j = 1; j < len1; j++) {
            newcost[0] = j;
            for (int i = 1; i < len0; i++) {
                int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;
                int cost_replace = cost[i - 1] + match;
                int cost_insert = cost[i] + 1;
                int cost_delete = newcost[i - 1] + 1;
                newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
            }
            int[] swap = cost;
            cost = newcost;
            newcost = swap;
        }
        return cost[len0 - 1];
    }

    private String formatKey(String key) {
        return key.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
    }
}
