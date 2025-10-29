package bd.edu.seu.pulse.dto;

import java.time.LocalDateTime;

public class CommentViewDTO {
    private String id;
    private String postId;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime timestamp;
    private int upvoteCount;
    private int downvoteCount;
    private boolean hasUpvoted;
    private boolean hasDownvoted;
    private boolean canEdit;

    public CommentViewDTO() {
    }

    public CommentViewDTO(String id, String postId, String authorId, String authorName, String content, LocalDateTime timestamp, int upvoteCount, int downvoteCount) {
        this.id = id;
        this.postId = postId;
        this.authorId = authorId;
        this.authorName = authorName;
        this.content = content;
        this.timestamp = timestamp;
        this.upvoteCount = upvoteCount;
        this.downvoteCount = downvoteCount;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public boolean isHasUpvoted() {
        return hasUpvoted;
    }

    public void setHasUpvoted(boolean hasUpvoted) {
        this.hasUpvoted = hasUpvoted;
    }

    public boolean isHasDownvoted() {
        return hasDownvoted;
    }

    public void setHasDownvoted(boolean hasDownvoted) {
        this.hasDownvoted = hasDownvoted;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public void setCanEdit(boolean canEdit) {
        this.canEdit = canEdit;
    }
} 