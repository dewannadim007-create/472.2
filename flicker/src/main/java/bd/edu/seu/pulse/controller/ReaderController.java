package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.CreateCommentDTO;
import bd.edu.seu.pulse.dto.PostViewDTO;
import bd.edu.seu.pulse.model.Post;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.PostService;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/reader")
public class ReaderController {
    
    @Autowired
    private PostService postService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/home")
    public String home(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a reader");
            return "redirect:/login";
        }
        
        List<PostViewDTO> feedPosts = postService.getFeedForReader(userId);
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("posts", feedPosts);
        model.addAttribute("userId", userId);
        model.addAttribute("createCommentDTO", new CreateCommentDTO());
        
        return "reader/home";
    }
    
    @GetMapping("/suggestions")
    public String suggestions(@RequestParam(required = false) String userId,
                            @RequestParam(required = false) String interest,
                            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a reader");
            return "redirect:/login";
        }
        
        List<User> writers;
        if (interest != null && !interest.trim().isEmpty()) {
            String normalizedInterest = interest.trim().toLowerCase();
            writers = userService.getWritersByInterestExcludingFollowing(userId, Arrays.asList(normalizedInterest));
        } else {
            writers = userService.getWritersByInterestExcludingFollowing(userId, null);
        }
        
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("suggestedWriters", writers);
        model.addAttribute("userId", userId);
        model.addAttribute("selectedInterest", interest);
        
        return "reader/suggestions";
    }
    
    @GetMapping("/following")
    public String showFollowingPage(@RequestParam String userId, Model model) {
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "redirect:/login";
        }

        List<User> followingWriters = userService.getFollowingWriters(userId);
        if (followingWriters == null) {
            followingWriters = new ArrayList<>();
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", userId);
        model.addAttribute("followingWriters", followingWriters);

        return "reader/following";
    }
    
    @PostMapping("/follow/{writerId}")
    public String followWriter(@PathVariable String writerId,
                             @RequestParam String userId,
                             RedirectAttributes redirectAttributes) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        
        boolean success = userService.followWriter(userId, writerId);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully followed the writer!");
            return "redirect:/reader/suggestions?userId=" + userId;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to follow writer");
            return "redirect:/reader/suggestions?userId=" + userId + "&errorMessage=Failed to follow writer";
        }
    }
    
    @PostMapping("/unfollow/{writerId}")
    public String unfollowWriter(@PathVariable String writerId,
                               @RequestParam String userId,
                               RedirectAttributes redirectAttributes) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        
        boolean success = userService.unfollowWriter(userId, writerId);
        if (success) {
            redirectAttributes.addFlashAttribute("successMessage", "Successfully unfollowed the writer!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to unfollow writer");
        }
        
        return "redirect:/reader/following?userId=" + userId;
    }
    
    @PostMapping("/post/{postId}/upvote")
    public String upvotePost(@PathVariable String postId,
                           @RequestParam String userId,
                           Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        
        String result = postService.upvotePost(postId, userId);
        if (!"SUCCESS".equals(result)) {
            model.addAttribute("errorMessage", result);
        }
        
        return "redirect:/reader/home?userId=" + userId;
    }
    
    @PostMapping("/post/{postId}/downvote")
    public String downvotePost(@PathVariable String postId,
                             @RequestParam String userId,
                             Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        
        String result = postService.downvotePost(postId, userId);
        if (!"SUCCESS".equals(result)) {
            model.addAttribute("errorMessage", result);
        }
        
        return "redirect:/reader/home?userId=" + userId;
    }
} 