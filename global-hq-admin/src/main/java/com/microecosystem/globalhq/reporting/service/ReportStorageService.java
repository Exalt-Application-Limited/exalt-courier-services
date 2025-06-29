package com.microecosystem.globalhq.reporting.service;

import com.microecosystem.globalhq.reporting.model.AdvancedReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for storing and retrieving report data.
 */
@Service
public class ReportStorageService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ReportStorageService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Save a report to the database.
     *
     * @param report The report to save
     * @return The saved report with its generated ID
     */
    public AdvancedReport saveReport(AdvancedReport report) {
        if (report.getId() == null) {
            report.setId(UUID.randomUUID().toString());
        }
        return mongoTemplate.save(report, "reports");
    }

    /**
     * Find a report by its ID.
     *
     * @param id The report ID
     * @return An Optional containing the report if found
     */
    public Optional<AdvancedReport> findReportById(String id) {
        AdvancedReport report = mongoTemplate.findById(id, AdvancedReport.class, "reports");
        return Optional.ofNullable(report);
    }

    /**
     * Find reports by type for a specific date range.
     *
     * @param reportType The report type
     * @param startDate  The start date (inclusive)
     * @param endDate    The end date (inclusive)
     * @param branchId   The branch ID (optional)
     * @return A list of matching reports
     */
    public List<AdvancedReport> findReportsByTypeAndDateRange(String reportType, LocalDate startDate, LocalDate endDate, String branchId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reportType").is(reportType));
        query.addCriteria(Criteria.where("startDate").gte(startDate));
        query.addCriteria(Criteria.where("endDate").lte(endDate));
        
        if (branchId != null && !branchId.isEmpty()) {
            query.addCriteria(Criteria.where("branchId").is(branchId));
        }
        
        return mongoTemplate.find(query, AdvancedReport.class, "reports");
    }

    /**
     * Find reports by date range, sorted by generation date.
     *
     * @param startDate The start date (inclusive)
     * @param endDate   The end date (inclusive)
     * @param branchId  The branch ID (optional)
     * @return A list of matching reports
     */
    public List<AdvancedReport> findReportsByDateRange(LocalDate startDate, LocalDate endDate, String branchId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("startDate").gte(startDate));
        query.addCriteria(Criteria.where("endDate").lte(endDate));
        
        if (branchId != null && !branchId.isEmpty()) {
            query.addCriteria(Criteria.where("branchId").is(branchId));
        }
        
        return mongoTemplate.find(query, AdvancedReport.class, "reports");
    }

    /**
     * Find the most recent reports of each type.
     *
     * @param limit    The maximum number of reports to return
     * @param branchId The branch ID (optional)
     * @return A list of recent reports
     */
    public List<AdvancedReport> findRecentReports(int limit, String branchId) {
        Query query = new Query();
        
        if (branchId != null && !branchId.isEmpty()) {
            query.addCriteria(Criteria.where("branchId").is(branchId));
        }
        
        query.limit(limit);
        query.with(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "generatedDate"));
        
        return mongoTemplate.find(query, AdvancedReport.class, "reports");
    }

    /**
     * Archive a report.
     *
     * @param id The report ID
     * @return true if the report was successfully archived, false otherwise
     */
    public boolean archiveReport(String id) {
        Optional<AdvancedReport> reportOpt = findReportById(id);
        if (reportOpt.isPresent()) {
            AdvancedReport report = reportOpt.get();
            report.setArchived(true);
            mongoTemplate.save(report, "reports");
            return true;
        }
        return false;
    }

    /**
     * Delete a report by its ID.
     *
     * @param id The report ID
     * @return true if the report was successfully deleted, false otherwise
     */
    public boolean deleteReport(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, AdvancedReport.class, "reports").getDeletedCount() > 0;
    }

    /**
     * Delete all reports that are older than the specified number of days.
     *
     * @param days The number of days
     * @return The number of reports deleted
     */
    public long deleteOldReports(int days) {
        LocalDate cutoffDate = LocalDate.now().minusDays(days);
        Query query = new Query(Criteria.where("generatedDate").lt(cutoffDate));
        return mongoTemplate.remove(query, AdvancedReport.class, "reports").getDeletedCount();
    }
}
