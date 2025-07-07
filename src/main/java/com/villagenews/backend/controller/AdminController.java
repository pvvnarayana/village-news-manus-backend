package com.villagenews.backend.controller;

import com.villagenews.backend.entity.NewsArticle;
import com.villagenews.backend.service.NewsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private NewsArticleService newsArticleService;
    
    @GetMapping("/news/pending")
    public ResponseEntity<List<NewsArticle>> getPendingNewsArticles() {
        List<NewsArticle> pendingNews = newsArticleService.getPendingNewsArticles();
        return ResponseEntity.ok(pendingNews);
    }
    
    @PutMapping("/news/{id}/approve")
    public ResponseEntity<?> approveNewsArticle(@PathVariable Long id) {
        try {
            NewsArticle newsArticle = newsArticleService.approveNewsArticle(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", newsArticle.getId());
            response.put("title", newsArticle.getTitle());
            response.put("status", newsArticle.getStatus().name());
            response.put("publishedAt", newsArticle.getPublishedAt());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/news/{id}/reject")
    public ResponseEntity<?> rejectNewsArticle(@PathVariable Long id) {
        try {
            NewsArticle newsArticle = newsArticleService.rejectNewsArticle(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", newsArticle.getId());
            response.put("title", newsArticle.getTitle());
            response.put("status", newsArticle.getStatus().name());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

