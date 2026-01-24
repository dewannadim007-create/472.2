package bd.edu.seu.pulse.dto;

public class ActivityDTO {
    private String type;
    private String userName;
    private String postPreview;
    private String commentContent;
    private String timeAgo;

    public ActivityDTO() {
    }

    public ActivityDTO(String type, String userName, String postPreview, String commentContent, String timeAgo) {
        this.type = type;
        this.userName = userName;
        this.postPreview = postPreview;
        this.commentContent = commentContent;
        this.timeAgo = timeAgo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostPreview() {
        return postPreview;
    }

    public void setPostPreview(String postPreview) {
        this.postPreview = postPreview;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
