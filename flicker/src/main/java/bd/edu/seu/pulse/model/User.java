package bd.edu.seu.pulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Indexed(unique = true)
    private String username;
    private String password;
    private String name;
    private String role;
    private List<String> interests;
    private List<String> followingWriters;
    private List<String> followers;
    private String profilePhotoUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public List<String> getFollowingWriters() {
        return followingWriters;
    }

    public void setFollowingWriters(List<String> followingWriters) {
        this.followingWriters = followingWriters;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public User() {
    }

    public User(String id, String username, String password, String name, String role, List<String> interests,
            List<String> followingWriters, List<String> followers) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.interests = interests;
        this.followingWriters = followingWriters;
        this.followers = followers;
    }
}
