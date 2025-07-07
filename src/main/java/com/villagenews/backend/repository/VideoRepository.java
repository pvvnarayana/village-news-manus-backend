package com.villagenews.backend.repository;

import com.villagenews.backend.entity.User;
import com.villagenews.backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    List<Video> findByUploader(User uploader);
}

