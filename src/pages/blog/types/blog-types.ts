export type UserRole = "Normal member" | "Premium member" | "Coach" | "Content Admin"

// Interface for blog posts
export interface BlogPost {
    BlogID: number
    AuthorID: string
    AuthorRole: UserRole
    Title: string
    Content: string
    CreatedAt: string
    LastUpdated?: string
    Status: "Pending Approval" | "Published" | "Rejected"
    ApprovedBy?: string
    ApprovedAt?: string
    UserHasLiked?: boolean
}

// Interface for comments
export interface Comment {
    CommentID: number
    BlogID: number
    UserID: string
    ParentCommentID?: number
    Content: string
    CommentDate: string
}

// Interface for reports
export interface Report {
    ReportID: number
    ReporterID: string
    ReportType: "User Report" | "Content Report" | "Bug Report"
    ReportedID?: string
    ReportedContentType?: "Blog" | "Program" | "Bug" | "Other"
    ReportedContentID?: number
    Reason: string
    Status: "Pending" | "Reviewed" | "Resolved" | "Rejected"
    GeneratedDate: string
    reviewedByID?: string
    AdminNotes?: string
}

export interface BlogUser {
    id: string
    name: string
    role: UserRole
}

export interface BlogFormData {
    title: string
    content: string
}

export interface ReportFormData {
    reason: string
    reportType: "Content Report"
    reportedContentType: "Blog"
}
