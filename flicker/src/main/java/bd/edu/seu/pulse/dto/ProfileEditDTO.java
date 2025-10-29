package bd.edu.seu.pulse.dto;

import java.util.List;

import java.util.ArrayList;

public class ProfileEditDTO {
    private String username;
    private String name;
    private String password;
    private String confirmPassword;
    private List<String> interests;
    private String interestsString;

    public ProfileEditDTO() {
    }

    public ProfileEditDTO(String username, String name, String password, String confirmPassword, List<String> interests) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.interests = interests;
        if (interests != null) {
            this.interestsString = String.join(", ", interests);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
        if (interests != null) {
            this.interestsString = String.join(", ", interests);
        }
    }

    public String getInterestsString() {
        return interestsString;
    }

    public void setInterestsString(String interestsString) {
        this.interestsString = interestsString;
        if (interestsString != null && !interestsString.trim().isEmpty()) {
            this.interests = List.of(interestsString.split("\\s*,\\s*"));
        } else {
            this.interests = new ArrayList<>();
        }
    }
} 