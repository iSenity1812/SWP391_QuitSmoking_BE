package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberSearchDTO;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.MemberService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class MemberController {
    private final MemberService memberService;
    //Authenticated: Người dùng đã xác thực có thể xem profile của chính họ (userId == memberId).
    //SUPER_ADMIN: Admin có thể xem thông tin của bất kỳ thành viên nào.
    //Lấy thông tin chi tiết của một thành viên bằng MemberID
    @GetMapping("/{memberId}")
    @PreAuthorize("isAuthenticated() and (authentication.principal.userId == #memberId or hasRole('SUPER_ADMIN'))")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable UUID memberId) {
        MemberResponse member = memberService.getMemberResponseById(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(member, "Lấy thông tin thành viên thành công"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('CONTENT_ADMIN', 'COACH')")
    public ResponseEntity<ApiResponse<List<MemberSearchDTO>>> searchMembers(@RequestParam String query) {
        List<MemberSearchDTO> members = memberService.searchMembers(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(members, "Lấy danh sách thành viên thành công"));
    }
}
