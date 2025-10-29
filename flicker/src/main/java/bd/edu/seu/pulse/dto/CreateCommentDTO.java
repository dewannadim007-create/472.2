package bd.edu.seu.pulse.dto;

public class CreateCommentDTO {
    private String content;

    public CreateCommentDTO() {
    }

    public CreateCommentDTO(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 