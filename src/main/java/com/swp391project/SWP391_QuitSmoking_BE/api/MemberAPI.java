package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member; // Giữ nếu có chỗ cần Entity, hoặc xóa nếu chỉ dùng DTO
import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse; // Import MemberResponse DTO
import com.swp391project.SWP391_QuitSmoking_BE.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
public class MemberAPI {
    @Autowired
    private MemberService memberService;

    @GetMapping
    public List<Member> getAllMembers() { // Sửa kiểu trả về nếu MemberService trả về List<MemberResponse>
        // memberService.getAllMembers() không tồn tại, bạn có getAllMembersWithUserDetails()
        return memberService.getAllMembersWithUserDetails(); // Sửa: gọi đúng phương thức
    }

    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable UUID id) {
        // memberService.getMemberById(id) trả về Member entity, không phải Optional<Member>
        // Sử dụng phương thức trả về DTO
        MemberResponse memberResponse = memberService.getMemberResponseById(id);
        return ResponseEntity.ok(memberResponse); // Sửa: trả về MemberResponse DTO
    }

    // PHƯƠNG THỨC POST /api/members ĐÃ ĐƯỢC BỎ BÌNH LUẬN HOẶC XÓA Ở ĐÂY
    // Vì logic `createMemberForUser` trong MemberService cho thấy Member được tạo ra cùng với User.
    // Nếu bạn cần API để Admin tạo Member thủ công, hãy tạo một DTO riêng (ví dụ: MemberCreateRequest)
    // và một phương thức phù hợp trong MemberService.

    @PutMapping("/{id}/streak") // Sửa API này để cập nhật streak
    public ResponseEntity<MemberResponse> updateMemberStreak(@PathVariable UUID id, @RequestParam int newStreak) {
        try {
            MemberResponse updated = memberService.updateMemberStreak(id, newStreak);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID id) {
        memberService.deleteMemberById(id); // Sửa: gọi đúng phương thức
        return ResponseEntity.noContent().build();
    }
}