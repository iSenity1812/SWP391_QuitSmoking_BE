package com.vnpay.common;

import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class vnpayRefund extends HttpServlet {

    private Config config; // Khai báo biến để lưu instance của Config

    // Override phương thức init để lấy Config bean từ Spring Context
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        // Lấy Spring's ApplicationContext từ ServletContext
        WebApplicationContext springContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext());
        // Lấy Config bean từ Spring Context
        this.config = springContext.getBean(Config.class);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Command: refund
        String vnp_RequestId = Config.getRandomNumber(8);
        String vnp_Version = "2.1.0";
        String vnp_Command = "refund";
        String vnp_TmnCode = config.getVnp_TmnCode(); // SỬA ĐỔI: Sử dụng instance 'config'
        String vnp_TransactionType = req.getParameter("trantype");
        String vnp_TxnRef = req.getParameter("order_id");
        long amount = Integer.parseInt(req.getParameter("amount")) * 100;
        String vnp_TransactionNo = req.getParameter("trans_no");
        String vnp_TransactionDate = req.getParameter("trans_date");
        String vnp_CreateBy = req.getParameter("user");
        String vnp_IpAddr = req.getRemoteAddr();
        String vnp_OrderInfo = "Hoan tien GD OrderId:" + vnp_TxnRef;

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        JsonObject vnp_Params = new JsonObject();
        vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
        vnp_Params.addProperty("vnp_Version", vnp_Version);
        vnp_Params.addProperty("vnp_Command", vnp_Command);
        vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.addProperty("vnp_TransactionType", vnp_TransactionType);
        vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.addProperty("vnp_Amount", String.valueOf(amount));
        vnp_Params.addProperty("vnp_TransactionNo", vnp_TransactionNo);
        vnp_Params.addProperty("vnp_TransactionDate", vnp_TransactionDate);
        vnp_Params.addProperty("vnp_CreateBy", vnp_CreateBy);
        vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);

        String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode,
                vnp_TransactionType, vnp_TxnRef, String.valueOf(amount), vnp_TransactionNo, vnp_TransactionDate,
                vnp_CreateBy, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

        // SỬA ĐỔI: Gọi HmacSHA512.hmacSHA512 trực tiếp và sử dụng config.getVnp_HashSecret()
        String vnp_SecureHash = HmacSHA512.hmacSHA512(config.getVnp_HashSecret(), hash_Data.toString());

        vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

        URL url = new URL(config.getVnp_apiUrl()); // SỬA ĐỔI: Sử dụng instance 'config'
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(vnp_Params.toString());
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post Data : " + vnp_Params);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String output;
        StringBuffer response = new StringBuffer();
        while ((output = in.readLine()) != null) {
            response.append(output);
        }
        in.close();
        System.out.println(response.toString());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(response.toString());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }
}
