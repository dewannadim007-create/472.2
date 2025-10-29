package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.getUserByUsername(username);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "redirect:/login";
        }
        
        if ("READER".equals(user.getRole())) {
            return "redirect:/reader/home?userId=" + user.getId();
        } else if ("WRITER".equals(user.getRole())) {
            return "redirect:/writer/home?userId=" + user.getId();
        } else {
            model.addAttribute("errorMessage", "Invalid user role");
            return "redirect:/login";
        }
    }
} 