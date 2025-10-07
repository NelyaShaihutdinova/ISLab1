package org.example.iblab1.repository;

import org.example.iblab1.dto.PostResponse;
import org.example.iblab1.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT new org.example.iblab1.dto.PostResponse(p.id, p.title, p.content, p.createdAt, p.author.username) " +
            "FROM Post p ORDER BY p.createdAt DESC")
    List<PostResponse> findAllPostsWithAuthor();
}
