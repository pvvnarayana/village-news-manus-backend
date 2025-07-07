package com.villagenews.backend.service;

import com.villagenews.backend.dto.VideoUploadDto;
import com.villagenews.backend.entity.User;
import com.villagenews.backend.entity.Video;
import com.villagenews.backend.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {
    
    @Autowired
    private VideoRepository videoRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public Video uploadVideo(MultipartFile file, VideoUploadDto videoUploadDto, User uploader) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create video entity
        Video video = new Video();
        video.setTitle(videoUploadDto.getTitle());
        video.setDescription(videoUploadDto.getDescription());
        video.setFilePath(filePath.toString());
        video.setUploader(uploader);
        
        return videoRepository.save(video);
    }
    
    public List<Video> getVideosByUploader(User uploader) {
        return videoRepository.findByUploader(uploader);
    }
    
    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id: " + id));
    }
    
    public void deleteVideo(Long id, User currentUser) throws IOException {
        Video video = getVideoById(id);
        
        // Check if the current user is the uploader or an admin
        if (!video.getUploader().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().equals(User.Role.ROLE_ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this video");
        }
        
        // Delete file from disk
        Path filePath = Paths.get(video.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Delete video entity from database
        videoRepository.delete(video);
    }
    
    public Path getVideoPath(Long id) {
        Video video = getVideoById(id);
        return Paths.get(video.getFilePath());
    }
}

