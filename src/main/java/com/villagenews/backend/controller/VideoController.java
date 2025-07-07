package com.villagenews.backend.controller;

import com.villagenews.backend.dto.VideoUploadDto;
import com.villagenews.backend.entity.User;
import com.villagenews.backend.entity.Video;
import com.villagenews.backend.service.UserService;
import com.villagenews.backend.service.VideoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {
    
    @Autowired
    private VideoService videoService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @Valid @ModelAttribute VideoUploadDto videoUploadDto,
                                         Authentication authentication) {
        try {
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Please select a video file to upload");
                return ResponseEntity.badRequest().body(error);
            }
            
            User uploader = userService.findByUsername(authentication.getName());
            Video video = videoService.uploadVideo(file, videoUploadDto, uploader);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", video.getId());
            response.put("title", video.getTitle());
            response.put("description", video.getDescription());
            response.put("filePath", video.getFilePath());
            response.put("uploadedAt", video.getUploadedAt());
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload video: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<Video>> getUserVideos(Authentication authentication) {
        User uploader = userService.findByUsername(authentication.getName());
        List<Video> videos = videoService.getVideosByUploader(uploader);
        return ResponseEntity.ok(videos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> streamVideo(@PathVariable Long id) {
        try {
            Path videoPath = videoService.getVideoPath(id);
            
            if (!Files.exists(videoPath)) {
                return ResponseEntity.notFound().build();
            }
            
            FileInputStream fileInputStream = new FileInputStream(videoPath.toFile());
            InputStreamResource resource = new InputStreamResource(fileInputStream);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(Files.size(videoPath));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoPath.getFileName() + "\"");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVideo(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            videoService.deleteVideo(id, currentUser);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Video deleted successfully");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete video: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

