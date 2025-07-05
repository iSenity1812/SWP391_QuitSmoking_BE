"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import {
    MessageSquare,
    Eye,
    Edit,
    Trash2,
    CheckCircle,
    XCircle,
    Search,
    Filter,
    Calendar,
    User,
    ThumbsUp,
    MessageCircle,
    Plus,
} from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"

interface BlogPost {
    id: number
    title: string
    content: string
    excerpt: string
    author: string
    authorId: string
    authorAvatar?: string
    authorRole: "user" | "coach" | "admin"
    createdAt: string
    status: "draft" | "published" | "pending" | "rejected"
    category: string
    views: number
    likes: number
    comments: number
    reportCount: number
}

export function BlogManagement() {
    const [blogPosts, setBlogPosts] = useState<BlogPost[]>([
        {
            id: 1,
            title: "Hành trình 100 ngày cai thuốc của tôi",
            content: "Chia sẻ kinh nghiệm cá nhân về quá trình cai thuốc lá...",
            excerpt: "Câu chuyện thật về những thử thách và chiến thắng trong hành trình cai thuốc",
            author: "Nguyễn Văn A",
            authorId: "user1",
            authorAvatar: "/placeholder.svg?height=40&width=40",
            authorRole: "user",
            createdAt: "2024-01-15T10:30:00Z",
            status: "published",
            category: "Kinh nghiệm cá nhân",
            views: 1250,
            likes: 89,
            comments: 23,
            reportCount: 0,
        },
        {
            id: 2,
            title: "5 Kỹ thuật thở giúp vượt qua cơn thèm thuốc",
            content: "Hướng dẫn chi tiết các kỹ thuật thở hiệu quả...",
            excerpt: "Các phương pháp thở được chứng minh khoa học",
            author: "Dr. Nguyễn Văn C",
            authorId: "coach1",
            authorRole: "coach",
            createdAt: "2024-01-16T09:00:00Z",
            status: "pending",
            category: "Kỹ thuật",
            views: 0,
            likes: 0,
            comments: 0,
            reportCount: 0,
        },
    ])

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [selectedBlog, setSelectedBlog] = useState<BlogPost | null>(null)
    const [formData, setFormData] = useState({
        title: "",
        content: "",
        excerpt: "",
        category: "",
    })

    const handleCreateBlog = () => {
        const newBlog: BlogPost = {
            id: Math.max(...blogPosts.map((blog) => blog.id)) + 1,
            title: formData.title,
            content: formData.content,
            excerpt: formData.excerpt,
            author: "Admin",
            authorId: "admin1",
            authorRole: "admin",
            createdAt: new Date().toISOString(),
            status: "published",
            category: formData.category,
            views: 0,
            likes: 0,
            comments: 0,
            reportCount: 0,
        }
        setBlogPosts((prev) => [newBlog, ...prev])
        setIsCreateModalOpen(false)
        resetForm()
    }

    const handleEditBlog = (blog: BlogPost) => {
        setSelectedBlog(blog)
        setFormData({
            title: blog.title,
            content: blog.content,
            excerpt: blog.excerpt,
            category: blog.category,
        })
        setIsEditModalOpen(true)
    }

    const handleUpdateBlog = () => {
        if (!selectedBlog) return
        setBlogPosts((prev) => prev.map((blog) => (blog.id === selectedBlog.id ? { ...blog, ...formData } : blog)))
        setIsEditModalOpen(false)
        resetForm()
    }

    const resetForm = () => {
        setFormData({
            title: "",
            content: "",
            excerpt: "",
            category: "",
        })
        setSelectedBlog(null)
    }

    const handleDeleteBlog = (blogId: number) => {
        setBlogPosts((prev) => prev.filter((blog) => blog.id !== blogId))
    }

    const handleApproveBlog = (blogId: number) => {
        setBlogPosts((prev) => prev.map((blog) => (blog.id === blogId ? { ...blog, status: "published" as const } : blog)))
    }

    const handleRejectBlog = (blogId: number) => {
        setBlogPosts((prev) => prev.map((blog) => (blog.id === blogId ? { ...blog, status: "rejected" as const } : blog)))
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "published":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "pending":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "rejected":
                return "bg-red-100 text-red-800 dark:bg-red-900/20 dark:text-red-400"
            case "draft":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getRoleColor = (role: string) => {
        switch (role) {
            case "coach":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "admin":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const userBlogs = blogPosts.filter((blog) => blog.authorRole === "user")
    const coachBlogs = blogPosts.filter((blog) => blog.authorRole === "coach")

    return (
        <div className="space-y-6">
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <CardTitle className="text-slate-900 dark:text-white">Quản Lý Blog & Bài Viết</CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-400">
                        Quản lý blog của người dùng và coach, phê duyệt nội dung
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <Tabs defaultValue="user-blogs" className="space-y-6">
                        <TabsList className="grid w-full grid-cols-2">
                            <TabsTrigger value="user-blogs" className="flex items-center space-x-2">
                                <MessageSquare className="w-4 h-4" />
                                <span>Blog Người Dùng</span>
                            </TabsTrigger>
                            <TabsTrigger value="coach-blogs" className="flex items-center space-x-2">
                                <User className="w-4 h-4" />
                                <span>Blog Coach</span>
                            </TabsTrigger>
                        </TabsList>

                        <TabsContent value="user-blogs" className="space-y-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Blog Người Dùng</h3>
                                <div className="flex space-x-2">
                                    <Button onClick={() => setIsCreateModalOpen(true)}>
                                        <Plus className="w-4 h-4 mr-2" />
                                        Tạo Blog Mới
                                    </Button>
                                    <Button variant="outline" size="sm">
                                        <Filter className="w-4 h-4 mr-2" />
                                        Lọc
                                    </Button>
                                    <Button variant="outline" size="sm">
                                        <Search className="w-4 h-4 mr-2" />
                                        Tìm kiếm
                                    </Button>
                                </div>
                            </div>

                            <div className="space-y-4">
                                {userBlogs.map((blog) => (
                                    <div
                                        key={blog.id}
                                        className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex items-start space-x-4 flex-1">
                                                <Avatar>
                                                    <AvatarImage src={blog.authorAvatar || "/placeholder.svg"} />
                                                    <AvatarFallback>{blog.author.charAt(0)}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center space-x-2 mb-2">
                                                        <h4 className="font-medium text-slate-900 dark:text-white">{blog.title}</h4>
                                                        <Badge className={getStatusColor(blog.status)}>
                                                            {blog.status === "published"
                                                                ? "Đã xuất bản"
                                                                : blog.status === "pending"
                                                                    ? "Chờ duyệt"
                                                                    : blog.status === "rejected"
                                                                        ? "Từ chối"
                                                                        : "Bản nháp"}
                                                        </Badge>
                                                        {blog.reportCount > 0 && <Badge variant="destructive">{blog.reportCount} báo cáo</Badge>}
                                                    </div>
                                                    <p className="text-slate-700 dark:text-slate-300 mb-2">{blog.excerpt}</p>
                                                    <div className="flex items-center space-x-4 text-sm text-slate-500 dark:text-slate-400">
                                                        <span>Bởi {blog.author}</span>
                                                        <Badge className={getRoleColor(blog.authorRole)}>Người dùng</Badge>
                                                        <span className="flex items-center">
                                                            <Eye className="w-3 h-3 mr-1" />
                                                            {blog.views}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <ThumbsUp className="w-3 h-3 mr-1" />
                                                            {blog.likes}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <MessageCircle className="w-3 h-3 mr-1" />
                                                            {blog.comments}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <Calendar className="w-3 h-3 mr-1" />
                                                            {new Date(blog.createdAt).toLocaleDateString("vi-VN")}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="ghost">
                                                    <Eye className="w-4 h-4" />
                                                </Button>
                                                <Button size="sm" variant="ghost" onClick={() => handleEditBlog(blog)}>
                                                    <Edit className="w-4 h-4" />
                                                </Button>
                                                {blog.status === "pending" && (
                                                    <>
                                                        <Button
                                                            size="sm"
                                                            onClick={() => handleApproveBlog(blog.id)}
                                                            className="bg-green-600 hover:bg-green-700"
                                                        >
                                                            <CheckCircle className="w-4 h-4 mr-1" />
                                                            Duyệt
                                                        </Button>
                                                        <Button size="sm" variant="destructive" onClick={() => handleRejectBlog(blog.id)}>
                                                            <XCircle className="w-4 h-4 mr-1" />
                                                            Từ chối
                                                        </Button>
                                                    </>
                                                )}
                                                <Button size="sm" variant="ghost" onClick={() => handleDeleteBlog(blog.id)}>
                                                    <Trash2 className="w-4 h-4" />
                                                </Button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </TabsContent>

                        <TabsContent value="coach-blogs" className="space-y-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-lg font-semibold text-slate-900 dark:text-white">Blog Coach</h3>
                                <div className="flex space-x-2">
                                    <Button variant="outline" size="sm">
                                        <Filter className="w-4 h-4 mr-2" />
                                        Lọc theo coach
                                    </Button>
                                    <Button variant="outline" size="sm">
                                        <Search className="w-4 h-4 mr-2" />
                                        Tìm kiếm
                                    </Button>
                                </div>
                            </div>

                            <div className="space-y-4">
                                {coachBlogs.map((blog) => (
                                    <div
                                        key={blog.id}
                                        className="p-4 rounded-lg bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600"
                                    >
                                        <div className="flex items-start justify-between">
                                            <div className="flex items-start space-x-4 flex-1">
                                                <Avatar>
                                                    <AvatarImage src={blog.authorAvatar || "/placeholder.svg"} />
                                                    <AvatarFallback>{blog.author.charAt(0)}</AvatarFallback>
                                                </Avatar>
                                                <div className="flex-1">
                                                    <div className="flex items-center space-x-2 mb-2">
                                                        <h4 className="font-medium text-slate-900 dark:text-white">{blog.title}</h4>
                                                        <Badge className={getStatusColor(blog.status)}>
                                                            {blog.status === "published"
                                                                ? "Đã duyệt"
                                                                : blog.status === "pending"
                                                                    ? "Chờ duyệt"
                                                                    : "Từ chối"}
                                                        </Badge>
                                                    </div>
                                                    <p className="text-slate-700 dark:text-slate-300 mb-2">{blog.excerpt}</p>
                                                    <div className="flex items-center space-x-4 text-sm text-slate-500 dark:text-slate-400">
                                                        <span>Coach: {blog.author}</span>
                                                        <Badge className={getRoleColor(blog.authorRole)}>Coach</Badge>
                                                        <span className="flex items-center">
                                                            <Eye className="w-3 h-3 mr-1" />
                                                            {blog.views}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <ThumbsUp className="w-3 h-3 mr-1" />
                                                            {blog.likes}
                                                        </span>
                                                        <span className="flex items-center">
                                                            <Calendar className="w-3 h-3 mr-1" />
                                                            {new Date(blog.createdAt).toLocaleDateString("vi-VN")}
                                                        </span>
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex space-x-2">
                                                <Button size="sm" variant="ghost">
                                                    <Eye className="w-4 h-4" />
                                                </Button>
                                                {blog.status === "pending" && (
                                                    <>
                                                        <Button
                                                            size="sm"
                                                            onClick={() => handleApproveBlog(blog.id)}
                                                            className="bg-green-600 hover:bg-green-700"
                                                        >
                                                            <CheckCircle className="w-4 h-4 mr-1" />
                                                            Duyệt
                                                        </Button>
                                                        <Button size="sm" variant="destructive" onClick={() => handleRejectBlog(blog.id)}>
                                                            <XCircle className="w-4 h-4 mr-1" />
                                                            Từ chối
                                                        </Button>
                                                    </>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </TabsContent>
                    </Tabs>
                </CardContent>
            </Card>
            {/* Create Blog Modal */}
            <Dialog open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen}>
                <DialogContent className="sm:max-w-[700px]">
                    <DialogHeader>
                        <DialogTitle>Tạo Blog Mới</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="blog-title">Tiêu đề</Label>
                            <Input
                                id="blog-title"
                                value={formData.title}
                                onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
                                placeholder="Nhập tiêu đề blog..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="blog-category">Danh mục</Label>
                            <Input
                                id="blog-category"
                                value={formData.category}
                                onChange={(e) => setFormData((prev) => ({ ...prev, category: e.target.value }))}
                                placeholder="Ví dụ: Kinh nghiệm cá nhân, Kỹ thuật..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="blog-excerpt">Tóm tắt</Label>
                            <Textarea
                                id="blog-excerpt"
                                value={formData.excerpt}
                                onChange={(e) => setFormData((prev) => ({ ...prev, excerpt: e.target.value }))}
                                placeholder="Nhập tóm tắt ngắn gọn..."
                                className="min-h-[80px]"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="blog-content">Nội dung</Label>
                            <Textarea
                                id="blog-content"
                                value={formData.content}
                                onChange={(e) => setFormData((prev) => ({ ...prev, content: e.target.value }))}
                                placeholder="Nhập nội dung blog..."
                                className="min-h-[200px]"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsCreateModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleCreateBlog} disabled={!formData.title.trim() || !formData.content.trim()}>
                            Tạo Blog
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Edit Blog Modal */}
            <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
                <DialogContent className="sm:max-w-[700px]">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Blog</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="edit-blog-title">Tiêu đề</Label>
                            <Input
                                id="edit-blog-title"
                                value={formData.title}
                                onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
                                placeholder="Nhập tiêu đề blog..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-blog-category">Danh mục</Label>
                            <Input
                                id="edit-blog-category"
                                value={formData.category}
                                onChange={(e) => setFormData((prev) => ({ ...prev, category: e.target.value }))}
                                placeholder="Ví dụ: Kinh nghiệm cá nhân, Kỹ thuật..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-blog-excerpt">Tóm tắt</Label>
                            <Textarea
                                id="edit-blog-excerpt"
                                value={formData.excerpt}
                                onChange={(e) => setFormData((prev) => ({ ...prev, excerpt: e.target.value }))}
                                placeholder="Nhập tóm tắt ngắn gọn..."
                                className="min-h-[80px]"
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-blog-content">Nội dung</Label>
                            <Textarea
                                id="edit-blog-content"
                                value={formData.content}
                                onChange={(e) => setFormData((prev) => ({ ...prev, content: e.target.value }))}
                                placeholder="Nhập nội dung blog..."
                                className="min-h-[200px]"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleUpdateBlog} disabled={!formData.title.trim() || !formData.content.trim()}>
                            Cập Nhật
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    )
}
