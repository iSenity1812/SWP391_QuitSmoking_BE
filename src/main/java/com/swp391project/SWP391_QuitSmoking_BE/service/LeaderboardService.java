package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.response.LeaderboardEntry;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.LeaderboardType;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    
    private final AchievementService achievementService;
    private final MemberRepository memberRepository;
    
    /**
     * Lấy leaderboard theo tiền tiết kiệm được
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getMoneySavedLeaderboard(int limit) {
        List<Member> members = memberRepository.findAll();
        
        List<LeaderboardEntry> entries = members.stream()
            .filter(member -> member.getUser() != null) // Bỏ qua member không có user
            .map(member -> {
                try {
                    BigDecimal moneySaved = achievementService.calculateMoneySaved(member.getMemberId());
                    String displayValue = formatMoney(moneySaved);
                    String username = member.getUser().getUsername() != null ? member.getUser().getUsername() : "(Không tên)";
                    String profilePicture = member.getUser().getProfilePicture();
                    return new LeaderboardEntry(
                        member.getMemberId(),
                        username,
                        profilePicture,
                        moneySaved,
                        0, // Rank sẽ được set sau
                        LeaderboardType.MONEY_SAVED.name(),
                        displayValue
                    );
                } catch (Exception e) {
                    // Log lỗi chi tiết
                    System.err.println("[LeaderboardService] Lỗi tính moneySaved cho memberId: " + member.getMemberId() + ", user: " + (member.getUser() != null ? member.getUser().getUsername() : "null") + ", lỗi: " + e.getMessage());
                    return null;
                }
            })
            .filter(entry -> entry != null && entry.getScore().compareTo(BigDecimal.ZERO) > 0)
            .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
            
        // Set rank
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        
        return entries;
    }
    
    /**
     * Lấy leaderboard theo số ngày bỏ thuốc
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getDaysQuitLeaderboard(int limit) {
        List<Member> members = memberRepository.findAll();
        
        List<LeaderboardEntry> entries = members.stream()
            .filter(member -> member.getUser() != null)
            .map(member -> {
                try {
                    BigDecimal daysQuit = achievementService.calculateDaysQuit(member.getMemberId());
                    String displayValue = formatDays(daysQuit);
                    String username = member.getUser().getUsername() != null ? member.getUser().getUsername() : "(Không tên)";
                    String profilePicture = member.getUser().getProfilePicture();
                    return new LeaderboardEntry(
                        member.getMemberId(),
                        username,
                        profilePicture,
                        daysQuit,
                        0,
                        LeaderboardType.DAYS_QUIT.name(),
                        displayValue
                    );
                } catch (Exception e) {
                    System.err.println("[LeaderboardService] Lỗi tính daysQuit cho memberId: " + member.getMemberId() + ", user: " + (member.getUser() != null ? member.getUser().getUsername() : "null") + ", lỗi: " + e.getMessage());
                    return null;
                }
            })
            .filter(entry -> entry != null && entry.getScore().compareTo(BigDecimal.ZERO) > 0)
            .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
            
        // Set rank
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        
        return entries;
    }
    
    /**
     * Lấy leaderboard theo số điếu đã tránh hút
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getCigarettesAvoidedLeaderboard(int limit) {
        List<Member> members = memberRepository.findAll();
        
        List<LeaderboardEntry> entries = members.stream()
            .filter(member -> member.getUser() != null)
            .map(member -> {
                try {
                    BigDecimal cigarettesAvoided = achievementService.calculateCigarettesNotSmoked(member.getMemberId());
                    String displayValue = formatCigarettes(cigarettesAvoided);
                    String username = member.getUser().getUsername() != null ? member.getUser().getUsername() : "(Không tên)";
                    String profilePicture = member.getUser().getProfilePicture();
                    return new LeaderboardEntry(
                        member.getMemberId(),
                        username,
                        profilePicture,
                        cigarettesAvoided,
                        0,
                        LeaderboardType.CIGARETTES_AVOIDED.name(),
                        displayValue
                    );
                } catch (Exception e) {
                    System.err.println("[LeaderboardService] Lỗi tính cigarettesAvoided cho memberId: " + member.getMemberId() + ", user: " + (member.getUser() != null ? member.getUser().getUsername() : "null") + ", lỗi: " + e.getMessage());
                    return null;
                }
            })
            .filter(entry -> entry != null && entry.getScore().compareTo(BigDecimal.ZERO) > 0)
            .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
            
        // Set rank
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        
        return entries;
    }
    
    /**
     * Lấy leaderboard theo số lượng thành tựu
     */
    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getAchievementCountLeaderboard(int limit) {
        List<Member> members = memberRepository.findAll();
        List<LeaderboardEntry> entries = members.stream()
            .filter(member -> member.getUser() != null)
            .map(member -> {
                try {
                    long count = achievementService.getMemberAchievementRepository().countByMember_MemberId(member.getMemberId());
                    String username = member.getUser().getUsername() != null ? member.getUser().getUsername() : "(Không tên)";
                    String profilePicture = member.getUser().getProfilePicture();
                    return new LeaderboardEntry(
                        member.getMemberId(),
                        username,
                        profilePicture,
                        java.math.BigDecimal.valueOf(count),
                        0,
                        LeaderboardType.ACHIEVEMENT_COUNT.name(),
                        count + " thành tựu"
                    );
                } catch (Exception e) {
                    System.err.println("[LeaderboardService] Lỗi tính achievementCount cho memberId: " + member.getMemberId() + ", user: " + (member.getUser() != null ? member.getUser().getUsername() : "null") + ", lỗi: " + e.getMessage());
                    return null;
                }
            })
            .filter(entry -> entry != null && entry.getScore().compareTo(java.math.BigDecimal.ZERO) > 0)
            .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }
        return entries;
    }
    
    /**
     * Lấy rank của user hiện tại trong leaderboard
     */
    @Transactional(readOnly = true)
    public LeaderboardEntry getUserRank(UUID memberId, String leaderboardType) {
        List<LeaderboardEntry> allEntries;
        
        switch (leaderboardType.toUpperCase()) {
            case "MONEY_SAVED":
                allEntries = getMoneySavedLeaderboard(Integer.MAX_VALUE);
                break;
            case "DAYS_QUIT":
                allEntries = getDaysQuitLeaderboard(Integer.MAX_VALUE);
                break;
            case "CIGARETTES_AVOIDED":
                allEntries = getCigarettesAvoidedLeaderboard(Integer.MAX_VALUE);
                break;
            default:
                throw new IllegalArgumentException("Invalid leaderboard type: " + leaderboardType);
        }
        
        return allEntries.stream()
            .filter(entry -> entry.getMemberId().equals(memberId))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Format tiền theo định dạng Việt Nam
     */
    private String formatMoney(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }
    
    /**
     * Format số ngày
     */
    private String formatDays(BigDecimal days) {
        int daysInt = days.intValue();
        if (daysInt == 1) {
            return "1 ngày";
        } else {
            return daysInt + " ngày";
        }
    }
    
    /**
     * Format số điếu
     */
    private String formatCigarettes(BigDecimal cigarettes) {
        int cigarettesInt = cigarettes.intValue();
        if (cigarettesInt == 1) {
            return "1 điếu";
        } else {
            return cigarettesInt + " điếu";
        }
    }
} 