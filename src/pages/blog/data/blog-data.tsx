import type { BlogPost, Comment } from "../types/blog-types"

// Sample blog posts data
export const sampleBlogPosts: BlogPost[] = [
    {
        BlogID: 1,
        AuthorID: "user-123",
        AuthorRole: "Coach",
        Title: "10 Chiến lược đã được chứng minh để vượt qua cơn thèm thuốc lá",
        Content:
            "Khám phá những kỹ thuật hiệu quả đã giúp hàng nghìn người thành công trong việc quản lý và vượt qua cơn thèm thuốc lá trong hành trình cai thuốc. Cai thuốc lá là một hành trình đầy thách thức nhưng cũng vô cùng xứng đáng...",
        CreatedAt: "2024-01-15T10:30:00",
        LastUpdated: "2024-01-16T14:20:00",
        Status: "Published",
        ApprovedBy: "admin-456",
        ApprovedAt: "2024-01-15T12:45:00",
        UserHasLiked: false,
    },
    {
        BlogID: 2,
        AuthorID: "user-456",
        AuthorRole: "Premium member",
        Title: "Hành trình của tôi: Từ 2 gói thuốc mỗi ngày đến không hút thuốc",
        Content:
            "Câu chuyện cá nhân về việc vượt qua thói quen hút thuốc 15 năm và những thách thức gặp phải trên đường đi. Tôi đã hút thuốc trong 15 năm, từ khi còn là sinh viên đại học...",
        CreatedAt: "2024-01-12T08:15:00",
        Status: "Published",
        ApprovedBy: "admin-789",
        ApprovedAt: "2024-01-12T10:30:00",
        UserHasLiked: false,
    },
    {
        BlogID: 3,
        AuthorID: "user-789",
        AuthorRole: "Coach",
        Title: "Khoa học đằng sau nghiện nicotine",
        Content:
            "Hiểu cách nicotine ảnh hưởng đến não bộ và tại sao việc cai thuốc có thể khó khăn từ góc độ khoa học. Nicotine là một chất gây nghiện mạnh...",
        CreatedAt: "2024-01-10T15:45:00",
        LastUpdated: "2024-01-11T09:20:00",
        Status: "Pending Approval",
        UserHasLiked: false,
    },
    {
        BlogID: 4,
        AuthorID: "user-current",
        AuthorRole: "Premium member",
        Title: "Xây dựng thói quen lành mạnh để thay thế việc hút thuốc",
        Content:
            "Học cách tạo ra những thói quen tích cực sẽ giúp bạn duy trì cuộc sống không thuốc lá và cải thiện sức khỏe tổng thể...",
        CreatedAt: "2024-01-08T11:20:00",
        Status: "Published",
        ApprovedBy: "admin-456",
        ApprovedAt: "2024-01-08T14:15:00",
        UserHasLiked: false,
    },
    {
        BlogID: 5,
        AuthorID: "user-654",
        AuthorRole: "Premium member",
        Title: "Động lực hàng ngày: Tại sao bản thân tương lai sẽ cảm ơn bạn",
        Content: "Những suy nghĩ truyền cảm hứng và lời nhắc nhở để giữ động lực trên hành trình không thuốc lá...",
        CreatedAt: "2024-01-05T09:30:00",
        Status: "Published",
        ApprovedBy: "admin-789",
        ApprovedAt: "2024-01-05T11:45:00",
        UserHasLiked: false,
    },
]

// Sample comments data
export const sampleComments: Comment[] = [
    {
        CommentID: 1,
        BlogID: 1,
        UserID: "user-111",
        Content: "Bài viết rất hữu ích! Tôi đã áp dụng chiến lược số 3 và thấy hiệu quả ngay.",
        CommentDate: "2024-01-16T08:45:00",
    },
    {
        CommentID: 2,
        BlogID: 1,
        UserID: "user-222",
        ParentCommentID: 1,
        Content: "Tôi cũng thấy chiến lược đó hiệu quả. Bạn đã thử chiến lược số 5 chưa?",
        CommentDate: "2024-01-16T09:30:00",
    },
    {
        CommentID: 3,
        BlogID: 1,
        UserID: "user-333",
        Content: "Cảm ơn tác giả đã chia sẻ những kinh nghiệm quý báu!",
        CommentDate: "2024-01-17T10:15:00",
    },
    {
        CommentID: 4,
        BlogID: 2,
        UserID: "user-444",
        Content: "Câu chuyện rất cảm động và truyền cảm hứng!",
        CommentDate: "2024-01-13T14:20:00",
    },
]
