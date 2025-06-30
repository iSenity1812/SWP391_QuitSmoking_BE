package com.swp391project.SWP391_QuitSmoking_BE.enums;

public enum QuitPlanStatus {
    COMPLETED,
    IN_PROGRESS,
    NOT_STARTED,
    FAILED
}

/*
* Tracking thông minh
* Completed: Người dùng đã hoàn thành kế hoạch bỏ thuốc lá (ko nhất thiết phải 100%)
*     - Mức độ hoàn thành có thể được tính dựa trên số ngày đã thực hiện so với tổng số ngày trong kế hoạch (>=80%)
* Not Started: Người dùng chưa bắt đầu kế hoạch bỏ thuốc lá (tức là ngày bắt đầu chưa đến)
* In Progress: Người dùng đang thực hiện kế hoạch bỏ thuốc lá (tức là ngày bắt đầu đã đến nhưng chưa kết thúc) - StartDate <= now < EndDate
* Failed: Người dùng ko tuân thủ kế hoạch (ko hoàn thành được chỉ tiêu)
* Restarted: Người dùng đã bắt đầu lại kế hoạch bỏ thuốc lá (tức là đã hoàn thành một kế hoạch trước đó và bắt đầu một kế hoạch mới)
* */