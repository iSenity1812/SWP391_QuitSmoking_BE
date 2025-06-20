package com.vnpay.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

@Configuration // Đánh dấu đây là một lớp cấu hình Spring
@PropertySource("classpath:application.properties") // Đảm bảo đọc từ application.properties
public class Config {

    // Các thuộc tính VnPay sẽ được tiêm từ application.properties
    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    // RẤT QUAN TRỌNG: Đảm bảo tên placeholder là "vnpay.url"
    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.api-url}")
    private String vnp_apiUrl; // Lưu ý chữ 'A' thường ở đây

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.version}")
    private String vnp_Version;

    @Value("${vnpay.command}")
    private String vnp_Command;

    @Value("${vnpay.order-type}")
    private String vnp_OrderType;

    // Biến static để lưu hashSecret, cần thiết cho phương thức static hashAllFields
    private static String hashSecret;

    // Setter để tiêm giá trị hashSecret vào biến static sau khi bean được tạo
    @Value("${vnpay.hash-secret}")
    public void setHashSecret(String secret) {
        Config.hashSecret = secret;
    }

    // Getters cho các thuộc tính VnPay
    public String getVnp_TmnCode() {
        return vnp_TmnCode;
    }

    public String getVnp_HashSecret() {
        return vnp_HashSecret;
    }

    public String getVnp_Url() {
        return vnp_Url;
    }

    public String getVnp_apiUrl() {
        return vnp_apiUrl;
    }

    public String getVnp_ReturnUrl() {
        return vnp_ReturnUrl;
    }

    public String getVnp_Version() {
        return vnp_Version;
    }

    public String getVnp_Command() {
        return vnp_Command;
    }

    public String getVnp_OrderType() {
        return vnp_OrderType;
    }

    // Phương thức tiện ích để tạo hash (sử dụng biến static hashSecret)
    public static String hashAllFields(Map fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                try {
                    sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        // Đảm bảo hashSecret đã được thiết lập trước khi sử dụng
        if (hashSecret == null) {
            // Đây là một kiểm tra an toàn, nhưng nếu Spring Context khởi tạo đúng, nó sẽ không null
            throw new IllegalStateException("vnpay.hash-secret is not configured properly or Config bean not initialized.");
        }
        return HmacSHA512.hmacSHA512(hashSecret, sb.toString());
    }

    // Các phương thức tiện ích khác (không thay đổi)
    public static String get(String name) {
        String data = System.getenv().get(name);
        return data;
    }

    public static String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
