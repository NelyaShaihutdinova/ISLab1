package org.example.iblab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 1, max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(min = 1, max = 2000)
    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    public Post() {
        this.createdAt = LocalDateTime.now();
    }

    public Post(String title, String content, User author) {
        this();
        this.title = title;
        this.content = content;
        this.author = author != null ? createDefensiveCopy(author) : null;
    }

    private User createDefensiveCopy(User original) {
        User copy = new User();
        copy.setUsername(original.getUsername());
        return copy;
    }
}
