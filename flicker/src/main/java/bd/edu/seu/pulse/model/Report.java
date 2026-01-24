package bd.edu.seu.pulse.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reports")
public class Report {
    @Id
    private String id;
    private String reporterId;
    private String reportedEntityId;
    private String reportedEntityType;
    private String reason;
    private String status = "PENDING";
    private String offenderId;
    private LocalDateTime timestamp;

    public Report() {
        this.timestamp = LocalDateTime.now();
    }

    public Report(String reporterId, String reportedEntityId, String reportedEntityType, String reason,
            String offenderId) {
        this.reporterId = reporterId;
        this.reportedEntityId = reportedEntityId;
        this.reportedEntityType = reportedEntityType;
        this.reason = reason;
        this.offenderId = offenderId;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public String getOffenderId() {
        return offenderId;
    }

    public void setOffenderId(String offenderId) {
        this.offenderId = offenderId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReporterId() {
        return reporterId;
    }

    public void setReporterId(String reporterId) {
        this.reporterId = reporterId;
    }

    public String getReportedEntityId() {
        return reportedEntityId;
    }

    public void setReportedEntityId(String reportedEntityId) {
        this.reportedEntityId = reportedEntityId;
    }

    public String getReportedEntityType() {
        return reportedEntityType;
    }

    public void setReportedEntityType(String reportedEntityType) {
        this.reportedEntityType = reportedEntityType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
