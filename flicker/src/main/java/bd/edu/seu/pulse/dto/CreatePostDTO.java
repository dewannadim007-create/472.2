package bd.edu.seu.pulse.dto;

public class CreatePostDTO {
    private String content;
    private String imageCaption;
    private String visibility = "PUBLIC";

    public CreatePostDTO() {
    }

    public CreatePostDTO(String content) {
        this.content = content;
    }

    public CreatePostDTO(String content, String imageCaption) {
        this.content = content;
        this.imageCaption = imageCaption;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageCaption() {
        return imageCaption;
    }

    public void setImageCaption(String imageCaption) {
        this.imageCaption = imageCaption;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}