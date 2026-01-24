package bd.edu.seu.pulse.service;

import bd.edu.seu.pulse.dto.CommentViewDTO;
import bd.edu.seu.pulse.dto.CreatePostDTO;
import bd.edu.seu.pulse.dto.PostViewDTO;
import bd.edu.seu.pulse.dto.ReaderActivityDTO;
import bd.edu.seu.pulse.model.Comment;
import bd.edu.seu.pulse.model.Post;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.CommentRepository;
import bd.edu.seu.pulse.repository.PostRepository;
import bd.edu.seu.pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FileUploadService fileUploadService;

    public String createPost(String authorId, CreatePostDTO dto) {
        if (authorId == null || authorId.isEmpty()) {
            return "Author ID is required";
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return "Post content is required";
        }

        User author = userRepository.findById(authorId).orElse(null);
        if (author == null) {
            return "Author not found";
        }
        if (!author.getRole().equals("WRITER")) {
            return "Only writers can create posts";
        }
        if ("FREEZED".equals(author.getAccountStatus())) {
            return "Your account is freezed. You cannot create posts.";
        }

        Post post = new Post();
        post.setAuthorId(authorId);
        post.setContent(dto.getContent().trim());
        post.setTimestamp(LocalDateTime.now());
        post.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : "PUBLIC");
        post.setUpvoteCount(0);
        post.setDownvoteCount(0);
        post.setUpvotedBy(new ArrayList<>());
        post.setDownvotedBy(new ArrayList<>());

        if (dto.getImageCaption() != null && !dto.getImageCaption().trim().isEmpty()) {
            post.setImageCaption(dto.getImageCaption().trim());
        }

        postRepository.save(post);
        return "SUCCESS";
    }

    public String createPostWithImage(String authorId, CreatePostDTO dto, MultipartFile imageFile) {
        if (authorId == null || authorId.isEmpty()) {
            return "Author ID is required";
        }
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return "Post content is required";
        }

        User author = userRepository.findById(authorId).orElse(null);
        if (author == null) {
            return "Author not found";
        }
        if (!author.getRole().equals("WRITER")) {
            return "Only writers can create posts";
        }
        if ("FREEZED".equals(author.getAccountStatus())) {
            return "Your account is freezed. You cannot create posts.";
        }

        Post post = new Post();
        post.setAuthorId(authorId);
        post.setContent(dto.getContent().trim());
        post.setTimestamp(LocalDateTime.now());
        post.setVisibility(dto.getVisibility() != null ? dto.getVisibility() : "PUBLIC");
        post.setUpvoteCount(0);
        post.setDownvoteCount(0);
        post.setUpvotedBy(new ArrayList<>());
        post.setDownvotedBy(new ArrayList<>());

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = fileUploadService.uploadImage(imageFile);
                post.setImageUrl(imageUrl);
                post.setImageFileName(imageFile.getOriginalFilename());
                post.setImageFileSize(imageFile.getSize());

                if (dto.getImageCaption() != null && !dto.getImageCaption().trim().isEmpty()) {
                    post.setImageCaption(dto.getImageCaption().trim());
                }
            } catch (Exception e) {
                return "Error uploading image: " + e.getMessage();
            }
        }

        postRepository.save(post);
        return "SUCCESS";
    }

    public List<PostViewDTO> getPostsByWriter(String writerId, String sortBy) {
        if (writerId == null || writerId.isEmpty()) {
            return new ArrayList<>();
        }

        User writer = userRepository.findById(writerId).orElse(null);
        if (writer == null) {
            return new ArrayList<>();
        }

        Sort sort;
        if ("oldest".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "timestamp");
        } else if ("upvotes".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "upvoteCount");
        } else if ("downvotes".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "downvoteCount");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "timestamp");
        }

        List<Post> posts = postRepository.findByAuthorId(writerId, sort);

        List<PostViewDTO> postDTOs = posts.stream()
                .map(post -> {
                    PostViewDTO dto = toPostViewDTO(post, writer.getName(), writerId);
                    List<CommentViewDTO> comments = commentService.getCommentsByPost(dto.getId(), writerId);
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());

        if ("most-comments".equals(sortBy)) {
            postDTOs.sort((p1, p2) -> Integer.compare(p2.getComments().size(), p1.getComments().size()));
        }

        return postDTOs;
    }

    public List<PostViewDTO> getFeedForReader(String readerId) {
        if (readerId == null || readerId.isEmpty()) {
            return new ArrayList<>();
        }

        User reader = userRepository.findById(readerId).orElse(null);
        if (reader == null || reader.getFollowingWriters() == null || reader.getFollowingWriters().isEmpty()) {
            return new ArrayList<>();
        }

        List<Post> posts = postRepository.findByAuthorIdIn(reader.getFollowingWriters(),
                Sort.by(Sort.Direction.DESC, "timestamp"));

        if (posts.isEmpty()) {
            return new ArrayList<>();
        }

        posts = posts.stream()
                .filter(post -> "PUBLIC".equals(post.getVisibility()))
                .collect(Collectors.toList());

        List<String> authorIds = posts.stream()
                .map(Post::getAuthorId)
                .distinct()
                .collect(Collectors.toList());
        Map<String, String> authorNames = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        List<PostViewDTO> postDTOs = posts.stream()
                .map(post -> {
                    PostViewDTO dto = toPostViewDTO(post,
                            authorNames.getOrDefault(post.getAuthorId(), "Unknown Author"), readerId);
                    List<CommentViewDTO> comments = commentService.getCommentsByPost(post.getId(), readerId);
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());

        postDTOs.sort((p1, p2) -> {
            LocalDateTime lastActivity1 = p1.getTimestamp();
            if (p1.getComments() != null && !p1.getComments().isEmpty()) {
                LocalDateTime latestCommentTime1 = p1.getComments().stream()
                        .map(CommentViewDTO::getTimestamp)
                        .max(LocalDateTime::compareTo)
                        .orElse(lastActivity1);
                if (latestCommentTime1.isAfter(lastActivity1)) {
                    lastActivity1 = latestCommentTime1;
                }
            }

            LocalDateTime lastActivity2 = p2.getTimestamp();
            if (p2.getComments() != null && !p2.getComments().isEmpty()) {
                LocalDateTime latestCommentTime2 = p2.getComments().stream()
                        .map(CommentViewDTO::getTimestamp)
                        .max(LocalDateTime::compareTo)
                        .orElse(lastActivity2);
                if (latestCommentTime2.isAfter(lastActivity2)) {
                    lastActivity2 = latestCommentTime2;
                }
            }

            return lastActivity2.compareTo(lastActivity1);
        });

        return postDTOs;
    }

    public String upvotePost(String postId, String userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || userId == null) {
            return "Post not found or invalid user";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && "FREEZED".equals(user.getAccountStatus())) {
            return "Your account is freezed. You cannot vote.";
        }

        boolean alreadyUpvoted = post.getUpvotedBy().contains(userId);
        boolean alreadyDownvoted = post.getDownvotedBy().contains(userId);

        if (alreadyUpvoted) {
            post.getUpvotedBy().remove(userId);
        } else {
            post.getUpvotedBy().add(userId);
            if (alreadyDownvoted) {
                post.getDownvotedBy().remove(userId);
            }
        }

        post.setUpvoteCount(post.getUpvotedBy().size());
        post.setDownvoteCount(post.getDownvotedBy().size());
        postRepository.save(post);
        return "SUCCESS";
    }

    public String downvotePost(String postId, String userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null || userId == null) {
            return "Post not found or invalid user";
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user != null && "FREEZED".equals(user.getAccountStatus())) {
            return "Your account is freezed. You cannot vote.";
        }

        boolean alreadyUpvoted = post.getUpvotedBy().contains(userId);
        boolean alreadyDownvoted = post.getDownvotedBy().contains(userId);

        if (alreadyDownvoted) {
            post.getDownvotedBy().remove(userId);
        } else {
            post.getDownvotedBy().add(userId);
            if (alreadyUpvoted) {
                post.getUpvotedBy().remove(userId);
            }
        }

        post.setUpvoteCount(post.getUpvotedBy().size());
        post.setDownvoteCount(post.getDownvotedBy().size());
        postRepository.save(post);
        return "SUCCESS";
    }

    public Post getPostById(String postId) {
        return postRepository.findById(postId).orElse(null);
    }

    public String updatePost(String postId, String authorId, CreatePostDTO dto) {
        return updatePost(postId, authorId, dto, null, false);
    }

    public String updatePost(String postId, String authorId, CreatePostDTO dto, MultipartFile imageFile,
            boolean removeImage) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return "Post not found";
        }

        if (!post.getAuthorId().equals(authorId)) {
            return "You can only edit your own posts";
        }

        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            return "Post content is required";
        }

        post.setContent(dto.getContent().trim());

        if (dto.getVisibility() != null) {
            post.setVisibility(dto.getVisibility());
        }

        if (dto.getImageCaption() != null) {
            post.setImageCaption(dto.getImageCaption().trim());
        }

        if (removeImage) {
            if (post.getImageUrl() != null) {
                fileUploadService.deleteImage(post.getImageUrl());
                post.setImageUrl(null);
                post.setImageFileName(null);
                post.setImageFileSize(null);
                post.setImageCaption(null);
            }
        } else if (imageFile != null && !imageFile.isEmpty()) {
            try {
                if (post.getImageUrl() != null) {
                    fileUploadService.deleteImage(post.getImageUrl());
                }

                String imageUrl = fileUploadService.uploadImage(imageFile);
                post.setImageUrl(imageUrl);
                post.setImageFileName(imageFile.getOriginalFilename());
                post.setImageFileSize(imageFile.getSize());
            } catch (Exception e) {
                return "Error uploading image: " + e.getMessage();
            }
        }

        postRepository.save(post);
        return "SUCCESS";
    }

    public String deletePost(String postId, String authorId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return "Post not found";
        }

        if (!post.getAuthorId().equals(authorId)) {
            return "You can only delete your own posts";
        }

        postRepository.delete(post);
        return "SUCCESS";
    }

    public List<PostViewDTO> getWriterPostsForReader(String readerId, String writerId) {
        if (readerId == null || writerId == null) {
            return new ArrayList<>();
        }

        User reader = userRepository.findById(readerId).orElse(null);
        User writer = userRepository.findById(writerId).orElse(null);
        if (reader == null || writer == null) {
            return new ArrayList<>();
        }

        boolean isFollowing = reader.getFollowingWriters() != null && reader.getFollowingWriters().contains(writerId);
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");

        List<Post> posts;
        if (isFollowing) {
            posts = postRepository.findByAuthorId(writerId, sort);
        } else {
            posts = postRepository.findByAuthorIdAndVisibility(writerId, "PUBLIC", sort);
        }

        return posts.stream()
                .map(post -> {
                    PostViewDTO dto = toPostViewDTO(post, writer.getName(), readerId);
                    List<CommentViewDTO> comments = commentService.getCommentsByPost(post.getId(), readerId);
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<PostViewDTO> getAllPosts(String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "timestamp");
        if ("oldest".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "timestamp");
        } else if ("upvotes".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "upvoteCount");
        } else if ("downvotes".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "downvoteCount");
        }

        List<Post> posts = postRepository.findAll(sort);

        List<PostViewDTO> postDTOs = posts.stream()
                .map(post -> {
                    User author = userRepository.findById(post.getAuthorId()).orElse(null);
                    String authorName = author != null ? author.getName() : "Unknown Author";
                    PostViewDTO dto = toPostViewDTO(post, authorName);
                    List<CommentViewDTO> comments = commentService.getCommentsByPost(post.getId(), null);
                    dto.setComments(comments);
                    return dto;
                })
                .collect(Collectors.toList());

        if ("most-comments".equals(sortBy)) {
            postDTOs.sort((p1, p2) -> Integer.compare(p2.getComments().size(), p1.getComments().size()));
        }

        return postDTOs;
    }

    private PostViewDTO toPostViewDTO(Post post, String authorName) {
        return toPostViewDTO(post, authorName, null);
    }

    private PostViewDTO toPostViewDTO(Post post, String authorName, String currentUserId) {
        PostViewDTO dto = new PostViewDTO();
        dto.setId(post.getId());
        dto.setAuthorId(post.getAuthorId());
        dto.setAuthorName(authorName);
        dto.setContent(post.getContent());
        dto.setTimestamp(post.getTimestamp());
        dto.setUpvoteCount(post.getUpvoteCount());
        dto.setDownvoteCount(post.getDownvoteCount());
        dto.setImageUrl(post.getImageUrl());
        dto.setImageCaption(post.getImageCaption());
        dto.setImageFileName(post.getImageFileName());
        dto.setImageFileSize(post.getImageFileSize());
        dto.setVisibility(post.getVisibility());

        if (currentUserId != null) {
            dto.setHasUpvoted(post.getUpvotedBy().contains(currentUserId));
            dto.setHasDownvoted(post.getDownvotedBy().contains(currentUserId));
        }

        return dto;
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public List<ReaderActivityDTO> getReaderActivity(String userId) {
        List<ReaderActivityDTO> activities = new ArrayList<>();

        List<Post> upvotedPosts = postRepository.findByUpvotedByContaining(userId);
        for (Post post : upvotedPosts) {
            String preview = post.getContent().length() > 50 ? post.getContent().substring(0, 50) + "..."
                    : post.getContent();
            activities.add(new ReaderActivityDTO("UPVOTE", preview, post.getId(), null, post.getTimestamp()));
        }

        List<Post> downvotedPosts = postRepository.findByDownvotedByContaining(userId);
        for (Post post : downvotedPosts) {
            String preview = post.getContent().length() > 50 ? post.getContent().substring(0, 50) + "..."
                    : post.getContent();
            activities.add(new ReaderActivityDTO("DOWNVOTE", preview, post.getId(), null, post.getTimestamp()));
        }

        List<Comment> userComments = commentRepository.findByAuthorId(userId);
        for (Comment comment : userComments) {
            Post post = postRepository.findById(comment.getPostId()).orElse(null);
            String postPreview = post != null
                    ? (post.getContent().length() > 50 ? post.getContent().substring(0, 50) + "..." : post.getContent())
                    : "Deleted Post";
            activities.add(new ReaderActivityDTO("COMMENT", postPreview, comment.getPostId(), comment.getContent(),
                    comment.getTimestamp()));
        }

        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        for (ReaderActivityDTO activity : activities) {
            activity.setTimeAgo(getTimeAgo(activity.getTimestamp()));
        }

        return activities;
    }

    public String getTimeAgo(LocalDateTime timestamp) {
        if (timestamp == null)
            return "recently";

        Duration duration = Duration.between(timestamp, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 1)
            return "just now";
        if (minutes < 60)
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        if (hours < 24)
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        if (days < 7)
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        return timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }
}