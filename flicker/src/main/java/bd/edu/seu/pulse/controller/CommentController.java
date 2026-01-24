package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.CreateCommentDTO;
import bd.edu.seu.pulse.model.Comment;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.CommentService;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public String createComment(@ModelAttribute CreateCommentDTO createCommentDTO,
            @RequestParam String userId,
            @RequestParam String postId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }

        String result = commentService.createComment(postId, userId, createCommentDTO);

        String redirectUrl = "WRITER".equals(user.getRole())
                ? "/writer/home?userId=" + userId
                : "/reader/home?userId=" + userId;

        if ("SUCCESS".equals(result)) {
            return "redirect:" + redirectUrl;
        } else {
            return "redirect:" + redirectUrl + "&error=" + result;
        }
    }

    @PostMapping("/{commentId}/edit")
    public String editComment(@PathVariable String commentId,
            @ModelAttribute CreateCommentDTO createCommentDTO,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = commentService.updateComment(commentId, userId, createCommentDTO);

        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/home?userId=" + userId;
        } else {
            return "redirect:/reader/home?userId=" + userId + "&error=" + result;
        }
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable String commentId,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = commentService.deleteComment(commentId, userId);

        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/home?userId=" + userId;
        } else {
            return "redirect:/reader/home?userId=" + userId + "&error=" + result;
        }
    }

    @PostMapping("/{commentId}/upvote")
    public String upvoteComment(@PathVariable String commentId,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = commentService.upvoteComment(commentId, userId);

        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/home?userId=" + userId;
        } else {
            return "redirect:/reader/home?userId=" + userId + "&error=" + result;
        }
    }

    @PostMapping("/{commentId}/downvote")
    public String downvoteComment(@PathVariable String commentId,
            @RequestParam String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = commentService.downvoteComment(commentId, userId);

        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/home?userId=" + userId;
        } else {
            return "redirect:/reader/home?userId=" + userId + "&error=" + result;
        }
    }

    @GetMapping("/{commentId}/edit")
    public String showEditCommentForm(@PathVariable String commentId,
            @RequestParam String userId,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/reader/home?userId=" + userId;
        }

        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "redirect:/login";
        }

        Comment comment = commentService.getCommentById(commentId);
        if (comment == null) {
            model.addAttribute("errorMessage", "Comment not found");
            return "redirect:/reader/home?userId=" + userId;
        }

        if (!comment.getAuthorId().equals(userId)) {
            model.addAttribute("errorMessage", "You can only edit your own comments");
            return "redirect:/reader/home?userId=" + userId;
        }

        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setContent(comment.getContent());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("createCommentDTO", createCommentDTO);
        model.addAttribute("comment", comment);
        model.addAttribute("userId", userId);

        return "reader/edit-comment";
    }
}