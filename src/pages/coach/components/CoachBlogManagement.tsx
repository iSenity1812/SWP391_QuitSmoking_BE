"use client"

import { useState } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
    BookOpen,
    Plus,
    Edit,
    Trash2,
    Eye,
    MessageSquare,
    Heart,
    Search,
    Clock,
    CheckCircle,
    TrendingUp,
} from "lucide-react"

interface BlogPost {
    id: number
    title: string
    content: string
    excerpt: string
    status: "draft" | "pending" | "published" | "rejected"
    createdAt: string
    updatedAt?: string
    views: number
    likes: number
    comments: number
    tags: string[]
}

interface Comment {
    id: number
    blogId: number
    author: string
    content: string
    createdAt: string
    avatar: string
}

export function CoachBlogManagement() {
    const [searchTerm, setSearchTerm] = useState("")
    const [filterStatus, setFilterStatus] = useState<string>("all")
    const [selectedPost, setSelectedPost] = useState<BlogPost | null>(null)
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
    const [newPost, setNewPost] = useState({
        title: "",
        content: "",
        tags: "",
    })

    const blogPosts: BlogPost[] = [
        {
            id: 1,
            title: "5 Kỹ Thuật Thở Sâu Giúp Kiểm Soát Cơn Thèm Thuốc",
            content: "Khi bạn cảm thấy thèm thuốc, hãy thử áp dụng những kỹ thuật thở sâu sau đây...",
            excerpt: "Học cách sử dụng hơi thở để vượt qua cơn thèm thuốc một cách hiệu quả",
            status: "published",
            createdAt: "2024-06-10",
            updatedAt: "2024-06-11",
            views: 1247,
            likes: 89,
            comments: 23,
            tags: ["kỹ thuật", "thở sâu", "kiểm soát"],
        },
        {
            id: 2,
            title: "Tại Sao Tuần Đầu Cai Thuốc Là Khó Khăn Nhất?",
            content: "Tuần đầu tiên khi cai thuốc thường là thời gian khó khăn nhất...",
            excerpt: "Hiểu rõ những thay đổi trong cơ thể và tâm lý trong tuần đầu cai thuốc",
            status: "pending",
            createdAt: "2024-06-12",
            views: 0,
            likes: 0,
            comments: 0,
            tags: ["tuần đầu", "khó khăn", "tâm lý"],
        },
        {
            id: 3,
            title: "Cách Xây Dựng Thói Quen Tích Cực Thay Thế Hút Thuốc",
            content: "Thay vì hút thuốc, bạn có thể xây dựng những thói quen tích cực khác...",
            excerpt: "Khám phá những hoạt động thay thế giúp bạn quên đi thói quen hút thuốc",
            status: "draft",
            createdAt: "2024-06-13",
            views: 0,
            likes: 0,
            comments: 0,
            tags: ["thói quen", "thay thế", "tích cực"],
        },
        {
            id: 4,
            title: "Vai Trò Của Gia Đình Trong Quá Trình Cai Thuốc",
            content: "Sự hỗ trợ từ gia đình đóng vai trò quan trọng trong việc cai thuốc thành công...",
            excerpt: "Tầm quan trọng của sự hỗ trợ từ người thân trong hành trình cai thuốc",
            status: "published",
            createdAt: "2024-06-08",
            views: 892,
            likes: 67,
            comments: 15,
            tags: ["gia đình", "hỗ trợ", "thành công"],
        },
    ]

    const comments: Comment[] = [
        {
            id: 1,
            blogId: 1,
            author: "Nguyễn Văn An",
            content: "Bài viết rất hữu ích! Em đã áp dụng kỹ thuật thở 4-7-8 và thấy hiệu quả rõ rệt.",
            createdAt: "2024-06-11",
            avatar: "/placeholder.svg?height=32&width=32",
        },
        {
            id: 2,
            blogId: 1,
            author: "Trần Thị Bình",
            content: "Cảm ơn coach đã chia sẻ. Em sẽ thử áp dụng những kỹ thuật này.",
            createdAt: "2024-06-11",
            avatar: "/placeholder.svg?height=32&width=32",
        },
        {
            id: 3,
            blogId: 4,
            author: "Lê Văn Cường",
            content: "Gia đình em đã hỗ trợ rất nhiều. Bài viết này giúp em hiểu thêm về tầm quan trọng của sự hỗ trợ đó.",
            createdAt: "2024-06-09",
            avatar: "/placeholder.svg?height=32&width=32",
        },
    ]

    const filteredPosts = blogPosts.filter((post) => {
        const matchesSearch =
            post.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            post.content.toLowerCase().includes(searchTerm.toLowerCase())
        const matchesFilter = filterStatus === "all" || post.status === filterStatus
        return matchesSearch && matchesFilter
    })

    const getStatusColor = (status: string) => {
        switch (status) {
            case "published":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "pending":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "draft":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            case "rejected":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            default:
                return "bg-gray-100 text-gray-800"
        }
    }

    const getStatusLabel = (status: string) => {
        switch (status) {
            case "published":
                return "Đã xuất bản"
            case "pending":
                return "Chờ duyệt"
            case "draft":
                return "Bản nháp"
            case "rejected":
                return "Bị từ chối"
            default:
                return status
        }
    }

    const handleCreatePost = () => {
        console.log("Creating post:", newPost)
        setNewPost({ title: "", content: "", tags: "" })
        setIsCreateDialogOpen(false)
    }

    const handleEditPost = (post: BlogPost) => {
        setSelectedPost(post)
        setNewPost({
            title: post.title,
            content: post.content,
            tags: post.tags.join(", "),
        })
        setIsEditDialogOpen(true)
    }

    const handleUpdatePost = () => {
        console.log("Updating post:", selectedPost?.id, newPost)
        setNewPost({ title: "", content: "", tags: "" })
        setSelectedPost(null)
        setIsEditDialogOpen(false)
    }

    const handleDeletePost = (postId: number) => {
        if (confirm("Bạn có chắc chắn muốn xóa bài viết này?")) {
            console.log("Deleting post:", postId)
        }
    }

    return (
        <div className="space-y-6">
            {/* Blog Stats */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-blue-100 dark:bg-blue-900/20 rounded-lg flex items-center justify-center">
                                <BookOpen className="w-5 h-5 text-blue-600 dark:text-blue-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">{blogPosts.length}</p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Tổng bài viết</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-green-100 dark:bg-green-900/20 rounded-lg flex items-center justify-center">
                                <CheckCircle className="w-5 h-5 text-green-600 dark:text-green-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">
                                    {blogPosts.filter((p) => p.status === "published").length}
                                </p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Đã xuất bản</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-yellow-100 dark:bg-yellow-900/20 rounded-lg flex items-center justify-center">
                                <Clock className="w-5 h-5 text-yellow-600 dark:text-yellow-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">
                                    {blogPosts.filter((p) => p.status === "pending").length}
                                </p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Chờ duyệt</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardContent className="p-4">
                        <div className="flex items-center space-x-3">
                            <div className="w-10 h-10 bg-purple-100 dark:bg-purple-900/20 rounded-lg flex items-center justify-center">
                                <TrendingUp className="w-5 h-5 text-purple-600 dark:text-purple-400" />
                            </div>
                            <div>
                                <p className="text-2xl font-bold text-slate-900 dark:text-white">
                                    {blogPosts.reduce((sum, post) => sum + post.views, 0)}
                                </p>
                                <p className="text-sm text-slate-600 dark:text-slate-400">Tổng lượt xem</p>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Blog Management */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                <CardHeader>
                    <div className="flex items-center justify-between">
                        <CardTitle className="text-slate-900 dark:text-white">Quản Lý Blog</CardTitle>
                        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
                            <DialogTrigger asChild>
                                <Button>
                                    <Plus className="w-4 h-4 mr-2" />
                                    Tạo Bài Viết
                                </Button>
                            </DialogTrigger>
                            <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                                <DialogHeader>
                                    <DialogTitle>Tạo Bài Viết Mới</DialogTitle>
                                </DialogHeader>
                                <div className="space-y-4">
                                    <div>
                                        <label className="text-sm font-medium">Tiêu đề</label>
                                        <Input
                                            value={newPost.title}
                                            onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
                                            placeholder="Nhập tiêu đề bài viết..."
                                        />
                                    </div>
                                    <div>
                                        <label className="text-sm font-medium">Nội dung</label>
                                        <Textarea
                                            value={newPost.content}
                                            onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
                                            placeholder="Nhập nội dung bài viết..."
                                            rows={10}
                                        />
                                    </div>
                                    <div>
                                        <label className="text-sm font-medium">Tags (phân cách bằng dấu phẩy)</label>
                                        <Input
                                            value={newPost.tags}
                                            onChange={(e) => setNewPost({ ...newPost, tags: e.target.value })}
                                            placeholder="kỹ thuật, thở sâu, kiểm soát..."
                                        />
                                    </div>
                                    <div className="flex space-x-2">
                                        <Button onClick={handleCreatePost} className="flex-1">
                                            Tạo Bài Viết
                                        </Button>
                                        <Button onClick={() => setIsCreateDialogOpen(false)} variant="outline" className="flex-1">
                                            Hủy
                                        </Button>
                                    </div>
                                </div>
                            </DialogContent>
                        </Dialog>
                    </div>
                    <div className="flex flex-col md:flex-row gap-4 mt-4">
                        <div className="flex-1 relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-4 h-4" />
                            <Input
                                placeholder="Tìm kiếm bài viết..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="pl-10"
                            />
                        </div>
                        <div className="flex gap-2">
                            <Button
                                variant={filterStatus === "all" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("all")}
                            >
                                Tất cả
                            </Button>
                            <Button
                                variant={filterStatus === "published" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("published")}
                            >
                                Đã xuất bản
                            </Button>
                            <Button
                                variant={filterStatus === "pending" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("pending")}
                            >
                                Chờ duyệt
                            </Button>
                            <Button
                                variant={filterStatus === "draft" ? "default" : "outline"}
                                size="sm"
                                onClick={() => setFilterStatus("draft")}
                            >
                                Bản nháp
                            </Button>
                        </div>
                    </div>
                </CardHeader>
                <CardContent>
                    <Tabs defaultValue="list" className="w-full">
                        <TabsList className="grid w-full grid-cols-2">
                            <TabsTrigger value="list">Danh Sách Bài Viết</TabsTrigger>
                            <TabsTrigger value="comments">Bình Luận</TabsTrigger>
                        </TabsList>

                        <TabsContent value="list" className="space-y-4">
                            <div className="space-y-4">
                                {filteredPosts.map((post) => (
                                    <div
                                        key={post.id}
                                        className="p-4 border rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50 transition-colors"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex-1">
                                                <div className="flex items-center space-x-2 mb-2">
                                                    <h3 className="font-semibold text-slate-900 dark:text-white">{post.title}</h3>
                                                    <Badge className={getStatusColor(post.status)}>{getStatusLabel(post.status)}</Badge>
                                                </div>
                                                <p className="text-sm text-slate-600 dark:text-slate-400 mb-3">{post.excerpt}</p>
                                                <div className="flex items-center space-x-4 text-sm text-slate-500">
                                                    <span>Tạo: {post.createdAt}</span>
                                                    {post.updatedAt && <span>Cập nhật: {post.updatedAt}</span>}
                                                    <div className="flex items-center space-x-1">
                                                        <Eye className="w-4 h-4" />
                                                        <span>{post.views}</span>
                                                    </div>
                                                    <div className="flex items-center space-x-1">
                                                        <Heart className="w-4 h-4" />
                                                        <span>{post.likes}</span>
                                                    </div>
                                                    <div className="flex items-center space-x-1">
                                                        <MessageSquare className="w-4 h-4" />
                                                        <span>{post.comments}</span>
                                                    </div>
                                                </div>
                                                <div className="flex flex-wrap gap-1 mt-2">
                                                    {post.tags.map((tag, index) => (
                                                        <Badge key={index} variant="outline" className="text-xs">
                                                            {tag}
                                                        </Badge>
                                                    ))}
                                                </div>
                                            </div>
                                            <div className="flex space-x-2 ml-4">
                                                <Button size="sm" variant="outline">
                                                    <Eye className="w-4 h-4" />
                                                </Button>
                                                <Button size="sm" variant="outline" onClick={() => handleEditPost(post)}>
                                                    <Edit className="w-4 h-4" />
                                                </Button>
                                                <Button size="sm" variant="outline" onClick={() => handleDeletePost(post.id)}>
                                                    <Trash2 className="w-4 h-4" />
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </TabsContent>

                        <TabsContent value="comments" className="space-y-4">
                            <div className="space-y-4">
                                {comments.map((comment) => {
                                    const post = blogPosts.find((p) => p.id === comment.blogId)
                                    return (
                                        <div
                                            key={comment.id}
                                            className="p-4 border rounded-lg hover:bg-slate-50 dark:hover:bg-slate-700/50"
                                        >
                                            <div className="flex items-start space-x-3">
                                                <Avatar className="h-8 w-8">
                                                    <AvatarImage src={comment.avatar || "/placeholder.svg"} />
                                                    <AvatarFallback>{comment.author.charAt(0)}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center justify-between mb-1">
                                                        <p className="font-medium text-sm">{comment.author}</p>
                                                        <span className="text-xs text-slate-500">{comment.createdAt}</span>
                                                    </div>
                                                    <p className="text-sm text-slate-600 dark:text-slate-400 mb-2">{comment.content}</p>
                                                    <p className="text-xs text-slate-500">
                                                        Bài viết: <span className="font-medium">{post?.title}</span>
                                                    </p>
                                                </div>
                                                <Button size="sm" variant="outline">
                                                    Trả lời
                                                </Button>
                                            </div>
                                        </div>
                                    )
                                })}
                            </div>
                        </TabsContent>
                    </Tabs>
                </CardContent>
            </Card>

            {/* Edit Post Dialog */}
            <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
                <DialogContent className="max-w-2xl max-h-[80vh] overflow-y-auto">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Bài Viết</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-4">
                        <div>
                            <label className="text-sm font-medium">Tiêu đề</label>
                            <Input
                                value={newPost.title}
                                onChange={(e) => setNewPost({ ...newPost, title: e.target.value })}
                                placeholder="Nhập tiêu đề bài viết..."
                            />
                        </div>
                        <div>
                            <label className="text-sm font-medium">Nội dung</label>
                            <Textarea
                                value={newPost.content}
                                onChange={(e) => setNewPost({ ...newPost, content: e.target.value })}
                                placeholder="Nhập nội dung bài viết..."
                                rows={10}
                            />
                        </div>
                        <div>
                            <label className="text-sm font-medium">Tags (phân cách bằng dấu phẩy)</label>
                            <Input
                                value={newPost.tags}
                                onChange={(e) => setNewPost({ ...newPost, tags: e.target.value })}
                                placeholder="kỹ thuật, thở sâu, kiểm soát..."
                            />
                        </div>
                        <div className="flex space-x-2">
                            <Button onClick={handleUpdatePost} className="flex-1">
                                Cập Nhật Bài Viết
                            </Button>
                            <Button onClick={() => setIsEditDialogOpen(false)} variant="outline" className="flex-1">
                                Hủy
                            </Button>
                        </div>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
