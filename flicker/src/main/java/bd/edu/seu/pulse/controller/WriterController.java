package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.ActivityDTO;
import bd.edu.seu.pulse.dto.CreatePostDTO;
import bd.edu.seu.pulse.dto.PostViewDTO;
import bd.edu.seu.pulse.model.Post;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.PostService;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/writer")
public class WriterController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String userId,
            @RequestParam(required = false) String sortBy,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login/writer";
        }

        List<PostViewDTO> posts = postService.getPostsByWriter(userId, sortBy);

        int totalComments = posts.stream()
                .mapToInt(post -> post.getComments() != null ? post.getComments().size() : 0)
                .sum();

        List<User> followers = userService.getFollowers(userId);
        int followerCount = followers != null ? followers.size() : 0;

        final List<ActivityDTO> recentActivities = new ArrayList<>();

        posts.stream().limit(3).forEach(post -> {
            recentActivities.add(new ActivityDTO(
                    "POST",
                    currentUser.getName(),
                    post.getContent().length() > 60 ? post.getContent().substring(0, 60) + "..." : post.getContent(),
                    null,
                    getTimeAgo(post.getTimestamp())));
        });

        posts.forEach(post -> {
            if (post.getComments() != null && !post.getComments().isEmpty()) {
                post.getComments().forEach(comment -> {
                    recentActivities.add(new ActivityDTO(
                            "COMMENT",
                            comment.getAuthorName(),
                            post.getContent().length() > 40 ? post.getContent().substring(0, 40) + "..."
                                    : post.getContent(),
                            comment.getContent(),
                            getTimeAgo(comment.getTimestamp())));
                });
            }
        });

        recentActivities.sort((a, b) -> 0);
        List<ActivityDTO> limitedActivities = recentActivities.size() > 10
                ? recentActivities.subList(0, 10)
                : recentActivities;

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("userId", userId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("totalComments", totalComments);
        model.addAttribute("followerCount", followerCount);
        model.addAttribute("recentActivities", limitedActivities);

        return "writer/home";
    }

    private String getTimeAgo(LocalDateTime timestamp) {
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

    @GetMapping("/my-posts")
    public String myPosts(@RequestParam(required = false) String userId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String filterBy,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login/writer";
        }

        List<PostViewDTO> posts = postService.getPostsByWriter(userId, sortBy);

        if (filterBy != null && !filterBy.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> filterBy.equalsIgnoreCase(post.getVisibility()))
                    .collect(Collectors.toList());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("userId", userId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("filterBy", filterBy);

        return "writer/my-posts";
    }

    @GetMapping("/post/new")
    public String showNewPostForm(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login/writer";
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("createPostDTO", new CreatePostDTO());
        model.addAttribute("userId", userId);

        return "writer/new-post";
    }

    @PostMapping("/post")
    public String createPost(@ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam String userId,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        String result = postService.createPost(userId, createPostDTO);

        if ("SUCCESS".equals(result)) {
            model.addAttribute("successMessage", "Post created successfully!");
        } else {
            model.addAttribute("errorMessage", result);
            model.addAttribute("createPostDTO", createPostDTO);
            model.addAttribute("userId", userId);
            return "writer/new-post";
        }

        return "redirect:/writer/home?userId=" + userId;
    }

    @PostMapping("/post/with-image")
    public String createPostWithImage(@ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam String userId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        String result;
        if (imageFile != null && !imageFile.isEmpty()) {
            result = postService.createPostWithImage(userId, createPostDTO, imageFile);
        } else {
            result = postService.createPost(userId, createPostDTO);
        }

        if ("SUCCESS".equals(result)) {
            model.addAttribute("successMessage", "Post created successfully!");
        } else {
            model.addAttribute("errorMessage", result);
            model.addAttribute("createPostDTO", createPostDTO);
            model.addAttribute("userId", userId);
            return "writer/new-post";
        }

        return "redirect:/writer/home?userId=" + userId;
    }

    @GetMapping("/post/{postId}/edit")
    public String showEditPostForm(@PathVariable String postId,
            @RequestParam String userId,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login/writer";
        }

        Post post = postService.getPostById(postId);
        if (post == null) {
            model.addAttribute("errorMessage", "Post not found");
            return "redirect:/writer/home?userId=" + userId;
        }

        if (!post.getAuthorId().equals(userId)) {
            model.addAttribute("errorMessage", "You can only edit your own posts");
            return "redirect:/writer/home?userId=" + userId;
        }

        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setContent(post.getContent());
        if (post.getImageCaption() != null) {
            createPostDTO.setImageCaption(post.getImageCaption());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("createPostDTO", createPostDTO);
        model.addAttribute("post", post);
        model.addAttribute("userId", userId);

        return "writer/edit-post";
    }

    @PostMapping("/post/{postId}/edit")
    public String editPost(@PathVariable String postId,
            @ModelAttribute CreatePostDTO createPostDTO,
            @RequestParam String userId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", required = false, defaultValue = "false") boolean removeImage,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        String result = postService.updatePost(postId, userId, createPostDTO, imageFile, removeImage);

        if ("SUCCESS".equals(result)) {
            model.addAttribute("successMessage", "Post updated successfully!");
        } else {
            Post post = postService.getPostById(postId);
            User currentUser = userService.getUserById(userId);

            model.addAttribute("errorMessage", result);
            model.addAttribute("createPostDTO", createPostDTO);
            model.addAttribute("post", post);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("userId", userId);
            return "writer/edit-post";
        }

        return "redirect:/writer/home?userId=" + userId;
    }

    @PostMapping("/post/{postId}/delete")
    public String deletePost(@PathVariable String postId,
            @RequestParam String userId,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        String result = postService.deletePost(postId, userId);

        if ("SUCCESS".equals(result)) {
            model.addAttribute("successMessage", "Post deleted successfully!");
        } else {
            model.addAttribute("errorMessage", result);
        }

        return "redirect:/writer/home?userId=" + userId;
    }

    @GetMapping("/followers")
    public String followers(@RequestParam(required = false) String userId,
            @RequestParam(required = false) String sortBy,
            Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/writer";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login/writer";
        }

        List<User> followers = userService.getFollowers(userId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("followers", followers);
        model.addAttribute("userId", userId);
        model.addAttribute("sortBy", sortBy);

        return "writer/followers";
    }
}