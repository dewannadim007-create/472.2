package bd.edu.seu.pulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String postId;
    private String authorId;
    private String content;
    private LocalDateTime timestamp = LocalDateTime.now();
    private int upvoteCount;
    private int downvoteCount;
    private List<String> upvotedBy = new ArrayList<>();
    private List<String> downvotedBy = new ArrayList<>();

    public Comment(String id, String postId, String authorId, String content, LocalDateTime timestamp, int upvoteCount, int downvoteCount) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.timestamp = timestamp;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
    }

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public int getDownvoteCount() {
        return downvoteCount;
    }

    public void setDownvoteCount(int downvoteCount) {
        this.downvoteCount = downvoteCount;
    }

    public List<String> getUpvotedBy() {
        return upvotedBy;
    }

    public void setUpvotedBy(List<String> upvotedBy) {
        this.upvotedBy = upvotedBy;
    }

    public List<String> getDownvotedBy() {
        return downvotedBy;
    }

    public void setDownvotedBy(List<String> downvotedBy) {
        this.downvotedBy = downvotedBy;
    }
} 