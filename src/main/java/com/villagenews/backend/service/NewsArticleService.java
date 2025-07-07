package com.villagenews.backend.service;

import com.villagenews.backend.dto.NewsArticleDto;
import com.villagenews.backend.entity.NewsArticle;
import com.villagenews.backend.entity.User;
import com.villagenews.backend.entity.Video;
import com.villagenews.backend.repository.NewsArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsArticleService {
    
    @Autowired
    private NewsArticleRepository newsArticleRepository;
    
    @Autowired
    private VideoService videoService;
    
    public NewsArticle createNewsArticle(NewsArticleDto newsArticleDto, User author) {
        NewsArticle newsArticle = new NewsArticle();
        newsArticle.setTitle(newsArticleDto.getTitle());
        newsArticle.setContent(newsArticleDto.getContent());
        newsArticle.setLocation(newsArticleDto.getLocation());
        newsArticle.setImageUrl(newsArticleDto.getImageUrl());
        newsArticle.setAuthor(author);
        
        if (newsArticleDto.getVideoId() != null) {
            Video video = videoService.getVideoById(newsArticleDto.getVideoId());
            newsArticle.setVideo(video);
        }
        
        return newsArticleRepository.save(newsArticle);
    }
    
    public Page<NewsArticle> getApprovedNewsArticles(Pageable pageable) {
        return newsArticleRepository.findByStatus(NewsArticle.Status.APPROVED, pageable);
    }
    
    public List<NewsArticle> getApprovedNewsArticles() {
        return newsArticleRepository.findByStatus(NewsArticle.Status.APPROVED);
    }
    
    public List<NewsArticle> getPendingNewsArticles() {
        return newsArticleRepository.findByStatus(NewsArticle.Status.PENDING_APPROVAL);
    }
    
    public NewsArticle getNewsArticleById(Long id) {
        return newsArticleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News article not found with id: " + id));
    }
    
    public NewsArticle updateNewsArticle(Long id, NewsArticleDto newsArticleDto, User currentUser) {
        NewsArticle newsArticle = getNewsArticleById(id);
        
        // Check if the current user is the author or an admin
        if (!newsArticle.getAuthor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ROLE_ADMIN)) {
            throw new RuntimeException("You don't have permission to update this news article");
        }
        
        newsArticle.setTitle(newsArticleDto.getTitle());
        newsArticle.setContent(newsArticleDto.getContent());
        newsArticle.setLocation(newsArticleDto.getLocation());
        newsArticle.setImageUrl(newsArticleDto.getImageUrl());
        
        if (newsArticleDto.getVideoId() != null) {
            Video video = videoService.getVideoById(newsArticleDto.getVideoId());
            newsArticle.setVideo(video);
        } else {
            newsArticle.setVideo(null);
        }
        
        return newsArticleRepository.save(newsArticle);
    }
    
    public void deleteNewsArticle(Long id, User currentUser) {
        NewsArticle newsArticle = getNewsArticleById(id);
        
        // Check if the current user is the author or an admin
        if (!newsArticle.getAuthor().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ROLE_ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this news article");
        }
        
        newsArticleRepository.delete(newsArticle);
    }
    
    public NewsArticle approveNewsArticle(Long id) {
        NewsArticle newsArticle = getNewsArticleById(id);
        newsArticle.setStatus(NewsArticle.Status.APPROVED);
        return newsArticleRepository.save(newsArticle);
    }
    
    public NewsArticle rejectNewsArticle(Long id) {
        NewsArticle newsArticle = getNewsArticleById(id);
        newsArticle.setStatus(NewsArticle.Status.REJECTED);
        return newsArticleRepository.save(newsArticle);
    }
    
    public List<NewsArticle> getNewsArticlesByAuthor(User author) {
        return newsArticleRepository.findByAuthor(author);
    }
}

