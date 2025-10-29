package bd.edu.seu.pulse.service;

import bd.edu.seu.pulse.dto.LoginDTO;
import bd.edu.seu.pulse.dto.ProfileEditDTO;
import bd.edu.seu.pulse.dto.RegistrationDTO;
import bd.edu.seu.pulse.model.User;
import bd.edu.seu.pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public String register(RegistrationDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            return "Username is required";
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return "Password is required";
        }
        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().trim().isEmpty()) {
            return "Confirm password is required";
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return "Passwords do not match";
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return "Name is required";
        }
        if (dto.getRole() == null || (!dto.getRole().equals("READER") && !dto.getRole().equals("WRITER"))) {
            return "Role must be READER or WRITER";
        }
        
        if (userRepository.existsByUsername(dto.getUsername())) {
            return "Username already exists";
        }
        
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        user.setName(dto.getName().trim());
        user.setRole(dto.getRole());
        user.setInterests(dto.getInterests() != null ? dto.getInterests() : new ArrayList<>());
        user.setFollowingWriters(new ArrayList<>());
        user.setFollowers(new ArrayList<>());
        
        userRepository.save(user);
        return "SUCCESS";
    }
    
    public User login(LoginDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            return null;
        }
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            return null;
        }
        
        Optional<User> userOpt = userRepository.findByUsername(dto.getUsername().trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String storedPassword = user.getPassword();
            String inputPassword = dto.getPassword().trim();
            
            if (storedPassword.startsWith("$2a$")) {
                if (passwordEncoder.matches(inputPassword, storedPassword)) {
                    return user;
                }
            } else {
                if (storedPassword.equals(inputPassword)) {
                    user.setPassword(passwordEncoder.encode(inputPassword));
                    userRepository.save(user);
                    return user;
                }
            }
        }
        return null;
    }
    
    public List<User> getWritersByInterest(List<String> interests) {
        if (interests == null || interests.isEmpty()) {
            return userRepository.findByRole("WRITER");
        }
        
        List<User> allWriters = userRepository.findByRole("WRITER");
        List<User> filteredWriters = new ArrayList<>();
        
        for (User writer : allWriters) {
            if (writer.getInterests() != null && !writer.getInterests().isEmpty()) {
                for (String interest : interests) {
                    boolean found = writer.getInterests().stream()
                        .anyMatch(writerInterest -> {
                            if (writerInterest == null) return false;
                            
                            String writerInterestLower = writerInterest.toLowerCase();
                            String interestLower = interest.toLowerCase();
                            
                            if (writerInterestLower.equals(interestLower)) return true;
                            
                            if (writerInterestLower.contains(interestLower)) return true;
                            
                            if (interestLower.contains(writerInterestLower)) return true;
                            
                            return false;
                        });
                    
                    if (found) {
                        filteredWriters.add(writer);
                        break;
                    }
                }
            }
        }
        
        return filteredWriters;
    }
    
    public List<User> getWritersByInterestExcludingFollowing(String readerId, List<String> interests) {
        List<User> allWriters = getWritersByInterest(interests);
        
        User reader = userRepository.findById(readerId).orElse(null);
        if (reader == null || reader.getFollowingWriters() == null) {
            return allWriters;
        }
        
        List<User> filteredWriters = new ArrayList<>();
        for (User writer : allWriters) {
            if (!reader.getFollowingWriters().contains(writer.getId())) {
                filteredWriters.add(writer);
            }
        }
        
        return filteredWriters;
    }
    
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    public boolean followWriter(String readerId, String writerId) {
        User reader = userRepository.findById(readerId).orElse(null);
        User writer = userRepository.findById(writerId).orElse(null);
        
        if (reader == null || writer == null || !reader.getRole().equals("READER") || !writer.getRole().equals("WRITER")) {
            return false;
        }
        
        if (reader.getFollowingWriters() == null) {
            reader.setFollowingWriters(new ArrayList<>());
        }
        
        if (!reader.getFollowingWriters().contains(writerId)) {
            reader.getFollowingWriters().add(writerId);
            userRepository.save(reader);
        }
        
        if (writer.getFollowers() == null) {
            writer.setFollowers(new ArrayList<>());
        }
        
        if (!writer.getFollowers().contains(readerId)) {
            writer.getFollowers().add(readerId);
            userRepository.save(writer);
        }
        
        return true;
    }
    
    public boolean unfollowWriter(String readerId, String writerId) {
        User reader = userRepository.findById(readerId).orElse(null);
        User writer = userRepository.findById(writerId).orElse(null);
        
        if (reader == null || writer == null) {
            return false;
        }
        
        if (reader.getFollowingWriters() != null) {
            reader.getFollowingWriters().remove(writerId);
            userRepository.save(reader);
        }
        
        if (writer.getFollowers() != null) {
            writer.getFollowers().remove(readerId);
            userRepository.save(writer);
        }
        
        return true;
    }
    
    public List<User> getFollowers(String writerId) {
        User writer = userRepository.findById(writerId).orElse(null);
        if (writer == null || writer.getFollowers() == null) {
            return new ArrayList<>();
        }
        
        List<User> followers = new ArrayList<>();
        for (String followerId : writer.getFollowers()) {
            User follower = userRepository.findById(followerId).orElse(null);
            if (follower != null) {
                followers.add(follower);
            }
        }
        return followers;
    }
    
    public String updateProfile(String userId, ProfileEditDTO dto) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return "User not found";
        }
        
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName().trim());
        }
        
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            if (dto.getConfirmPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
                return "Passwords do not match";
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword().trim()));
        }
        
        if (dto.getInterests() != null) {
            user.setInterests(dto.getInterests());
        }
        
        userRepository.save(user);
        return "SUCCESS";
    }
    
    public boolean isFollowing(String readerId, String writerId) {
        User reader = userRepository.findById(readerId).orElse(null);
        if (reader == null || reader.getFollowingWriters() == null) {
            return false;
        }
        return reader.getFollowingWriters().contains(writerId);
    }
    
    public List<User> getFollowingWriters(String readerId) {
        User reader = userRepository.findById(readerId).orElse(null);
        if (reader == null || reader.getFollowingWriters() == null) {
            return new ArrayList<>();
        }
        
        List<User> followingWriters = new ArrayList<>();
        for (String writerId : reader.getFollowingWriters()) {
            User writer = userRepository.findById(writerId).orElse(null);
            if (writer != null) {
                followingWriters.add(writer);
            }
        }
        return followingWriters;
    }
    
    public List<User> findAll() {
        return userRepository.findAll();
    }
} 