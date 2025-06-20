package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievement;
import com.swp391project.SWP391_QuitSmoking_BE.entity.MemberAchievementId;
import com.swp391project.SWP391_QuitSmoking_BE.service.MemberAchievementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/member-achievements")
public class MemberAchievementAPI {
    @Autowired
    private MemberAchievementService memberAchievementService;

    @GetMapping
    public List<MemberAchievement> getAllMemberAchievements() {
        return memberAchievementService.findAll();
    }

    @GetMapping("/{achievementId}/{memberId}")
    public ResponseEntity<MemberAchievement> getMemberAchievementById(@PathVariable Integer achievementId,
                                                                      @PathVariable String memberId) {
        MemberAchievementId id = new MemberAchievementId(achievementId, UUID.fromString(memberId));
        Optional<MemberAchievement> memberAchievement = memberAchievementService.findById(id);
        return memberAchievement.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MemberAchievement> createMemberAchievement(
            @Valid @RequestBody MemberAchievement memberAchievement) {
        // Đảm bảo id được thiết lập đúng cách từ request hoặc service.
        // Nếu memberAchievement.id được gửi từ client, Lombok sẽ tự động set các thành phần qua constructor/setter của MemberAchievementId.
        MemberAchievement created = memberAchievementService.save(memberAchievement);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{achievementId}/{memberId}")
    public ResponseEntity<MemberAchievement> updateMemberAchievement(@PathVariable Integer achievementId,
                                                                     @PathVariable String memberId, @Valid @RequestBody MemberAchievement memberAchievement) {
        MemberAchievementId id = new MemberAchievementId(achievementId, UUID.fromString(memberId));

        Optional<MemberAchievement> existingMemberAchievement = memberAchievementService.findById(id);
        if (!existingMemberAchievement.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        MemberAchievement existing = existingMemberAchievement.get();

        // Cập nhật các trường dữ liệu của đối tượng hiện có từ request body
        existing.setAchievedDate(memberAchievement.getAchievedDate());
        existing.setIsShared(memberAchievement.getIsShared());

        MemberAchievement updated = memberAchievementService.save(existing);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{achievementId}/{memberId}")
    public ResponseEntity<Void> deleteMemberAchievement(@PathVariable Integer achievementId,
                                                        @PathVariable String memberId) {
        MemberAchievementId id = new MemberAchievementId(achievementId, UUID.fromString(memberId));
        memberAchievementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}