import type { Comment, UserRole } from "../types/blog-types"

// Format date to Vietnamese locale
export const formatDate = (dateString: string) => {
    const date = new Date(dateString)
    return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    })
}

// Get root comments for a blog post
export const getRootComments = (comments: Comment[], blogId: number) => {
    return comments.filter((comment) => comment.BlogID === blogId && !comment.ParentCommentID)
}

// Get replies for a comment
export const getReplies = (comments: Comment[], commentId: number) => {
    return comments.filter((comment) => comment.ParentCommentID === commentId)
}

// Get publishing status based on user role
export const getPublishingStatus = (role: UserRole) => {
    // Coach blogs need approval, member blogs are published immediately
    return role === "Coach" ? "Pending Approval" : "Published"
}

// Get publishing message based on user role
export const getPublishingMessage = (role: UserRole) => {
    if (role === "Coach") {
        return "Bài viết của Coach sẽ được gửi để chờ phê duyệt trước khi xuất bản."
    } else {
        return "Bài viết sẽ được xuất bản ngay lập tức."
    }
}
