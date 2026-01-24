package bd.edu.seu.pulse.dto;

import java.time.LocalDateTime;

public class ReaderActivityDTO {
    private String type;
    private String postPreview;
    private String postId;
    private String commentContent;
    private LocalDateTime timestamp;
    private String timeAgo;

    public ReaderActivityDTO() {
    }

    public ReaderActivityDTO(String type, String postPreview, String postId, String commentContent,
            LocalDateTime timestamp) {
        this.type = type;
        this.postPreview = postPreview;
        this.postId = postId;
        this.commentContent = commentContent;
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostPreview() {
        return postPreview;
    }

    public void setPostPreview(String postPreview) {
        this.postPreview = postPreview;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
