package bd.edu.seu.pulse.service;

import bd.edu.seu.pulse.model.Report;
import bd.edu.seu.pulse.repository.CommentRepository;
import bd.edu.seu.pulse.repository.PostRepository;
import bd.edu.seu.pulse.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    public void createReport(String reporterId, String entityId, String type, String reason) {
        String offenderId = null;
        if ("POST".equals(type)) {
            offenderId = postRepository.findById(entityId)
                    .map(post -> post.getAuthorId())
                    .orElse(null);
        } else if ("COMMENT".equals(type)) {
            offenderId = commentRepository.findById(entityId)
                    .map(comment -> comment.getAuthorId())
                    .orElse(null);
        }

        Report report = new Report(reporterId, entityId, type, reason, offenderId);
        reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByTimestampDesc();
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus("PENDING");
    }

    public void updateReportStatus(String reportId, String status) {
        Report report = reportRepository.findById(reportId).orElse(null);
        if (report != null) {
            report.setStatus(status);
            reportRepository.save(report);
        }
    }
}
