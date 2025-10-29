package bd.edu.seu.pulse.repository;

import bd.edu.seu.pulse.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    List<User> findByRole(String role);
    List<User> findByRoleAndInterestsIn(String role, List<String> interests);
    boolean existsByUsername(String username);
} 