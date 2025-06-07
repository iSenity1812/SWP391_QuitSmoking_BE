package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TokenBlacklistRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // This method is called once per request
        // You can add your custom logic here if needed

        // 1. Lấy token từ header Authorization
        final String jwt;
        final String userIdentifier;
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        // 2. Xử lý xác thưc JWT
        try {
            // Kiểm tra xem token có hợp lệ không
            // Trichs xuất jti từ token và ktra blacklist
            String jti = jwtUtil.extractJti(jwt);
            if (tokenBlacklistRepository.findByJti(jti).isPresent()) {
                // Nếu token đã bị blacklist, trả về lỗi Unauthorized
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token has been blacklisted.");
                return; // Dừng chuỗi filter và trả về lỗi
            }

            //Trích xuất thông tin người dùng từ token
            userIdentifier = jwtUtil.extractUsername(jwt);

            // Chỉ tiếp tục xác thực nếu đã trích xuất được userIdentifier và
            // người dùng chưa được xác thực trong SecurityContext
            if (userIdentifier != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //3. Lấy thông tin người dùng từ UserDetailsService
                // lấy dữ liệu người dùng mới nhất từ database
                User userDetails = (User) userDetailsService.loadUserByUsername(userIdentifier);
                System.out.println(("Loaded user details for: {}, Role: {}" + userDetails.getUsername() + userDetails.getRole()));
                //4. Xác thực JWT với UserDetails đã tải
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    // Nếu token hợp lệ, thiết lập Authentication trong SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // Thiết lập thêm các chi tiết về yêu cầu web (như IP, session ID) cho Authentication object.
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Đặt đối tượng Authentication vào SecurityContextHolder.
                    // Điều này thông báo cho Spring Security rằng người dùng hiện tại đã được xác thực,
                    // cho phép các cơ chế phân quyền (Authorization) hoạt động dựa trên thông tin này.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (SignatureException e) {
            // Xử lý khi chữ ký JWT không hợp lệ (token bị giả mạo hoặc thay đổi)
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT signature.");
            return; // Dừng chuỗi filter và trả về lỗi
        } catch (MalformedJwtException e) {
            // Xử lý khi JWT có định dạng không đúng
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed JWT.");
            return;
        } catch (ExpiredJwtException e) {
            // Xử lý khi JWT đã hết hạn
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT has expired.");
            return;
        } catch (UsernameNotFoundException e) {
            // Xử lý khi người dùng trong JWT không tìm thấy trong database
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User in token not found.");
            return;
        } catch (Exception e) {
            // Xử lý các ngoại lệ chung khác có thể xảy ra trong quá trình xử lý JWT
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during JWT processing.");
            return;
        }

        // Allow the request to proceed to the next filter or controller
        filterChain.doFilter(request, response);
    }

//    public String getToken(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) return authHeader.substring(7); // Trả về token sau "Bearer "
//        return null; // Trả về null nếu không có token
//    }
}
