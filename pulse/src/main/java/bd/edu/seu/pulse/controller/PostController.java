package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @PostMapping("/vote/{postId}")
    public String votePost(@PathVariable String postId,
                          @RequestParam String vote,
                          @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        
        String result;
        if ("up".equals(vote)) {
            result = postService.upvotePost(postId, userId);
        } else if ("down".equals(vote)) {
            result = postService.downvotePost(postId, userId);
        } else {
            return "redirect:/reader/home?userId=" + userId;
        }
        
        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/home?userId=" + userId;
        } else {
            return "redirect:/reader/home?userId=" + userId + "&error=" + result;
        }
    }
} 