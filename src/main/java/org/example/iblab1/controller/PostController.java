package org.example.iblab1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.iblab1.dto.PostRequest;
import org.example.iblab1.dto.PostResponse;
import org.example.iblab1.model.Post;
import org.example.iblab1.model.User;
import org.example.iblab1.repository.PostRepository;
import org.example.iblab1.repository.UserRepository;
import org.example.iblab1.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping
    public ResponseEntity<?> getAllPosts(HttpServletRequest request) {
        try {
            validateToken(request);
            List<PostResponse> posts = postRepository.findAllPostsWithAuthor();
            return ResponseEntity.ok(posts);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping
    public ResponseEntity<?> createPost(@Valid @RequestBody PostRequest postRequest,
                                        HttpServletRequest request) {
        try {
            String username = validateTokenAndGetUsername(request);
            User author = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Post post = new Post(postRequest.getTitle(), postRequest.getContent(), author);
            postRepository.save(post);

            return ResponseEntity.ok("Post created successfully");
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private String validateTokenAndGetUsername(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        if (token == null) {
            throw new RuntimeException("JWT token is missing");
        }

        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }

        return jwtUtils.getUsernameFromJwtToken(token);
    }

    private void validateToken(HttpServletRequest request) {
        String token = extractJwtFromRequest(request);
        if (token == null) {
            throw new RuntimeException("JWT token is missing");
        }

        if (!jwtUtils.validateJwtToken(token)) {
            throw new RuntimeException("Invalid JWT token");
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        return new ResponseEntity<>(errorResponse, status);
    }
}