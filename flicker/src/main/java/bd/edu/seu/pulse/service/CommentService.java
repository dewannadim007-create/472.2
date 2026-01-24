package bd.edu.seu.pulse.service;

import bd.edu.seu.pulse.dto.CommentViewDTO;
import bd.edu.seu.pulse.dto.CreateCommentDTO;
import bd.edu.seu.pulse.model.Comment;
import bd.edu.seu.pulse.model.Post;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.CommentRepository;
import bd.edu.seu.pulse.repository.PostRepository;
import bd.edu.seu.pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public String createComment(String postId, String authorId, CreateCommentDTO dto) {
        if (postId == null || postId.trim().isEmpty()) {
            return "Post ID is required";
        }
        if (authorId == null || authorId.trim().isEmpty()) {
            return "Author ID is required";
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return "Comment content is required";
        }

        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return "Post not found";
        }

        User author = userRepository.findById(authorId).orElse(null);
        if (author == null) {
            return "Author not found";
        }

        if ("FREEZED".equals(author.getAccountStatus())) {
            return "Your account is freezed. You cannot comment.";
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setAuthorId(authorId);
        comment.setContent(dto.getContent().trim());
        comment.setTimestamp(LocalDateTime.now());
        comment.setUpvoteCount(0);
        comment.setDownvoteCount(0);
        comment.setUpvotedBy(new ArrayList<>());
        comment.setDownvotedBy(new ArrayList<>());

        commentRepository.save(comment);
        return "SUCCESS";
    }

    public List<CommentViewDTO> getCommentsByPost(String postId, String currentUserId) {
        if (postId == null || postId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        List<Comment> comments = commentRepository.findByPostId(postId, sort);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> authorNames = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        return comments.stream()
                .map(comment -> toCommentViewDTO(comment,
                        authorNames.getOrDefault(comment.getAuthorId(), "Unknown Author"), currentUserId))
                .collect(Collectors.toList());
    }

    public List<CommentViewDTO> getCommentsForPosts(List<String> postIds, String currentUserId) {
        if (postIds == null || postIds.isEmpty()) {
            return new ArrayList<>();
        }

        Sort sort = Sort.by(Sort.Direction.ASC, "timestamp");
        List<Comment> comments = commentRepository.findByPostIdIn(postIds, sort);

        if (comments.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .collect(Collectors.toList());

        Map<String, String> authorNames = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        return comments.stream()
                .map(comment -> toCommentViewDTO(comment,
                        authorNames.getOrDefault(comment.getAuthorId(), "Unknown Author"), currentUserId))
                .collect(Collectors.toList());
    }

    public String upvoteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || userId == null) {
            return "Comment not found or invalid user";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && "FREEZED".equals(user.getAccountStatus())) {
            return "Your account is freezed. You cannot vote.";
        }

        boolean alreadyUpvoted = comment.getUpvotedBy().contains(userId);
        boolean alreadyDownvoted = comment.getDownvotedBy().contains(userId);

        if (alreadyUpvoted) {
            comment.getUpvotedBy().remove(userId);
        } else {
            comment.getUpvotedBy().add(userId);
            if (alreadyDownvoted) {
                comment.getDownvotedBy().remove(userId);
            }
        }

        comment.setUpvoteCount(comment.getUpvotedBy().size());
        comment.setDownvoteCount(comment.getDownvotedBy().size());
        commentRepository.save(comment);
        return "SUCCESS";
    }

    public String downvoteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || userId == null) {
            return "Comment not found or invalid user";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && "FREEZED".equals(user.getAccountStatus())) {
            return "Your account is freezed. You cannot vote.";
        }

        boolean alreadyUpvoted = comment.getUpvotedBy().contains(userId);
        boolean alreadyDownvoted = comment.getDownvotedBy().contains(userId);

        if (alreadyDownvoted) {
            comment.getDownvotedBy().remove(userId);
        } else {
            comment.getDownvotedBy().add(userId);
            if (alreadyUpvoted) {
                comment.getUpvotedBy().remove(userId);
            }
        }

        comment.setUpvoteCount(comment.getUpvotedBy().size());
        comment.setDownvoteCount(comment.getDownvotedBy().size());
        commentRepository.save(comment);
        return "SUCCESS";
    }

    public Comment getCommentById(String commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

    public String updateComment(String commentId, String authorId, CreateCommentDTO dto) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return "Comment not found";
        }

        if (!comment.getAuthorId().equals(authorId)) {
            return "You can only edit your own comments";
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return "Comment content is required";
        }

        comment.setContent(dto.getContent().trim());
        commentRepository.save(comment);
        return "SUCCESS";
    }

    public String deleteComment(String commentId, String authorId) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            return "Comment not found";
        }

        if (!comment.getAuthorId().equals(authorId)) {
            return "You can only delete your own comments";
        }

        commentRepository.delete(comment);
        return "SUCCESS";
    }

    private CommentViewDTO toCommentViewDTO(Comment comment, String authorName, String currentUserId) {
        CommentViewDTO dto = new CommentViewDTO(
                comment.getId(),
                comment.getPostId(),
                comment.getAuthorId(),
                authorName,
                comment.getContent(),
                comment.getTimestamp(),
                comment.getUpvoteCount(),
                comment.getDownvoteCount());

        if (currentUserId != null) {
            dto.setHasUpvoted(comment.getUpvotedBy().contains(currentUserId));
            dto.setHasDownvoted(comment.getDownvotedBy().contains(currentUserId));
            dto.setCanEdit(comment.getAuthorId().equals(currentUserId));
        }

        return dto;
    }
}