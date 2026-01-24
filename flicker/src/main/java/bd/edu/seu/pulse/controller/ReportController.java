package bd.edu.seu.pulse.controller;

import bd.edu.seu.pulse.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/create")
    public String createReport(@RequestParam String reporterId,
            @RequestParam String entityId,
            @RequestParam String type,
            @RequestParam String reason,
            @RequestParam String redirectUrl) {
        reportService.createReport(reporterId, entityId, type, reason);
        return "redirect:" + redirectUrl + "?reported=true";
    }
}
