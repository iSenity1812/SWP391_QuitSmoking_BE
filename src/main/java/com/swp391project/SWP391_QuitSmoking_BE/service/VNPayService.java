package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.config.VNPayConfig;
import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayPaymentRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayPaymentResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.payment.VNPayTransactionResultDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Subscription;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Transaction;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.enums.TransactionStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.PlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.SubscriptionRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TransactionRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VNPayService {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(VNPayService.class);

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final TransactionRepository transactionRepository;
    private final SubscriptionRepository subscriptionRepository;

    public VNPayService(UserRepository userRepository, PlanRepository planRepository, TransactionRepository transactionRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.transactionRepository = transactionRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public VNPayPaymentResponseDTO createPaymentUrl(VNPayPaymentRequestDTO paymentRequestDTO, HttpServletRequest request) throws UnsupportedEncodingException {
        // 1. Kiểm tra tồn tại User và Plan
        User user = userRepository.findById(paymentRequestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + paymentRequestDTO.getUserId()));

        Plan plan = planRepository.findById(paymentRequestDTO.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + paymentRequestDTO.getPlanId()));

        // 2. Tạo bản ghi Transaction với trạng thái PENDING
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setPlan(plan);
        transaction.setAmount(paymentRequestDTO.getAmount());
        transaction.setPaymentMethod("VNPAY");
        transaction.setStatus(TransactionStatus.PENDING);

        // Lưu tạm thời để có Transaction ID
        transaction = transactionRepository.save(transaction);

        // 3. Chuẩn bị các tham số cho VNPay
        long amount = paymentRequestDTO.getAmount().longValue() * 100;
        String vnp_TxnRef = transaction.getTransactionId().toString(); // Sử dụng Transaction ID của bạn
        String vnp_IpAddr = VNPayConfig.getIpAddress(request);
        String vnp_Command = "pay";
        String vnp_CurrCode = "VND";
        String vnp_Locale = "vn";
        String vnp_OrderInfo = URLEncoder.encode(paymentRequestDTO.getOrderInfo() + " - OrderRef: " + vnp_TxnRef, StandardCharsets.UTF_8.toString());
        String vnp_OrderType = paymentRequestDTO.getOrderType();
        String vnp_Version = "2.1.0";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_Returnurl); // URL để VNPay redirect về
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        if (paymentRequestDTO.getBankCode() != null && !paymentRequestDTO.getBankCode().isEmpty()) {
            vnp_Params.put("vnp_BankCode", paymentRequestDTO.getBankCode());
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15); // Thời gian hết hạn giao dịch là 15 phút
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sắp xếp các tham số và tạo chuỗi hash
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                if (itr.hasNext()) {
                    query.append("&");
                    hashData.append("&");
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        // Cập nhật externalTransactionId và paymentGatewayResponse cho transaction PENDING
        try {
            transaction.setExternalTransactionId(vnp_TxnRef); // VNPay TxnRef là ID duy nhất
            transaction.setPaymentGatewayResponse(new HashMap<>(vnp_Params)); // Lưu các tham số ban đầu
            transactionRepository.save(transaction);
        } catch (Exception e) {
            log.error("Error saving initial transaction details: {}", e.getMessage());
            // Xử lý rollback hoặc thông báo lỗi nếu không lưu được
        }
        return new VNPayPaymentResponseDTO("00", "Success", paymentUrl);
    }

    // Endpoint xử lý kết quả từ VNPay (Return URL)
    @Transactional
    public VNPayTransactionResultDTO processPaymentReturn(HttpServletRequest request) {
        Map<String, String> vnp_Params = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                vnp_Params.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        if (vnp_Params.containsKey("vnp_SecureHashType")) {
            vnp_Params.remove("vnp_SecureHashType");
        }
        if (vnp_Params.containsKey("vnp_SecureHash")) {
            vnp_Params.remove("vnp_SecureHash");
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();

        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }

        String checkHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());

        VNPayTransactionResultDTO result = new VNPayTransactionResultDTO();

        if (checkHash != null && checkHash.equals(vnp_SecureHash)) {
            String RspCode = vnp_Params.get("vnp_ResponseCode");
            String TxnRef = vnp_Params.get("vnp_TxnRef");
            String Amount = vnp_Params.get("vnp_Amount"); // Amount in cents
            String OrderInfo = vnp_Params.get("vnp_OrderInfo");
            String PayDate = vnp_Params.get("vnp_PayDate");

            result.setVnp_ResponseCode(RspCode);
            result.setVnp_TxnRef(TxnRef);
            result.setVnp_Amount(new BigDecimal(Amount).divide(new BigDecimal(100))); // Convert to VND
            result.setVnp_OrderInfo(OrderInfo);
            result.setVnp_PayDate(PayDate);

            // Tìm transaction theo externalTransactionId (vnp_TxnRef)
            Optional<Transaction> optionalTransaction = transactionRepository.findByExternalTransactionId(TxnRef);

            if (optionalTransaction.isPresent()) {
                Transaction transaction = optionalTransaction.get();

                if ("00".equals(RspCode)) {
                    // Giao dịch thành công
                    result.setMessage("Giao dịch thành công");
                    result.setVnp_TransactionStatus("SUCCESS");
                    // Cập nhật trạng thái giao dịch
                    if (transaction.getStatus() != TransactionStatus.SUCCESS) { // Tránh cập nhật lại nếu đã thành công
                        transaction.setStatus(TransactionStatus.SUCCESS);
                        // Cập nhật paymentGatewayResponse
                        try {
                            transaction.setPaymentGatewayResponse(new HashMap<>(vnp_Params)); // Lưu response cuối cùng
                        } catch (Exception e) {
                            log.error("Error converting Map to JSON for paymentGatewayResponse: " + e.getMessage());
                        }
                        transactionRepository.save(transaction);

                        // Cập nhật Subscription và User Role
                        updateSubscriptionAndUserRole(transaction);
                    } else {
                        log.info("Transaction already marked as SUCCESS: " + TxnRef);
                    }
                } else  {
                    // Giao dịch thất bại hoặc lỗi
                    result.setMessage("Giao dịch thất bại. Mã lỗi: " + RspCode);
                    result.setVnp_TransactionStatus("FAILED");

                    if (transaction.getStatus() == TransactionStatus.PENDING) { // Chỉ cập nhật nếu đang PENDING
                        transaction.setStatus(TransactionStatus.FAILED);
                        try {
                            transaction.setPaymentGatewayResponse(new HashMap<>(vnp_Params));
                        } catch (Exception e) {
                            log.error("Error converting Map to JSON for paymentGatewayResponse: " + e.getMessage());
                        }
                        transactionRepository.save(transaction);
                    }
                    log.warn("VNPAY Return: Transaction failed for TxnRef: " + TxnRef + ", ResponseCode: " + RspCode);
                }
            } else {
                result.setVnp_ResponseCode("99");
                result.setMessage("Không tìm thấy giao dịch với TxnRef: " + TxnRef);
                result.setVnp_TransactionStatus("FAILED");
                log.error("VNPAY Return: Transaction with TxnRef " + TxnRef + " not found.");
            }
        } else {
            result.setVnp_ResponseCode("97");
            result.setMessage("Invalid signature");
            result.setVnp_TransactionStatus("FAILED");
            log.error("VNPAY Return: Invalid signature for TxnRef: " + vnp_Params.get("vnp_TxnRef"));
        }
        return result;
    }


    // Endpoint xử lý IPN từ VNPay (Server-to-server callback)
    @Transactional
    public String processIPN(HttpServletRequest request) {
        Map<String, String> vnp_Params = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                vnp_Params.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = vnp_Params.get("vnp_SecureHash");
        if (vnp_Params.containsKey("vnp_SecureHashType")) {
            vnp_Params.remove("vnp_SecureHashType");
        }
        if (vnp_Params.containsKey("vnp_SecureHash")) {
            vnp_Params.remove("vnp_SecureHash");
        }

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                if (itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }

        String checkHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());

        String RspCode = vnp_Params.get("vnp_ResponseCode");
        String TxnRef = vnp_Params.get("vnp_TxnRef"); // Mã giao dịch của bạn

        // Logic xử lý IPN
        if (checkHash != null && checkHash.equals(vnp_SecureHash)) {
            Optional<Transaction> optionalTransaction = transactionRepository.findByExternalTransactionId(TxnRef);

            if (optionalTransaction.isPresent()) {
                Transaction transaction = optionalTransaction.get();

                // Kiểm tra trạng thái hiện tại của giao dịch
                // Nếu đã thành công qua Return URL hoặc IPN khác, bỏ qua để tránh trùng lặp
                if (transaction.getStatus() == TransactionStatus.SUCCESS) {
                    log.info("IPN: Transaction {} already processed as SUCCESS. Skipping.", TxnRef);
                    return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                }

                if ("00".equals(RspCode)) {
                    // Giao dịch thành công
                    // Kiểm tra số tiền có khớp không (để phòng trường hợp gian lận)
                    long vnp_Amount = Long.parseLong(vnp_Params.get("vnp_Amount")) / 100;
                    if (transaction.getAmount().longValue() != vnp_Amount) {
                        log.warn("IPN: Amount mismatch for TxnRef {}", TxnRef);
                        return "{\"RspCode\":\"04\",\"Message\":\"Amount invalid\"}"; // Số tiền không khớp
                    }

                    transaction.setStatus(TransactionStatus.SUCCESS);
                    try {
                        transaction.setPaymentGatewayResponse(new HashMap<>(vnp_Params));
                    } catch (Exception e) {
                        log.error("Error converting Map to JSON for paymentGatewayResponse in IPN: {}", e.getMessage());
                    }
                    transactionRepository.save(transaction);

                    // Cập nhật Subscription và User Role
                    updateSubscriptionAndUserRole(transaction);

                    log.info("IPN: Transaction {} processed successfully.", TxnRef);
                    return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}";
                } else {
                    // Giao dịch thất bại (hoặc các mã lỗi khác từ VNPay)
                    transaction.setStatus(TransactionStatus.FAILED);
                    try {
                        transaction.setPaymentGatewayResponse(new HashMap<>(vnp_Params));
                    } catch (Exception e) {
                        log.warn("Error converting Map to JSON for paymentGatewayResponse in IPN: {}", e.getMessage());
                    }
                    transactionRepository.save(transaction);
                    log.warn("IPN: Transaction {} failed with ResponseCode: {}", TxnRef, RspCode);
                    return "{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}"; // Vẫn trả về 00 để VNPay dừng gửi IPN
                }
            } else {
                log.error("IPN: Transaction with TxnRef {} not found in database.", TxnRef);
                return "{\"RspCode\":\"01\",\"Message\":\"Order not found\"}"; // Không tìm thấy đơn hàng
            }
        } else {
            log.error("IPN: Invalid signature for transaction: {}", TxnRef);
            return "{\"RspCode\":\"97\",\"Message\":\"Invalid Checksum\"}"; // Chữ ký không hợp lệ
        }
    }


    // Logic cập nhật Subscription và User Role
    private void updateSubscriptionAndUserRole(Transaction transaction) {
        User user = transaction.getUser();
        Plan plan = transaction.getPlan();
        LocalDateTime now = LocalDateTime.now();

        List<Subscription> existingSubscriptions = subscriptionRepository.findByUser(user);

        // Vô hiệu hóa tất cả các subscription cũ (đang hoạt động hoặc đã kết thúc)
        // để chuẩn bị cho subscription mới.
        // Logic "người dùng không thể hủy plan trong thời gian" được đảm bảo bởi
        // việc không có API cho phép người dùng tự động đặt isActive về false.
        // Việc này chỉ xảy ra khi có một gói mới được mua, hoặc gói cũ đã hết hạn.
        existingSubscriptions.forEach(sub -> {
            // Chỉ vô hiệu hóa nếu subscription đang hoạt động HOẶC đã hết hạn nhưng vẫn đánh dấu active
            if (sub.isActive() || sub.getEndDate().isBefore(now)) {
                sub.setActive(false);
                sub.setUpdatedAt(now);
                subscriptionRepository.save(sub);
                log.info("Deactivated old subscription ID {} for user: {}", sub.getSubscriptionId(), user.getUserId());
            }
        });

        // 2. Tạo một Subscription mới
        Subscription newSubscription = new Subscription();
        newSubscription.setUser(user);
        newSubscription.setPlan(plan);

        // Xác định ngày bắt đầu của gói mới
        // Nếu có gói cũ và nó chưa hết hạn, gói mới sẽ bắt đầu ngay sau khi gói cũ kết thúc.
        // Nếu không có gói cũ, hoặc gói cũ đã hết hạn, gói mới sẽ bắt đầu từ bây giờ.

        LocalDateTime newStartDate = now;
        Optional<Subscription> latestExistingSubscription = existingSubscriptions.stream()
                .filter(sub -> sub.getEndDate() != null) // Chỉ xét các sub có end date
                .max(Comparator.comparing(Subscription::getEndDate)); // Tìm sub có EndDate xa nhất

        if (latestExistingSubscription.isPresent()) {
            Subscription lastSub = latestExistingSubscription.get();
            // Nếu gói cũ chưa hết hạn, gói mới sẽ bắt đầu từ ngày kết thúc của gói cũ
            if (lastSub.getEndDate().isAfter(now)) {
                newStartDate = lastSub.getEndDate();
                log.info("New subscription for user {} will start after current active plan ends: {}", user.getUserId(), newStartDate);
            }
        }

        newSubscription.setStartDate(newStartDate);
        // Tính toán EndDate dựa trên DurationValue của Plan
        newSubscription.setEndDate(newStartDate.plusDays(plan.getDurationValue()));
        newSubscription.setActive(true);
        newSubscription.setTransaction(transaction); // Gán transaction vào subscription
        subscriptionRepository.save(newSubscription);
        log.info("Created new subscription ID {} for user: {} with plan: {}", newSubscription.getSubscriptionId(), user.getUserId(), plan.getPlanName());

        // Gán subscription vào transaction (mối quan hệ 1-1)
        transaction.setSubscription(newSubscription);
        transactionRepository.save(transaction);

        log.info("Updated transaction ID {} with new subscription ID {}", transaction.getTransactionId(), newSubscription.getSubscriptionId());

        // 3. Cập nhật Role của User thành PREMIUM_MEMBER (nếu chưa phải)
        if (user.getRole() != Role.PREMIUM_MEMBER) {
            user.setRole(Role.PREMIUM_MEMBER);
            user.setUpdatedAt(now);
            userRepository.save(user);
            log.info("Updated user {} role to PREMIUM_MEMBER.", user.getUserId());
        }
    }

//    public String createOrder(int total, String orderInfor, String urlReturn){
//        String vnp_Version = "2.1.0";
//        String vnp_Command = "pay";
//        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
//        String vnp_IpAddr = "127.0.0.1";
//        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;
//        String orderType = "order-type";
//
//        Map<String, String> vnp_Params = new HashMap<>();
//        vnp_Params.put("vnp_Version", vnp_Version);
//        vnp_Params.put("vnp_Command", vnp_Command);
//        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
//        vnp_Params.put("vnp_Amount", String.valueOf(total*100));
//        vnp_Params.put("vnp_CurrCode", "VND");
//
//        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
//        vnp_Params.put("vnp_OrderInfo", orderInfor);
//        vnp_Params.put("vnp_OrderType", orderType);
//
//        String locate = "vn";
//        vnp_Params.put("vnp_Locale", locate);
//
//        urlReturn += VNPayConfig.vnp_Returnurl;
//        vnp_Params.put("vnp_ReturnUrl", urlReturn);
//        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
//
//        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//        String vnp_CreateDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
//
//        cld.add(Calendar.MINUTE, 15);
//        String vnp_ExpireDate = formatter.format(cld.getTime());
//        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
//
//        List fieldNames = new ArrayList(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//        Iterator itr = fieldNames.iterator();
//        while (itr.hasNext()) {
//            String fieldName = (String) itr.next();
//            String fieldValue = (String) vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                //Build hash data
//                hashData.append(fieldName);
//                hashData.append('=');
//                try {
//                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
//                    //Build query
//                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
//                    query.append('=');
//                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                if (itr.hasNext()) {
//                    query.append('&');
//                    hashData.append('&');
//                }
//            }
//        }
//        String queryUrl = query.toString();
//        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData.toString());
//        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
//        return paymentUrl;
//    }

    public int orderReturn(HttpServletRequest request){
        Map fields = new HashMap();
        for (Enumeration params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = null;
            String fieldValue = null;
            try {
                fieldName = URLEncoder.encode((String) params.nextElement(), StandardCharsets.US_ASCII.toString());
                fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }
        String signValue = VNPayConfig.hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

}