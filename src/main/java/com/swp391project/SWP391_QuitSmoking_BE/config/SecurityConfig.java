package com.swp391project.SWP391_QuitSmoking_BE.config;

import com.swp391project.SWP391_QuitSmoking_BE.repository.TokenBlacklistRepository;
import com.swp391project.SWP391_QuitSmoking_BE.service.TokenCleanupService;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.core.annotation.Order;

@Configuration
@EnableWebSecurity // Bật EnableWebSecurity để kích hoạt bảo mật web
@EnableMethodSecurity(prePostEnabled = true) // Bật EnableMethodSecurity để kích hoạt bảo mật phương thức, cho phép sử dụng @PreAuthorize, @PostAuthorize, v.v.
//@RequiredArgsConstructor
public class SecurityConfig {

//    private final JwtAuthenticationFilter jwtAuthenticationFilter; // Đổi tên biến nếu bạn dùng "jwtAuthFilter" trước đó
//    private final AuthenticationService authenticationService; // Inject AuthenticationService
//    private final UserDetailsService userDetailsService;

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public SecurityConfig(@Lazy UserDetailsService userDetailsService, JwtUtil jwtUtil, TokenBlacklistRepository tokenBlacklistRepository) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    // Bean mới cho JwtAuthenticationFilter
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // Spring sẽ inject jwtUtil và userDetailsService vào constructor của JwtAuthenticationFilter
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, tokenBlacklistRepository);
    }

    // Bean để mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // quan ly xac thuc dang nhap, dang ky, ma hoa
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // <-- Đã được chỉ định ở đây
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Cấu hình corsFilter gọi API từ frontend
    @Bean
    @Order(0)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // Cho phép gửi cookies, authorization headers
        config.addAllowedOrigin("http://localhost:5173"); // Hoặc "*" cho mọi origin (ít an toàn hơn trong production)
        config.addAllowedOrigin("http://localhost:3000");
        // Nếu deploy lên VPS, bạn cần thay đổi "http://localhost:3000" thành URL của frontend
        config.addAllowedHeader("*"); // Cho phép tất cả các header
        config.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP (GET, POST, PUT, DELETE...)
        source.registerCorsConfiguration("/**", config); // Áp dụng cấu hình CORS cho tất cả các đường dẫn
        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Tắt CSRF protection, có thể cần tùy theo ứng dụng
                // Nên bật CSRF protection nếu ứng dụng, Với JWT, Stateless session thì có thể tắt CSRF
                .authorizeHttpRequests( // Cấu hình phân quyền truy cập
                    req -> req
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll() // Swagger UI
                        .requestMatchers("/auth/login", "/auth/register").permitAll() // Cho phép truy cập không cần xác thực cho login/register
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/superadmin/**").hasRole("SUPER_ADMIN")
                        .requestMatchers("/coaches/**").authenticated()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider()) // Sử dụng DaoAuthenticationProvider để xác thực người dùng
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).build(); // JWT filter
    }
}
