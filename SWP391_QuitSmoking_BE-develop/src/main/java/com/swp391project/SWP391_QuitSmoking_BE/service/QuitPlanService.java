package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.*;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ReductionQuitPlanType;
import com.swp391project.SWP391_QuitSmoking_BE.exception.QuitPlanChangedTypeException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.*;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuitPlanService {
    private static final Logger log = LoggerFactory.getLogger(QuitPlanService.class);
    private final QuitPlanRepository quitPlanRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;
    private final DailySummaryService dailySummaryService;

    private static final LocalDate MAX_GOAL_DATE = LocalDate.of(2999, 12, 31);

    @Autowired
    public QuitPlanService(
            QuitPlanRepository quitPlanRepository,
            MemberRepository memberRepository,
            @Lazy DailySummaryService dailySummaryService, //@Lazy to break potential cycles
            ModelMapper modelMapper) {
        this.dailySummaryService = dailySummaryService;
        this.quitPlanRepository = quitPlanRepository;
        this.memberRepository = memberRepository;
        this.modelMapper = modelMapper;
    }

    // Chuyển đổi QuitPlan entity sang DTO
    private QuitPlanResponseDTO convertToResponseDto(QuitPlan quitPlan) {
        return modelMapper.map(quitPlan, QuitPlanResponseDTO.class);
    }

    // Chuyển đổi QuitPlan entity sang Admin DTO
    private QuitPlanAdminResponseDTO convertToAdminResponseDto(QuitPlan quitPlan) {
        return modelMapper.map(quitPlan, QuitPlanAdminResponseDTO.class);
    }

    public boolean isPlanInProgress(QuitPlan quitPlan) {
        return quitPlan.getStatus() == QuitPlanStatus.IN_PROGRESS;
    }

    @Transactional(readOnly = true)
    public Optional<QuitPlan> getProgressQuitPlansByMemberId(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với ID: " + memberId));
        return quitPlanRepository.
                findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.IN_PROGRESS);
    }

    //Lấy kế hoạch cai thuốc lá theo ID cho người dùng đã xác thực
    //Đảm bảo trạng thái của kế hoạch được cập nhật trước khi trả về
    @Transactional(readOnly = true)
    public QuitPlanResponseDTO getQuitPlanById(Integer id, UUID memberId) {
        QuitPlan quitPlan = quitPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));

        // Kiểm tra quyền sở hữu
        if (!quitPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }

        //Đảm bảo trạng thái được cập nhật theo thời gian thực trước khi trả về
        ensureQuitPlanStatusIsCurrent(quitPlan);
        return convertToResponseDto(quitPlan);
    }

    //ADMIN: Lấy tất cả các kế hoạch cai thuốc lá trong hệ thống
    @Transactional(readOnly = true)
    public List<QuitPlanAdminResponseDTO> getAllQuitPlansForAdmin() {
        return quitPlanRepository.findAllWithMemberAndUser().stream()
                .peek(this::ensureQuitPlanStatusIsCurrent)
                .map(this::convertToAdminResponseDto)
                .collect(Collectors.toList());
    }

    // get quit plans by Id for admin
    @Transactional
    public QuitPlanAdminResponseDTO getQuitPlanByIdForAdmin(Integer id) {
        QuitPlan quitPlan = quitPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy kế hoạch cai thuốc với ID: " + id));

        ensureQuitPlanStatusIsCurrent(quitPlan);
        return convertToAdminResponseDto(quitPlan);
    }

    @Transactional(readOnly = true)
    public List<QuitPlanResponseDTO> getQuitPlansByMemberId(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMember_MemberId(memberId);
        return quitPlans.stream()
                .peek(this::ensureQuitPlanStatusIsCurrent)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    //ADMIN: Lấy tất cả các kế hoạch cai thuốc lá của một thành viên theo ID của thành viên
    @Transactional(readOnly = true)
    public List<QuitPlanAdminResponseDTO> getQuitPlansByMemberIdForAdmin(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> quitPlans = quitPlanRepository.findByMemberOrderByCreatedAtDesc(member);
        return quitPlans.stream()
                .peek(this::ensureQuitPlanStatusIsCurrent)
                .map(this::convertToAdminResponseDto)
                .collect(Collectors.toList());
    }

    // Lấy quit plan hiện tại của một thành viên (chỉ có một kế hoạch cai thuốc đang hoạt động)
    //(đang IN_PROGRESS hoặc NOT_STARTED)
    @Transactional(readOnly = true)
    public QuitPlanResponseDTO getCurrentQuitPlanByMemberId(UUID memberId) {
        // Kiểm tra sự tồn tại của Member trước
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        // Lấy kế hoạch cai thuốc hiện tại (chỉ có một kế hoạch cai thuốc đang hoạt động)
        // Attempt to find IN_PROGRESS first, then NOT_STARTED
        Optional<QuitPlan> inProgressPlan = quitPlanRepository.
                findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.IN_PROGRESS);
        QuitPlan currentPlan;

        currentPlan = inProgressPlan.orElseGet(() -> quitPlanRepository.
                findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.NOT_STARTED)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch cai thuốc đang hoạt động hoặc chưa bắt đầu cho thành viên với ID: " + memberId)));

        ensureQuitPlanStatusIsCurrent(currentPlan);
        return convertToResponseDto(currentPlan);
    }

    //Tự động gán trạng thái (NOT_STARTED hoặc IN_PROGRESS) dựa trên StartDate so với thời điểm hiện tại
    //Có giới hạn thời gian 3 tháng cho các loại kế hoạch giảm dần
    @Transactional
    public QuitPlanResponseDTO createQuitPlan(UUID memberId, QuitPlanCreateRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu tạo không được để trống");
        }

        // Kiểm tra sự tồn tại của Member (ID của Member chính là ID của User)
        // Vì Member entity sử dụng @MapsId, memberRepository.findById(userId) sẽ tìm Member có userId đó
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> existingActivePlans = quitPlanRepository.findByMemberAndStatusIn(
                member, List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );

        if (!existingActivePlans.isEmpty()) {
            log.warn("Thành viên {} đã có kế hoạch cai thuốc đang hoạt động. Không thể tạo kế hoạch mới.", memberId);
            throw new IllegalArgumentException("Thành viên đã có kế hoạch cai thuốc đang hoạt động.");
        }

        if (request.getPricePerPack().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Giá tiền mỗi gói thuốc phải lớn hơn 0");
        }

        //Buộc loại kế hoạch dựa trên InitialSmokingAmount
        if (request.getInitialSmokingAmount() >= 1 && request.getInitialSmokingAmount() <= 2) {
            // Đối với 1-2 điếu, buộc chọn loại IMMEDIATE
            log.info("Số điếu thuốc ban đầu là {}. Buộc loại giảm dần IMMEDIATE cho thành viên {}",
                    request.getInitialSmokingAmount(), memberId);
            request.setReductionType(ReductionQuitPlanType.IMMEDIATE);
            request.setGoalDate(MAX_GOAL_DATE);
            request.setStartDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1)); // Bắt đầu ngay bây giờ
            log.warn("Kế hoạch đã tự động chuyển sang loại IMMEDIATE (do số điếu thuốc ban đầu quá thấp)");
        }

        //Truncated để đảm bảo tính nhất quán
        LocalDateTime nowForConsistency = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);
        QuitPlan quitPlan = new QuitPlan();
        quitPlan.setMember(member);
        quitPlan.setCreatedAt(nowForConsistency); // Đặt ngày tạo là hiện tại

        ReductionQuitPlanType reductionType = request.getReductionType();
        LocalDateTime actualStartDate;
        QuitPlanStatus status;
        LocalDate goalDate;
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate().toLocalDate() : LocalDate.now();

        // Logic xử lý quitType IMMEDIATE và kế hoạch giảm dần 1 ngày
        // Nếu là loại IMMEDIATE, hoặc loại giảm dần nhưng startDate/goalDate chỉ cách nhau 1 ngày
        boolean isImmediateOrOneDayGradual = (request.getReductionType() == ReductionQuitPlanType.IMMEDIATE)
                || ChronoUnit.DAYS.between(startDate, request.getGoalDate()) == 0
                || ChronoUnit.DAYS.between(startDate, request.getGoalDate()) == 1;

        if (isImmediateOrOneDayGradual) {
            // Đối với kế hoạch "dừng hẳn" (IMMEDIATE hoặc giảm dần - 1 ngày)
            // StartDate là bây giờ, GoalDate là vô tận (thực tế là rất xa)
            actualStartDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);
            goalDate = MAX_GOAL_DATE;
            status = QuitPlanStatus.IN_PROGRESS;
            reductionType = ReductionQuitPlanType.IMMEDIATE; // Đặt loại là IMMEDIATE
            log.info("Tạo kế hoạch Dừng Hẳn (IMMEDIATE) hoặc 1 ngày cho thành viên {}. Ngày bắt đầu: {}, Mục tiêu: {}",
                    memberId, actualStartDate, goalDate);
        } else {
            // Logic cho các loại giảm dần có thời gian > 1 ngày
            if (request.getStartDate() != null) {
                // Startdate được cung cấp, sử dụng ngày đó
                actualStartDate = request.getStartDate();
                if (actualStartDate.isAfter(LocalDateTime.now())) {
                    status = QuitPlanStatus.NOT_STARTED;
                    log.info("Ngày bắt đầu trong tương lai: {}", actualStartDate);
                } else {
                    status = QuitPlanStatus.IN_PROGRESS;
                    log.info("Ngày bắt đầu là hôm nay hoặc trong quá khứ: {}", actualStartDate);
                }
            } else {
                // Startdate ko được cung cấp, sử dụng ngày hiện tại
                actualStartDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(1);
                status = QuitPlanStatus.IN_PROGRESS; // Ngày bắt đầu là hôm nay
                log.info("Ngày bắt đầu không được cung cấp, sử dụng ngày hiện tại (+1s): {}", actualStartDate);
            }

            // Đảm bảo goalDate không null cho kế hoạch giảm dần
            if (request.getGoalDate() == null) {
                throw new IllegalArgumentException("Ngày kết thúc mục tiêu không được để trống");
            }
            goalDate = request.getGoalDate();

            // Logic giới hạn thời gian 3 tháng cho kế hoạch giảm dần (chỉ áp dụng khi không phải 1 ngày)
            LocalDate startLocalDate = actualStartDate.toLocalDate();
            LocalDate goalLocalDate = goalDate;

            if (goalLocalDate.isBefore(startLocalDate)) {
                throw new IllegalArgumentException("Ngày kết thúc mục tiêu không thể trước ngày bắt đầu");
            }

            Period period = Period.between(startLocalDate, goalLocalDate);
            long totalMonths = period.toTotalMonths(); // Tổng số tháng (ví dụ: 3 tháng 1 ngày vẫn là 3 tháng)
            int days = period.getDays(); // Số ngày lẻ sau khi tính tháng

            // Giới hạn 3 tháng:
            // - Nếu tổng số tháng lớn hơn 3
            // - Hoặc nếu tổng số tháng bằng 3 và có thêm ngày lẻ (nghĩa là 3 tháng và một vài ngày)
            if (totalMonths > 3 || (totalMonths == 3 && days > 0)) {
                throw new IllegalArgumentException("Kế hoạch cai thuốc giảm dần không được vượt quá 3 tháng");
            }
        }

        quitPlan.setReductionType(reductionType);
        quitPlan.setStartDate(actualStartDate); // Chuyển đổi sang LocalDateTime
        quitPlan.setStatus(status); // Trạng thái mới là IN_PROGRESS hoặc NOT_STARTED
        quitPlan.setGoalDate(goalDate);
        quitPlan.setInitialSmokingAmount(request.getInitialSmokingAmount());
        quitPlan.setCigarettesPerPack(request.getCigarettesPerPack());
        quitPlan.setPricePerPack(request.getPricePerPack());

        // Lưu kế hoạch cai thuốc vào cơ sở dữ liệu
        log.info("Lưu kế hoạch cai thuốc cho thành viên: {}", memberId);
        QuitPlan savedPlan = quitPlanRepository.save(quitPlan);
        log.info("Kế hoạch cai thuốc đã được lưu với ID: {}", savedPlan.getQuitPlanId());

        // Kiểm tra và cập nhật trạng thái ngay lập tức sau khi lưu
        ensureQuitPlanStatusIsCurrent(savedPlan);
        return convertToResponseDto(savedPlan);
    }

    // -- UPDATE --
    //chỉ cho người dùng update về giá tiền và số điếu trong gói - liên quan đến moneySaved
    //các thông tin khác như initialCount bị liên quan đến logic tạo quitplan -> nếu muốn đổi thì tạo plan mới
    @Transactional
    public QuitPlanResponseDTO updateQuitPlan(Integer quitPlanId, UUID memberId, QuitPlanUpdateRequestDTO updateRequestDTO) {
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // Kiểm tra quyền sở hữu
        if (!existingPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }

        if (existingPlan.getStatus() == QuitPlanStatus.COMPLETED || existingPlan.getStatus() == QuitPlanStatus.FAILED) {
            throw new IllegalArgumentException("Kế hoạch này đã " + existingPlan.getStatus() + ", không thể cập nhật");
        }

        // Đảm bảo trạng thái được cập nhật trước khi áp dụng thay đổi
        ensureQuitPlanStatusIsCurrent(existingPlan);

//        modelMapper.map(updateRequestDTO, existingPlan); //Cập nhật các trường từ DTO vào entity
        boolean changed = false;
        if (updateRequestDTO.getCigarettesPerPack() != null) {
            existingPlan.setCigarettesPerPack(updateRequestDTO.getCigarettesPerPack());
            changed = true;
        }
        if (updateRequestDTO.getPricePerPack() != null) {
            existingPlan.setPricePerPack(updateRequestDTO.getPricePerPack());
            changed = true;
        }

        if (!changed) {
            throw new IllegalArgumentException("Không có thông tin nào để cập nhật");
        } else {
            QuitPlan updatedPlan = quitPlanRepository.save(existingPlan);
            return convertToResponseDto(updatedPlan);
        }

        //Cập nhật lại tiền đã tiết kiệm được trong các bản nhật ký liên quan
       // dailySummaryService.recalculateMoneySavedForQuitPlan(updatedPlan);
    }

    // Cập nhaat kế hoạch cai thuốc hiện tại của người dùng
    @Transactional
    public QuitPlanResponseDTO updateCurrentActivePlan(UUID memberId, QuitPlanUpdateRequestDTO request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thành viên với ID: " + memberId));

        List<QuitPlan> activePlans = quitPlanRepository.findByMemberAndStatusIn(
                member, List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );
        if (activePlans.isEmpty()) {
            log.info("Không có kế hoạch cai thuốc nào đang hoạt động cho thành viên: {}", memberId);
            throw new ResourceNotFoundException
                    ("Không tìm thấy kế hoạch cai thuốc đang hoạt động với thành viên với ID: " + memberId);
        }

        QuitPlan currentPlan = activePlans.getFirst();
        ensureQuitPlanStatusIsCurrent(currentPlan);

        modelMapper.map(request, currentPlan);
        QuitPlan updatedPlan = quitPlanRepository.save(currentPlan);
        return convertToResponseDto(updatedPlan);
    }

    // -- DELETE --
    @Transactional
    public void deleteQuitPlanById(Integer quitPlanId) {
        QuitPlan existingPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        quitPlanRepository.delete(existingPlan);
    }

    // --- BUSINESS LOGIC FOR STATUS MANAGEMENT---

    //Đảm bảo trạng thái của một kế hoạch được cập nhật theo thời điểm hiện tại (just-in-time)
    //Nếu trạng thái thay đổi, nó sẽ được lưu vào cơ sở dữ liệu
    @Transactional
    public void ensureQuitPlanStatusIsCurrent(QuitPlan plan) {
        updateQuitPlanStatus(plan, LocalDateTime.now());
    }

    //Tác vụ chạy định kỳ để cập nhật trạng thái của tất cả các kế hoạch cai thuốc
    @Scheduled(cron = "0 0 0 * * ?") // Chạy mỗi ngày lúc 00:00
    @Transactional
    public void updateQuitPlanStatus() {
        List<QuitPlan> allPlans = quitPlanRepository.findAll();

        for (QuitPlan plan : allPlans) {
            ensureQuitPlanStatusIsCurrent(plan);
        }
        log.info("Hoàn thành cập nhật trạng thái kế hoạch cai thuốc cho tất cả các kế hoạch");
    }

    //Cập nhật trạng thái của một kế hoạch cai thuốc cụ thể dựa trên thời điểm hiện tại
    //Được gọi bởi scheduler hoặc trực tiếp sau khi tạo/truy cập kế hoạch
    private void updateQuitPlanStatus(QuitPlan plan, LocalDateTime currentDate) {
        QuitPlanStatus currentStatus = plan.getStatus();
        QuitPlanStatus newStatus = currentStatus;

        // Logic cập nhật status
        switch (currentStatus) {
            case NOT_STARTED:
                // Logic: Ngày hiện tại >= ngày bắt đầu -> chuyển sang IN_PROGRESS
                if (!plan.getStartDate().isAfter(currentDate)) {
                    newStatus = QuitPlanStatus.IN_PROGRESS;
                    log.info("Kế hoạch ID {} đã chuyển từ NOT_STARTED sang IN_PROGRESS", plan.getQuitPlanId());
                }
                break;
            case IN_PROGRESS:
                // Đối với kế hoạch Dừng Hẳn, kiểm tra isUserRelapsed riêng biệt
                if (plan.getReductionType() == ReductionQuitPlanType.IMMEDIATE || plan.getGoalDate().isEqual(MAX_GOAL_DATE)) {
                    // Logic cho kế hoạch Dừng Hẳn sẽ được xử lý chủ yếu bởi handleDailySummaryUpdateForRelapse`
                    // Tại đây, chúng ta chỉ đảm bảo rằng nó không tự động chuyển sang COMPLETED/FAILED
                    // nếu chưa có sự kiện tái nghiện được phát hiện
                } else {
                    // Logic cho các kế hoạch giảm dần thông thường
                    // Logic: Ngày hiện tại > ngày kết thúc -> chuyển sang COMPLETED hoặc FAILED
                    if (currentDate.toLocalDate().isAfter(plan.getGoalDate())) {
                        // Giả sử nếu người dùng đã hoàn thành ít nhất 80% kế hoạch thì coi là COMPLETED
                        // Cần có logic để tính toán mức độ hoàn thành
                        if (isPlanCompleted(plan)) {
                            newStatus = QuitPlanStatus.COMPLETED;
                            log.info("Kế hoạch ID {} đã hoàn thành COMPLETED.", plan.getQuitPlanId());
                            // Tự động tạo kế hoạch Dừng Hẳn sau khi hoàn thành kế hoạch giảm dần
                            createImmediatePlanForMemberAfterCompletion(
                                    plan.getMember(), currentDate,
                                    plan.getInitialSmokingAmount(), plan.getCigarettesPerPack(), plan.getPricePerPack());
                        } else {
                            newStatus = QuitPlanStatus.FAILED;
                            log.info("Kế hoạch ID {} đã FAILED (không hoàn thành mục tiêu).", plan.getQuitPlanId());
                        }
                    }
                }
                break;
            case COMPLETED:
            case FAILED:
                break; // Không làm gì nếu đã ở trạng thái cuối cùng
        }

        if (newStatus != currentStatus) {
            plan.setStatus(newStatus);
            quitPlanRepository.save(plan); // Lưu lại thay đổi
        }
    }

    //Tạo một kế hoạch Dừng Hẳn (IMMEDIATE) mới cho thành viên sau khi hoàn thành kế hoạch giảm dần
    @Transactional
    public void createImmediatePlanForMemberAfterCompletion(
            Member member, LocalDateTime creationTime,
            int initialSmokingAmount, int cigarettesPerPack, BigDecimal pricePerPack) {
        List<QuitPlan> activePlans = quitPlanRepository.findByMemberAndStatusIn(
                member, List.of(QuitPlanStatus.IN_PROGRESS, QuitPlanStatus.NOT_STARTED)
        );

        if (!activePlans.isEmpty()) {
            log.warn("Thành viên {} đã có kế hoạch đang hoạt động. Không tạo thêm kế hoạch Dừng Hẳn tự động",
                    member.getMemberId());
            return;
        }

        QuitPlan immediatePlan = new QuitPlan();
        immediatePlan.setMember(member);
        immediatePlan.setReductionType(ReductionQuitPlanType.IMMEDIATE);
        immediatePlan.setCreatedAt(creationTime);
        immediatePlan.setStartDate(creationTime);
        immediatePlan.setGoalDate(MAX_GOAL_DATE); // Vô tận
        immediatePlan.setStatus(QuitPlanStatus.IN_PROGRESS);

        //Lấy từ COMPLETED plan trước đó
        immediatePlan.setInitialSmokingAmount(initialSmokingAmount);
        immediatePlan.setCigarettesPerPack(cigarettesPerPack);
        immediatePlan.setPricePerPack(pricePerPack);

        quitPlanRepository.save(immediatePlan);
        log.info("Đã tạo kế hoạch Dừng Hẳn mới (ID: {}) cho thành viên {} sau khi hoàn thành kế hoạch trước đó",
                immediatePlan.getQuitPlanId(), member.getMemberId());
    }

    //Kiểm tra xem kế hoạch có được coi là COMPLETED hay không
    //COMPLETED (đạt >= 80% mục tiêu)
    private boolean isPlanCompleted(QuitPlan plan) {
        return dailySummaryService.isPlanCompletedForQuitPlan(plan);
    }

    //Logic kiểm tra xem người dùng có tái nghiện hay không
    //Dựa trên số ngày liên tiếp vượt quá ngưỡng cho phép
    private boolean isUserRelapsed(QuitPlan plan) {
        return dailySummaryService.isUserRelapsedForQuitPlan(plan);
    }

    //Phương thức chính để kiểm tra và xử lý tái nghiện dựa trên DailySummary đã cập nhật
    // Nếu tái nghiện được phát hiện, trạng thái kế hoạch sẽ chuyển sang FAILED
    @Transactional
    public void handleDailySummaryUpdateForRelapse(DailySummary updatedDailySummary) {
        if (updatedDailySummary == null || updatedDailySummary.getQuitPlan() == null) {
            log.warn("không thể kiểm tra tái nghiện vì DailySummary hoặc QuitPlan không hợp lệ");
            return;
        }

        QuitPlan quitPlan = updatedDailySummary.getQuitPlan();

        // Chỉ kiểm tra cho các kế hoạch đang IN_PROGRESS
        if (quitPlan.getStatus() != QuitPlanStatus.IN_PROGRESS) {
            return;
        }

        boolean relapsed = false;

        // Nếu là kế hoạch Dừng Hẳn (IMMEDIATE hoặc 1 ngày), kiểm tra trực tiếp DailySummary vừa cập nhật
        if (quitPlan.getReductionType() == ReductionQuitPlanType.IMMEDIATE || quitPlan.getGoalDate().isEqual(MAX_GOAL_DATE)) {
            if (updatedDailySummary.getTotalSmokedCount() > 0) {
                relapsed = true;
                log.info("Tái nghiện phát hiện cho kế hoạch Dừng Hẳn ID {} do số điếu hút > 0 trong DailySummary ID {} vào ngày {}",
                        quitPlan.getQuitPlanId(), updatedDailySummary.getDailySummaryId(), updatedDailySummary.getTrackDate());
            }
        } else {
            // Đối với các kế hoạch giảm dần, sử dụng logic kiểm tra mẫu tái nghiện
            relapsed = isUserRelapsed(quitPlan);
        }

        if (relapsed) {
            // Đặt trạng thái kế hoạch thành FAILED
            if (quitPlan.getStatus() == QuitPlanStatus.IN_PROGRESS) { // Đảm bảo chỉ chuyển từ IN_PROGRESS
                quitPlanRepository.updateQuitPlanStatus(quitPlan.getQuitPlanId(), QuitPlanStatus.FAILED);
                log.warn("Trạng thái kế hoạch ID {} đã chuyển sang FAILED do phát hiện tái nghiện.", quitPlan.getQuitPlanId());
            }
        }
    }

    //Đặt lại trạng thái của một kế hoạch từ FAILED sang IN_PROGRESS
    //phía frontend gửi yêu cầu để đặt lại trạng thái của một QuitPlan từ FAILED về IN_PROGRESS
    //nếu người dùng chọn "giữ kế hoạch hiện tại" sau khi tái nghiện
    @Transactional
    public QuitPlanResponseDTO resetQuitPlanToInProgress(Integer quitPlanId, UUID memberId) {
        QuitPlan quitPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        if (!quitPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }

        if (quitPlan.getStatus() != QuitPlanStatus.FAILED) {
            throw new IllegalArgumentException("Không thể đặt lại kế hoạch không ở trạng thái FAILED");
        }

        quitPlanRepository.updateQuitPlanStatus(quitPlanId, QuitPlanStatus.IN_PROGRESS);
        log.info("Kế hoạch ID {} của thành viên {} đã được đặt lại từ FAILED sang IN_PROGRESS", quitPlanId, memberId);

        Optional<QuitPlan> updatedPlan = quitPlanRepository.findById(quitPlanId);
        if (updatedPlan.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy kế hoạch đã cập nhật với ID: " + quitPlanId);
        }
        return convertToResponseDto(updatedPlan.get());
    }

    // GIVE UP QUIT PLAN
    //Kế hoạch sẽ được chuyển trạng thái sang FAILED
    @Transactional
    public QuitPlanResponseDTO giveUpQuitPlan(Integer quitPlanId, UUID memberId) {
        QuitPlan currentPlan = quitPlanRepository.findById(quitPlanId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Không tìm thấy kế hoạch bỏ thuốc với ID: " + quitPlanId));

        // Kiểm tra quyền sở hữu
        if (!currentPlan.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("Kế hoạch này không thuộc về thành viên với ID: " + memberId);
        }
        // Kiểm tra trạng thái hiện tại
        if (currentPlan.getStatus() == QuitPlanStatus.FAILED || currentPlan.getStatus() == QuitPlanStatus.COMPLETED) {
            throw new IllegalArgumentException("Không thể bỏ cuộc kế hoạch đã hoàn thành hoặc đã thất bại");
        }

        // Đảm bảo trạng thái được cập nhật theo thời gian thực trước khi ép buộc sang FAILED
        ensureQuitPlanStatusIsCurrent(currentPlan);

        currentPlan.setGoalDate(LocalDate.now()); //ngày kết thúc kế hoạch
        currentPlan.setStatus(QuitPlanStatus.FAILED);
        log.info("Kế hoạch ID {} của thành viên {} đã được đánh dấu là FAILED do người dùng bỏ cuộc",
                quitPlanId, memberId);

        return modelMapper.map(currentPlan, QuitPlanResponseDTO.class);
    }
}