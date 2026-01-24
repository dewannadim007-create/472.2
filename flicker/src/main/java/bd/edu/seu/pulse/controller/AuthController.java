package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.RegistrationDTO;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "landing";
    }

    @GetMapping("/landing")
    public String landing() {
        return "landing";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute RegistrationDTO registrationDTO,
            @RequestParam(value = "selectedInterests", required = false) List<String> selectedInterests,
            Model model) {
        if (selectedInterests != null) {
            registrationDTO.setInterests(selectedInterests);
        } else {
            registrationDTO.setInterests(new ArrayList<>());
        }

        String result = userService.register(registrationDTO);

        if ("SUCCESS".equals(result)) {
            return "redirect:/landing?registered=true";
        } else {
            model.addAttribute("errorMessage", result);
            model.addAttribute("registrationDTO", registrationDTO);
            return "register";
        }
    }

    @GetMapping("/login/reader")
    public String showReaderLoginForm(Model model) {
        model.addAttribute("loginType", "reader");
        return "login-reader";
    }

    @GetMapping("/login/writer")
    public String showWriterLoginForm(Model model) {
        model.addAttribute("loginType", "writer");
        return "login-writer";
    }

    @GetMapping("/login/admin")
    public String showAdminLoginForm(Model model) {
        model.addAttribute("loginType", "admin");
        return "login-admin";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/landing";
    }
}