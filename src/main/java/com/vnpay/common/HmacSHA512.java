package com.vnpay.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class HmacSHA512 {
    private static final Logger log = LoggerFactory.getLogger(HmacSHA512.class);

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                // Xử lý trường hợp key hoặc data là null một cách tường minh hơn.
                // Điều này có thể xảy ra nếu cấu hình hashSecret bị lỗi.
                log.warn("Warning: HMAC-SHA512 key or data is null.");
                return null;
            }
            Mac hmacSha512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSha512.init(secretKey);
            byte[] hash = hmacSha512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return toHexString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            log.error("Error calculating HMAC-SHA512: {}", e.getMessage());
            // Trả về null hoặc một giá trị an toàn khi có lỗi
            return null;
        }
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }
}
