package com.villagenews.backend.repository;

import com.villagenews.backend.entity.NewsArticle;
import com.villagenews.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    
    Page<NewsArticle> findByStatus(NewsArticle.Status status, Pageable pageable);
    
    List<NewsArticle> findByStatus(NewsArticle.Status status);
    
    List<NewsArticle> findByAuthor(User author);
    
    List<NewsArticle> findByAuthorAndStatus(User author, NewsArticle.Status status);
}

