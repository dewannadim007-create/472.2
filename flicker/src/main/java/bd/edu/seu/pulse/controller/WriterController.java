package bd.edu.seu.pulse.controller;

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

import java.util.List;

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
            return "redirect:/login";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login";
        }

        List<PostViewDTO> posts = postService.getPostsByWriter(userId, sortBy);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", posts);
        model.addAttribute("userId", userId);
        model.addAttribute("sortBy", sortBy);

        return "writer/home";
    }

    @GetMapping("/post/new")
    public String showNewPostForm(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login";
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
            return "redirect:/login";
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
            return "redirect:/login";
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
            return "redirect:/login";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login";
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
            return "redirect:/login";
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
            return "redirect:/login";
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
            return "redirect:/login";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a writer");
            return "redirect:/login";
        }

        List<User> followers = userService.getFollowers(userId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("followers", followers);
        model.addAttribute("userId", userId);
        model.addAttribute("sortBy", sortBy);

        return "writer/followers";
    }
}