package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.config.VnPayConfig;
import com.swp391project.SWP391_QuitSmoking_BE.dto.VnPayRequest;
import com.swp391project.SWP391_QuitSmoking_BE.exception.VnPayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class VnPayService {
    private static final Logger logger = LoggerFactory.getLogger(VnPayService.class);

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private VnPaySecurityService securityService;

    @Retryable(value = {Exception.class}, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public String createPaymentUrl(VnPayRequest request, String ipAddr) {
        try {
            logger.info("Creating VNPay payment URL for amount: {}, orderInfo: {}",
                    request.getAmount(), request.getOrderInfo());

            // Validate input
            validatePaymentRequest(request);

            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = request.getTransactionId() != null ?
                    request.getTransactionId() :
                    String.valueOf(System.currentTimeMillis());

            // Convert to VND cents (multiply by 100)
            long amount = (long) request.getAmount() * 100;

            // Validate amount limits
            if (amount < vnPayConfig.getMinAmount() * 100 || amount > vnPayConfig.getMaxAmount() * 100) {
                throw new VnPayException("Amount out of allowed range", "INVALID_AMOUNT");
            }

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnp_Params.put("vnp_Amount", String.valueOf(amount));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", sanitizeOrderInfo(request.getOrderInfo()));
            vnp_Params.put("vnp_OrderType", request.getOrderType() != null ? request.getOrderType() : "billpayment");
            vnp_Params.put("vnp_Locale", request.getLanguage() != null ? request.getLanguage() : "vn");
            vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnp_Params.put("vnp_IpAddr", ipAddr);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            vnp_Params.put("vnp_CreateDate", formatter.format(calendar.getTime()));

            // Add timeout
            calendar.add(Calendar.MINUTE, vnPayConfig.getTimeoutMinutes());
            vnp_Params.put("vnp_ExpireDate", formatter.format(calendar.getTime()));

            if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
                vnp_Params.put("vnp_BankCode", request.getBankCode());
            }

            // Build query string and hash data
            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String fieldName : fieldNames) {
                String fieldValue = vnp_Params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()))
                            .append('&');
                }
            }

            // Remove the last '&'
            if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
            if (query.length() > 0) query.setLength(query.length() - 1);

            String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            String paymentUrl = vnPayConfig.getPayUrl() + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

            logger.info("Generated VNPay URL successfully for transaction: {}", vnp_TxnRef);
            return paymentUrl;

        } catch (VnPayException e) {
            logger.error("VNPay specific error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error creating VNPay payment URL", e);
            throw new VnPayException("Failed to create payment URL", "PAYMENT_URL_ERROR", e);
        }
    }

    public boolean verifyVnpaySignature(Map<String, String> vnpParams) {
        try {
            logger.debug("Verifying VNPay signature for params: {}", vnpParams.keySet());

            String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                logger.error("Missing vnp_SecureHash in callback parameters");
                return false;
            }

            Map<String, String> sortedParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
                String key = entry.getKey();
                if (!key.equals("vnp_SecureHash") && !key.equals("vnp_SecureHashType")) {
                    sortedParams.put(key, entry.getValue());
                }
            }

            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(entry.getKey()).append('=').append(entry.getValue());
            }

            String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
            boolean isValid = vnp_SecureHash.equalsIgnoreCase(calculatedHash);

            logger.info("VNPay signature verification: {}", isValid ? "VALID" : "INVALID");
            return isValid;

        } catch (Exception e) {
            logger.error("Error verifying VNPay signature", e);
            return false;
        }
    }

    private void validatePaymentRequest(VnPayRequest request) {
        if (request == null) {
            throw new VnPayException("Payment request cannot be null", "INVALID_REQUEST");
        }
        if (request.getAmount() <= 0) {
            throw new VnPayException("Amount must be greater than 0", "INVALID_AMOUNT");
        }
        if (request.getOrderInfo() == null || request.getOrderInfo().trim().isEmpty()) {
            throw new VnPayException("Order info cannot be empty", "INVALID_ORDER_INFO");
        }
        if (request.getTransactionId() == null || request.getTransactionId().trim().isEmpty()) {
            throw new VnPayException("Transaction ID cannot be empty", "INVALID_TRANSACTION_ID");
        }
    }

    private String sanitizeOrderInfo(String orderInfo) {
        if (orderInfo == null) return "";

        // Remove special characters that might cause issues
        return orderInfo.replaceAll("[^a-zA-Z0-9\\s\\-_.,]", "")
                .trim()
                .substring(0, Math.min(orderInfo.length(), 255));
    }

    private String hmacSHA512(String key, String data) throws Exception {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKeySpec);
            byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("Error generating HMAC-SHA512", e);
            throw new VnPayException("Failed to generate secure hash", "HASH_ERROR", e);
        }
    }
}
