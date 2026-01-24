package bd.edu.seu.pulse.repository;

import bd.edu.seu.pulse.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByStatus(String status);

    List<Report> findAllByOrderByTimestampDesc();
}
