package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.CreateCommentDTO;
import bd.edu.seu.pulse.dto.PostViewDTO;
import bd.edu.seu.pulse.dto.ReaderActivityDTO;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.PostService;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            return "redirect:/login/reader";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a reader");
            return "redirect:/login/reader";
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
            return "redirect:/login/reader";
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a reader");
            return "redirect:/login/reader";
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
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> upvotePost(@PathVariable String postId,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body("User ID is required");
        }

        String result = postService.upvotePost(postId, userId);
        if ("SUCCESS".equals(result)) {
            return org.springframework.http.ResponseEntity.ok("SUCCESS");
        } else {
            return org.springframework.http.ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/post/{postId}/downvote")
    @ResponseBody
    public org.springframework.http.ResponseEntity<String> downvotePost(@PathVariable String postId,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return org.springframework.http.ResponseEntity.badRequest().body("User ID is required");
        }

        String result = postService.downvotePost(postId, userId);
        if ("SUCCESS".equals(result)) {
            return org.springframework.http.ResponseEntity.ok("SUCCESS");
        } else {
            return org.springframework.http.ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/writer/{writerId}")
    public String viewWriterProfile(@PathVariable String writerId,
            @RequestParam String userId,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login/reader";
        }

        User currentUser = userService.getUserById(userId);
        User writer = userService.getUserById(writerId);

        if (currentUser == null || writer == null || !"WRITER".equals(writer.getRole())) {
            model.addAttribute("errorMessage", "Writer not found");
            return "redirect:/reader/home?userId=" + userId;
        }

        boolean isFollowing = userService.isFollowing(userId, writerId);
        List<PostViewDTO> posts = postService.getWriterPostsForReader(userId, writerId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("writer", writer);
        model.addAttribute("posts", posts);
        model.addAttribute("userId", userId);
        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("createCommentDTO", new CreateCommentDTO());

        return "reader/writer-profile";
    }

    @GetMapping("/activity")
    public String showActivityPage(@RequestParam String userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String time,
            Model model) {
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "Invalid user or not a reader");
            return "redirect:/login/reader";
        }

        List<ReaderActivityDTO> activities = postService.getReaderActivity(userId);

        if (type != null && !type.trim().isEmpty() && !"ALL".equalsIgnoreCase(type)) {
            activities = activities.stream()
                    .filter(a -> type.equalsIgnoreCase(a.getType()))
                    .collect(Collectors.toList());
        }

        if (time != null && !time.trim().isEmpty() && !"ALL".equalsIgnoreCase(time)) {
            LocalDateTime cutoff = LocalDateTime.now();
            if ("24H".equalsIgnoreCase(time)) {
                cutoff = cutoff.minusDays(1);
            } else if ("7D".equalsIgnoreCase(time)) {
                cutoff = cutoff.minusDays(7);
            } else if ("30D".equalsIgnoreCase(time)) {
                cutoff = cutoff.minusMonths(1);
            }
            LocalDateTime finalCutoff = cutoff;
            activities = activities.stream()
                    .filter(a -> a.getTimestamp().isAfter(finalCutoff))
                    .collect(Collectors.toList());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", userId);
        model.addAttribute("activities", activities);
        model.addAttribute("selectedType", type != null ? type : "ALL");
        model.addAttribute("selectedTime", time != null ? time : "ALL");

        return "reader/activity";
    }
}