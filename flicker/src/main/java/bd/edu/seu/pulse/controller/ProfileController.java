package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.dto.ProfileEditDTO;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String showProfile(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", userId);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfileForm(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            model.addAttribute("errorMessage", "User not found");
            return "redirect:/login";
        }
        ProfileEditDTO profileEditDTO = new ProfileEditDTO();
        profileEditDTO.setUsername(currentUser.getUsername());
        profileEditDTO.setName(currentUser.getName());
        profileEditDTO.setInterests(currentUser.getInterests());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileEditDTO", profileEditDTO);
        model.addAttribute("userId", userId);
        return "profile";
    }

    @PostMapping("/profile/edit")
    public String editProfile(@ModelAttribute ProfileEditDTO profileEditDTO,
            @RequestParam String userId,
            @RequestParam(value = "selectedInterests", required = false) List<String> selectedInterests,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (selectedInterests != null) {
            profileEditDTO.setInterests(selectedInterests);
        } else {
            profileEditDTO.setInterests(new ArrayList<>());
        }
        String result = userService.updateProfile(userId, profileEditDTO);
        if ("SUCCESS".equals(result)) {
            return "redirect:/profile?userId=" + userId + "&successMessage=Profile updated successfully!";
        } else {
            return "redirect:/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @GetMapping("/reader/profile")
    public String showReaderProfile(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "User not found or not a reader");
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", userId);
        return "reader/profile";
    }

    @GetMapping("/reader/profile/edit")
    public String showEditReaderProfileForm(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"READER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "User not found or not a reader");
            return "redirect:/login";
        }
        ProfileEditDTO profileEditDTO = new ProfileEditDTO();
        profileEditDTO.setUsername(currentUser.getUsername());
        profileEditDTO.setName(currentUser.getName());
        profileEditDTO.setInterests(currentUser.getInterests());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileEditDTO", profileEditDTO);
        model.addAttribute("userId", userId);
        return "reader/profile";
    }

    @PostMapping("/reader/profile/edit")
    public String editReaderProfile(@ModelAttribute ProfileEditDTO profileEditDTO,
            @RequestParam String userId,
            @RequestParam(value = "selectedInterests", required = false) List<String> selectedInterests,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (selectedInterests != null) {
            profileEditDTO.setInterests(selectedInterests);
        } else {
            profileEditDTO.setInterests(new ArrayList<>());
        }
        String result = userService.updateProfile(userId, profileEditDTO);
        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/profile?userId=" + userId + "&successMessage=Profile updated successfully!";
        } else {
            return "redirect:/reader/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @GetMapping("/writer/profile")
    public String showWriterProfile(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "User not found or not a writer");
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("userId", userId);
        return "writer/profile";
    }

    @GetMapping("/writer/profile/edit")
    public String showEditWriterProfileForm(@RequestParam(required = false) String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("errorMessage", "User ID is required");
            return "redirect:/login";
        }
        User currentUser = userService.getUserById(userId);
        if (currentUser == null || !"WRITER".equals(currentUser.getRole())) {
            model.addAttribute("errorMessage", "User not found or not a writer");
            return "redirect:/login";
        }
        ProfileEditDTO profileEditDTO = new ProfileEditDTO();
        profileEditDTO.setUsername(currentUser.getUsername());
        profileEditDTO.setName(currentUser.getName());
        profileEditDTO.setInterests(currentUser.getInterests());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("profileEditDTO", profileEditDTO);
        model.addAttribute("userId", userId);
        return "writer/profile";
    }

    @PostMapping("/writer/profile/edit")
    public String editWriterProfile(@ModelAttribute ProfileEditDTO profileEditDTO,
            @RequestParam String userId,
            @RequestParam(value = "selectedInterests", required = false) List<String> selectedInterests,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }
        if (selectedInterests != null) {
            profileEditDTO.setInterests(selectedInterests);
        } else {
            profileEditDTO.setInterests(new ArrayList<>());
        }
        String result = userService.updateProfile(userId, profileEditDTO);
        if ("SUCCESS".equals(result)) {
            return "redirect:/writer/profile?userId=" + userId + "&successMessage=Profile updated successfully!";
        } else {
            return "redirect:/writer/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @PostMapping("/writer/profile/photo/upload")
    public String uploadWriterProfilePhoto(@RequestParam String userId,
            @RequestParam(value = "profilePhoto", required = false) org.springframework.web.multipart.MultipartFile photoFile,
            @RequestParam(value = "removePhoto", required = false, defaultValue = "false") boolean removePhoto,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        if (removePhoto) {
            String result = userService.removeProfilePhoto(userId);
            if ("SUCCESS".equals(result)) {
                return "redirect:/writer/profile?userId=" + userId + "&successMessage=Profile photo removed!";
            } else {
                return "redirect:/writer/profile?userId=" + userId + "&errorMessage=" + result;
            }
        }

        if (photoFile == null || photoFile.isEmpty()) {
            return "redirect:/writer/profile?userId=" + userId + "&errorMessage=Please select a photo to upload";
        }

        String result = userService.updateProfilePhoto(userId, photoFile);
        if ("SUCCESS".equals(result)) {
            return "redirect:/writer/profile?userId=" + userId + "&successMessage=Profile photo updated!";
        } else {
            return "redirect:/writer/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @PostMapping("/writer/profile/photo/remove")
    public String removeWriterProfilePhoto(@RequestParam String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = userService.removeProfilePhoto(userId);
        if ("SUCCESS".equals(result)) {
            return "redirect:/writer/profile?userId=" + userId + "&successMessage=Profile photo removed!";
        } else {
            return "redirect:/writer/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @PostMapping("/reader/profile/photo/upload")
    public String uploadReaderProfilePhoto(@RequestParam String userId,
            @RequestParam(value = "profilePhoto", required = false) org.springframework.web.multipart.MultipartFile photoFile,
            @RequestParam(value = "removePhoto", required = false, defaultValue = "false") boolean removePhoto,
            Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        if (removePhoto) {
            String result = userService.removeProfilePhoto(userId);
            if ("SUCCESS".equals(result)) {
                return "redirect:/reader/profile?userId=" + userId + "&successMessage=Profile photo removed!";
            } else {
                return "redirect:/reader/profile?userId=" + userId + "&errorMessage=" + result;
            }
        }

        if (photoFile == null || photoFile.isEmpty()) {
            return "redirect:/reader/profile?userId=" + userId + "&errorMessage=Please select a photo to upload";
        }

        String result = userService.updateProfilePhoto(userId, photoFile);
        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/profile?userId=" + userId + "&successMessage=Profile photo updated!";
        } else {
            return "redirect:/reader/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }

    @PostMapping("/reader/profile/photo/remove")
    public String removeReaderProfilePhoto(@RequestParam String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            return "redirect:/login";
        }

        String result = userService.removeProfilePhoto(userId);
        if ("SUCCESS".equals(result)) {
            return "redirect:/reader/profile?userId=" + userId + "&successMessage=Profile photo removed!";
        } else {
            return "redirect:/reader/profile?userId=" + userId + "&errorMessage=" + result;
        }
    }
}