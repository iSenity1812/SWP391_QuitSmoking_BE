package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.config.VnPayConfig;
import com.swp391project.SWP391_QuitSmoking_BE.dto.VnPayRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public String createPaymentUrl(VnPayRequest request, String ipAddr) throws Exception {
        logger.info("Creating VNPay payment URL for amount: {}, orderInfo: {}",
                request.getAmount(), request.getOrderInfo());

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = request.getTransactionId() != null ?
                request.getTransactionId() :
                String.valueOf(System.currentTimeMillis());
        int amount = request.getAmount() * 100;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", request.getOrderInfo());
        vnp_Params.put("vnp_OrderType", request.getOrderType() != null ? request.getOrderType() : "other");
        vnp_Params.put("vnp_Locale", request.getLanguage() != null ? request.getLanguage() : "vn");
        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddr);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(calendar.getTime()));
        calendar.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(calendar.getTime()));

        if (request.getBankCode() != null && !request.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", request.getBankCode());
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString())).append('&');
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()))
                        .append('=')
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()))
                        .append('&');
            }
        }

        // Remove the last '&'
        if (hashData.length() > 0) hashData.setLength(hashData.length() - 1);
        if (query.length() > 0) query.setLength(query.length() - 1);

        String vnp_SecureHash = hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
        String paymentUrl = VnPayConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

        logger.info("Generated VNPay URL: {}", paymentUrl);
        return paymentUrl;
    }

    public boolean verifyVnpaySignature(Map<String, String> vnpParams) {
        logger.info("Verifying VNPay signature for params: {}", vnpParams);

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

        try {
            String calculatedHash = hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
            boolean isValid = vnp_SecureHash.equalsIgnoreCase(calculatedHash);
            logger.info("VNPay signature verification: {}", isValid ? "VALID" : "INVALID");
            return isValid;
        } catch (Exception e) {
            logger.error("Error verifying VNPay signature", e);
            return false;
        }
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKeySpec);
        byte[] hashBytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}