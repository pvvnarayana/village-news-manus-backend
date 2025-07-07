package com.villagenews.backend.controller;

import com.villagenews.backend.dto.NewsArticleDto;
import com.villagenews.backend.entity.NewsArticle;
import com.villagenews.backend.entity.User;
import com.villagenews.backend.service.NewsArticleService;
import com.villagenews.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")
@CrossOrigin(origins = "*")
public class NewsController {
    
    @Autowired
    private NewsArticleService newsArticleService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createNewsArticle(@Valid @RequestBody NewsArticleDto newsArticleDto, 
                                               Authentication authentication) {
        try {
            User author = userService.findByUsername(authentication.getName());
            NewsArticle newsArticle = newsArticleService.createNewsArticle(newsArticleDto, author);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", newsArticle.getId());
            response.put("title", newsArticle.getTitle());
            response.put("content", newsArticle.getContent());
            response.put("status", newsArticle.getStatus().name());
            response.put("author", newsArticle.getAuthor().getUsername());
            response.put("location", newsArticle.getLocation());
            response.put("imageUrl", newsArticle.getImageUrl());
            response.put("createdAt", newsArticle.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<NewsArticle>> getApprovedNewsArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        Page<NewsArticle> newsPage = newsArticleService.getApprovedNewsArticles(pageable);
        
        return ResponseEntity.ok(newsPage.getContent());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsArticleById(@PathVariable Long id) {
        try {
            NewsArticle newsArticle = newsArticleService.getNewsArticleById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", newsArticle.getId());
            response.put("title", newsArticle.getTitle());
            response.put("content", newsArticle.getContent());
            response.put("status", newsArticle.getStatus().name());
            response.put("author", newsArticle.getAuthor().getUsername());
            response.put("location", newsArticle.getLocation());
            response.put("imageUrl", newsArticle.getImageUrl());
            response.put("createdAt", newsArticle.getCreatedAt());
            response.put("publishedAt", newsArticle.getPublishedAt());
            
            if (newsArticle.getVideo() != null) {
                response.put("videoId", newsArticle.getVideo().getId());
                response.put("videoTitle", newsArticle.getVideo().getTitle());
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNewsArticle(@PathVariable Long id, 
                                               @Valid @RequestBody NewsArticleDto newsArticleDto,
                                               Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            NewsArticle newsArticle = newsArticleService.updateNewsArticle(id, newsArticleDto, currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", newsArticle.getId());
            response.put("title", newsArticle.getTitle());
            response.put("content", newsArticle.getContent());
            response.put("status", newsArticle.getStatus().name());
            response.put("author", newsArticle.getAuthor().getUsername());
            response.put("location", newsArticle.getLocation());
            response.put("imageUrl", newsArticle.getImageUrl());
            response.put("createdAt", newsArticle.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNewsArticle(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            newsArticleService.deleteNewsArticle(id, currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "News article deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

