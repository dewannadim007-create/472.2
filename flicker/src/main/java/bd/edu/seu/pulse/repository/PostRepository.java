package bd.edu.seu.pulse.repository;

import bd.edu.seu.pulse.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByAuthorId(String authorId, Sort sort);

    List<Post> findByAuthorIdIn(List<String> authorIds, Sort sort);

    List<Post> findByAuthorIdAndVisibility(String authorId, String visibility, Sort sort);

    List<Post> findByUpvotedByContaining(String userId);

    List<Post> findByDownvotedByContaining(String userId);
}