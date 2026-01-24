package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.model.Post;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.PostService;
import bd.edu.seu.pulse.service.ReportService;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private PostService postService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = userService.findAll();
        List<Post> posts = postService.getAllPosts();

        long totalUsers = users.size();
        long activeUsers = users.stream().filter(u -> "ACTIVE".equals(u.getAccountStatus())).count();
        long freezedUsers = users.stream().filter(u -> "FREEZED".equals(u.getAccountStatus())).count();
        long totalPosts = posts.size();

        LocalDate today = LocalDate.now();
        long dailyPosts = posts.stream()
                .filter(p -> p.getTimestamp() != null && p.getTimestamp().toLocalDate().equals(today))
                .count();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("activeUsers", activeUsers);
        model.addAttribute("freezedUsers", freezedUsers);
        model.addAttribute("totalPosts", totalPosts);
        model.addAttribute("dailyPosts", dailyPosts);
        model.addAttribute("pendingReports", reportService.getPendingReports());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model,
            @RequestParam(required = false, defaultValue = "asc") String sort,
            @RequestParam(required = false, defaultValue = "all") String role) {
        List<User> allUsers = userService.findAll();

        java.util.stream.Stream<User> userStream = allUsers.stream()
                .filter(u -> !"ADMIN".equals(u.getRole()));

        if ("reader".equalsIgnoreCase(role)) {
            userStream = userStream.filter(u -> "READER".equals(u.getRole()));
        } else if ("writer".equalsIgnoreCase(role)) {
            userStream = userStream.filter(u -> "WRITER".equals(u.getRole()));
        }

        java.util.Comparator<User> comparator = java.util.Comparator.comparing(
                u -> u.getName() != null ? u.getName().toLowerCase() : "",
                java.util.Comparator.naturalOrder());
        if ("desc".equalsIgnoreCase(sort)) {
            comparator = comparator.reversed();
        }

        List<User> filteredUsers = userStream
                .sorted(comparator)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("users", filteredUsers);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentRole", role);
        return "admin/users";
    }

    @GetMapping("/users/add")
    public String addUserForm() {
        return "admin/add-user";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String username,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            @RequestParam(required = false) List<String> selectedInterests) {
        User user = new User();
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setInterests(selectedInterests);
        user.setAccountStatus("ACTIVE");
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/status")
    public String updateUserStatus(@PathVariable String userId, @RequestParam String status) {
        userService.updateUserStatus(userId, status);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/delete")
    public String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{userId}/edit")
    public String editUserForm(@PathVariable String userId, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("user", user);
        return "admin/edit-user";
    }

    @PostMapping("/users/{userId}/edit")
    public String editUser(@PathVariable String userId,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam String accountStatus,
            @RequestParam(required = false) List<String> selectedInterests) {
        User user = userService.getUserById(userId);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            user.setAccountStatus(accountStatus);
            user.setInterests(selectedInterests != null ? selectedInterests : new java.util.ArrayList<>());
            userService.updateUser(user);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("reports", reportService.getAllReports());
        return "admin/reports";
    }

    @PostMapping("/reports/{reportId}/resolve")
    public String resolveReport(@PathVariable String reportId, @RequestParam String action,
            @RequestParam(required = false) String userId) {
        if ("FREEZE".equals(action) && userId != null) {
            userService.updateUserStatus(userId, "FREEZED");
            reportService.updateReportStatus(reportId, "RESOLVED");
        } else if ("DISMISS".equals(action)) {
            reportService.updateReportStatus(reportId, "DISMISSED");
        }
        return "redirect:/admin/reports";
    }
}
