package com.campus.lostfound.dto;

import com.campus.lostfound.model.ItemCategory;

import java.util.HashMap;
import java.util.Map;

public class VisionAnalysisResult {
    private boolean success;
    private ItemCategory detectedCategory;
    private String suggestedTitle;
    private String suggestedDescription;
    private double confidenceScore;
    private Map<String, String> extractedAttributes = new HashMap<>();

    public VisionAnalysisResult() {}

    public VisionAnalysisResult(boolean success, ItemCategory detectedCategory, String suggestedTitle, String suggestedDescription, double confidenceScore) {
        this.success = success;
        this.detectedCategory = detectedCategory;
        this.suggestedTitle = suggestedTitle;
        this.suggestedDescription = suggestedDescription;
        this.confidenceScore = confidenceScore;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public ItemCategory getDetectedCategory() {
        return detectedCategory;
    }

    public void setDetectedCategory(ItemCategory detectedCategory) {
        this.detectedCategory = detectedCategory;
    }

    public String getSuggestedTitle() {
        return suggestedTitle;
    }

    public void setSuggestedTitle(String suggestedTitle) {
        this.suggestedTitle = suggestedTitle;
    }

    public String getSuggestedDescription() {
        return suggestedDescription;
    }

    public void setSuggestedDescription(String suggestedDescription) {
        this.suggestedDescription = suggestedDescription;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Map<String, String> getExtractedAttributes() {
        return extractedAttributes;
    }

    public void setExtractedAttributes(Map<String, String> extractedAttributes) {
        this.extractedAttributes = extractedAttributes;
    }

    public void addAttribute(String key, String value) {
        if (value != null && !value.isBlank()) {
            this.extractedAttributes.put(key, value);
        }
    }
}
