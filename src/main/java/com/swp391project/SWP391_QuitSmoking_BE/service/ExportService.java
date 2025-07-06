package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.DashboardFilterDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.request.ExportRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response.ExportResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.revenue.response.TransactionDetailDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling data export functionality
 * Supports CSV, Excel, and PDF export formats
 */
@Service
@RequiredArgsConstructor
public class ExportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    private final TransactionRepository transactionRepository;
    private final DashboardService dashboardService;
    
    /**
     * Export transactions to specified format
     */
    public ResponseEntity<Resource> exportTransactions(ExportRequestDTO request) throws IOException {
        logger.info("Starting transaction export: format={}, period={}", request.getFormat(), request.getPeriod());
        
        try {
            // Get transaction data
            List<TransactionDetailDTO> transactions = getTransactionDataForExport(request);
            
            // Generate file based on format
            switch (request.getFormat().toUpperCase()) {
                case "CSV":
                    return exportTransactionsToCSV(transactions, request);
                case "EXCEL":
                    return exportTransactionsToExcel(transactions, request);
                default:
                    throw new IllegalArgumentException("Unsupported export format: " + request.getFormat());
            }
        } catch (Exception e) {
            logger.error("Error during transaction export: {}", e.getMessage(), e);
            throw new IOException("Failed to export transactions: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get transaction data for export based on filters
     */
    private List<TransactionDetailDTO> getTransactionDataForExport(ExportRequestDTO request) {
        // Convert request to dashboard filter
        DashboardFilterDTO filter = DashboardFilterDTO.builder()
                .period(request.getPeriod())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .planIds(request.getPlanIds())
                .userIds(request.getUserIds())
                .paymentMethod(request.getPaymentMethod())
                .page(0)
                .size(request.getMaxRecords() != null ? request.getMaxRecords() : 10000) // Max 10k records
                .sortBy(request.getSortBy() != null ? request.getSortBy() : "transactionDate")
                .sortDirection(request.getSortDirection() != null ? request.getSortDirection() : "DESC")
                .build();
        
        // Convert status strings to enums if provided
        if (request.getStatuses() != null && !request.getStatuses().isEmpty()) {
            List<TransactionStatus> statusEnums = request.getStatuses().stream()
                    .map(status -> TransactionStatus.valueOf(status.toUpperCase()))
                    .toList();
            filter.setStatuses(statusEnums);
        }
        
        // Get paginated transaction data
        Page<TransactionDetailDTO> transactionPage = dashboardService.getTransactionDetails(filter);
        List<TransactionDetailDTO> transactions = transactionPage.getContent();
        
        logger.info("Retrieved {} transactions for export", transactions.size());
        return transactions;
    }
    
    /**
     * Export transactions to CSV format
     */
    private ResponseEntity<Resource> exportTransactionsToCSV(List<TransactionDetailDTO> transactions, ExportRequestDTO request) throws IOException {
        StringBuilder csvBuilder = new StringBuilder();
        
        // CSV Header
        csvBuilder.append("Transaction ID,External ID,User ID,Username,Email,User Role,")
                  .append("Plan ID,Plan Name,Plan Display Name,Plan Price,")
                  .append("Amount,Payment Method,Status,Transaction Date,")
                  .append("Subscription ID,Subscription Start,Subscription End,Is Active,")
                  .append("Created At,Updated At\n");
        
        // CSV Data
        for (TransactionDetailDTO transaction : transactions) {
            csvBuilder.append(escapeCsvField(String.valueOf(transaction.getTransactionId()))).append(",")
                     .append(escapeCsvField(transaction.getExternalTransactionId())).append(",")
                     .append(escapeCsvField(transaction.getUserId().toString())).append(",")
                     .append(escapeCsvField(transaction.getUsername())).append(",")
                     .append(escapeCsvField(transaction.getUserEmail())).append(",")
                     .append(escapeCsvField(transaction.getUserRole().name())).append(",")
                     .append(transaction.getPlanId()).append(",")
                     .append(escapeCsvField(transaction.getPlanName())).append(",")
                     .append(escapeCsvField(transaction.getPlanDisplayName())).append(",")
                     .append(transaction.getPlanPrice()).append(",")
                     .append(transaction.getAmount()).append(",")
                     .append(escapeCsvField(transaction.getPaymentMethod())).append(",")
                     .append(escapeCsvField(transaction.getStatus().name())).append(",")
                     .append(escapeCsvField(transaction.getTransactionDate().format(DATE_FORMATTER))).append(",")
                     .append(transaction.getSubscriptionId() != null ? transaction.getSubscriptionId() : "").append(",")
                     .append(transaction.getSubscriptionStartDate() != null ? transaction.getSubscriptionStartDate().format(DATE_FORMATTER) : "").append(",")
                     .append(transaction.getSubscriptionEndDate() != null ? transaction.getSubscriptionEndDate().format(DATE_FORMATTER) : "").append(",")
                     .append(transaction.getIsSubscriptionActive() != null ? transaction.getIsSubscriptionActive() : "").append(",")
                     .append(escapeCsvField(transaction.getCreatedAt().format(DATE_FORMATTER))).append(",")
                     .append(escapeCsvField(transaction.getUpdatedAt().format(DATE_FORMATTER))).append("\n");
        }
        
        // Generate filename
        String fileName = String.format("transactions_export_%s.csv", 
                LocalDateTime.now().format(FILE_DATE_FORMATTER));
        
        // Prepare response
        byte[] csvBytes = csvBuilder.toString().getBytes("UTF-8");
        ByteArrayResource resource = new ByteArrayResource(csvBytes);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }
    
    /**
     * Export transactions to Excel format
     */
    private ResponseEntity<Resource> exportTransactionsToExcel(List<TransactionDetailDTO> transactions, ExportRequestDTO request) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Transactions");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            
            // Create currency style
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.cloneStyleFrom(dataStyle);
            currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
            
            // Create date style
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.cloneStyleFrom(dataStyle);
            dateStyle.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss"));
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "Transaction ID", "External ID", "User ID", "Username", "Email", "User Role",
                "Plan ID", "Plan Name", "Plan Display Name", "Plan Price (VND)",
                "Amount (VND)", "Payment Method", "Status", "Transaction Date",
                "Subscription ID", "Subscription Start", "Subscription End", "Is Active",
                "Created At", "Updated At"
            };
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Populate data rows
            int rowNum = 1;
            for (TransactionDetailDTO transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                
                // Transaction basic info
                createCell(row, 0, String.valueOf(transaction.getTransactionId()), dataStyle);
                createCell(row, 1, transaction.getExternalTransactionId(), dataStyle);
                createCell(row, 2, transaction.getUserId().toString(), dataStyle);
                createCell(row, 3, transaction.getUsername(), dataStyle);
                createCell(row, 4, transaction.getUserEmail(), dataStyle);
                createCell(row, 5, transaction.getUserRole().name(), dataStyle);
                
                // Plan info
                createCell(row, 6, transaction.getPlanId().toString(), dataStyle);
                createCell(row, 7, transaction.getPlanName(), dataStyle);
                createCell(row, 8, transaction.getPlanDisplayName(), dataStyle);
                createCurrencyCell(row, 9, transaction.getPlanPrice().doubleValue(), currencyStyle);
                
                // Transaction details
                createCurrencyCell(row, 10, transaction.getAmount().doubleValue(), currencyStyle);
                createCell(row, 11, transaction.getPaymentMethod(), dataStyle);
                createCell(row, 12, transaction.getStatus().name(), dataStyle);
                createDateCell(row, 13, transaction.getTransactionDate(), dateStyle);
                
                // Subscription info
                createCell(row, 14, transaction.getSubscriptionId() != null ? transaction.getSubscriptionId().toString() : "", dataStyle);
                createDateCell(row, 15, transaction.getSubscriptionStartDate(), dateStyle);
                createDateCell(row, 16, transaction.getSubscriptionEndDate(), dateStyle);
                createCell(row, 17, transaction.getIsSubscriptionActive() != null ? transaction.getIsSubscriptionActive().toString() : "", dataStyle);
                
                // Timestamps
                createDateCell(row, 18, transaction.getCreatedAt(), dateStyle);
                createDateCell(row, 19, transaction.getUpdatedAt(), dateStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Set minimum width
                if (sheet.getColumnWidth(i) < 2000) {
                    sheet.setColumnWidth(i, 2000);
                }
                // Set maximum width
                if (sheet.getColumnWidth(i) > 8000) {
                    sheet.setColumnWidth(i, 8000);
                }
            }
            
            // Write to output stream
            workbook.write(outputStream);
            
            // Generate filename
            String fileName = String.format("transactions_export_%s.xlsx", 
                    LocalDateTime.now().format(FILE_DATE_FORMATTER));
            
            // Prepare response
            byte[] excelBytes = outputStream.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(excelBytes);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .contentLength(excelBytes.length)
                    .body(resource);
        }
    }
    
    // Helper methods for Excel cell creation
    private void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
    
    private void createCurrencyCell(Row row, int column, double value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
    
    private void createDateCell(Row row, int column, LocalDateTime date, CellStyle style) {
        Cell cell = row.createCell(column);
        if (date != null) {
            cell.setCellValue(java.sql.Timestamp.valueOf(date));
        } else {
            cell.setCellValue("");
        }
        cell.setCellStyle(style);
    }
    
    // Helper method for CSV field escaping
    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        
        // Escape double quotes and wrap in quotes if necessary
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        
        return field;
    }
    
    /**
     * Generate export metadata
     */
    public ExportResponseDTO generateExportResponse(String format, int recordCount) {
        String exportId = UUID.randomUUID().toString();
        String fileName = String.format("transactions_export_%s.%s", 
                LocalDateTime.now().format(FILE_DATE_FORMATTER),
                format.toLowerCase());
        
        return ExportResponseDTO.builder()
                .exportId(exportId)
                .fileName(fileName)
                .recordCount((long) recordCount)
                .status("COMPLETED")
                .completedAt(LocalDateTime.now().format(DATE_FORMATTER))
                .build();
    }
}
