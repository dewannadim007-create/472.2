package bd.edu.seu.pulse.repository;

import bd.edu.seu.pulse.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByPostId(String postId, Sort sort);
    List<Comment> findByPostIdIn(List<String> postIds, Sort sort);
} 