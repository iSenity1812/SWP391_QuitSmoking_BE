"use client"

import type React from "react"
import { useState } from "react"
import { AnimatedSection } from "@/components/ui/AnimatedSection"
import type { BlogPost, BlogUser, Comment, BlogFormData, ReportFormData } from "./types/blog-types"
import { getRootComments, getPublishingStatus } from "./utils/blog-utils"
import { sampleBlogPosts, sampleComments } from "./data/blog-data"

// Components
import BlogHeader from "./components/BlogHeader"
import BlogPostList from "./components/BlogPostList"
import BlogPostDetail from "./components/BlogPostDetail"
import UserAuthSection from "./components/UserAuthSection"

// Dialogs
import LoginPromptDialog from "./dialogs/LoginPromptDialog"
import BlogFormDialog from "./dialogs/BlogFormDialog"
import ReportDialog from "./dialogs/ReportDialog"
import DeleteConfirmDialog from "./dialogs/DeleteConfirmDialog"

const BlogPage: React.FC = () => {
    // State
    const [searchTerm, setSearchTerm] = useState("")
    const [selectedPost, setSelectedPost] = useState<BlogPost | null>(null)
    const [blogPosts, setBlogPosts] = useState<BlogPost[]>(sampleBlogPosts)
    const [comments, setComments] = useState<Comment[]>(sampleComments)

    // User state (null for guest)
    const [currentUser, setCurrentUser] = useState<BlogUser | null>(null)

    // Dialog states
    const [isLoginPromptOpen, setIsLoginPromptOpen] = useState(false)
    const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false)
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
    const [isReportDialogOpen, setIsReportDialogOpen] = useState(false)
    const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState(false)

    // Temporary state for editing/deleting/reporting
    const [editingPost, setEditingPost] = useState<BlogPost | null>(null)
    const [reportingPost, setReportingPost] = useState<BlogPost | null>(null)
    const [deletingPost, setDeletingPost] = useState<BlogPost | null>(null)

    // Filter posts based on search term and published status
    const filteredPosts = blogPosts.filter((post) => {
        const isPublished = post.Status === "Published"
        const matchesSearch =
            post.Title.toLowerCase().includes(searchTerm.toLowerCase()) ||
            post.Content.toLowerCase().includes(searchTerm.toLowerCase())
        return isPublished && matchesSearch
    })

    // Handlers
    const handleViewPost = (post: BlogPost) => {
        setSelectedPost(post)
    }

    const handleBackToList = () => {
        setSelectedPost(null)
        // Scroll to top when going back to blog list
        window.scrollTo(0, 0)
    }

    const handleCreateBlogClick = () => {
        if (!currentUser) {
            setIsLoginPromptOpen(true)
            return
        }

        // Content Admin cannot create blogs
        if (currentUser.role === "Content Admin") {
            alert("Content Admin không có quyền tạo bài viết.")
            return
        }

        setIsCreateDialogOpen(true)
    }

    const handleCreateBlog = (formData: BlogFormData) => {
        if (!currentUser) {
            setIsLoginPromptOpen(true)
            return
        }

        const newBlog: BlogPost = {
            BlogID: Math.max(...blogPosts.map((p) => p.BlogID)) + 1,
            AuthorID: currentUser.id,
            AuthorRole: currentUser.role,
            Title: formData.title,
            Content: formData.content,
            CreatedAt: new Date().toISOString(),
            Status: getPublishingStatus(currentUser.role),
            UserHasLiked: false,
        }

        setBlogPosts((prev) => [newBlog, ...prev])
        setIsCreateDialogOpen(false)

        // Show success message
        const successMessage =
            currentUser.role === "Coach"
                ? "Bài viết đã được tạo thành công! Bài viết đang chờ phê duyệt."
                : "Bài viết đã được tạo và xuất bản thành công!"

        alert(successMessage)
    }

    const handleEditPost = (post: BlogPost) => {
        if (!canEditPost(post)) return

        setEditingPost(post)
        setIsEditDialogOpen(true)
    }

    const handleUpdateBlog = (formData: BlogFormData) => {
        if (!editingPost || !currentUser) return

        setBlogPosts((prev) =>
            prev.map((post) =>
                post.BlogID === editingPost.BlogID
                    ? {
                        ...post,
                        Title: formData.title,
                        Content: formData.content,
                        LastUpdated: new Date().toISOString(),
                    }
                    : post,
            ),
        )

        // Update selected post if it's the one being edited
        if (selectedPost?.BlogID === editingPost.BlogID) {
            setSelectedPost({
                ...editingPost,
                Title: formData.title,
                Content: formData.content,
                LastUpdated: new Date().toISOString(),
            })
        }

        setEditingPost(null)
        setIsEditDialogOpen(false)
        alert("Bài viết đã được cập nhật thành công!")
    }

    const handleDeletePost = (post: BlogPost) => {
        if (!canDeletePost(post)) return

        setDeletingPost(post)
        setIsDeleteConfirmOpen(true)
    }

    const confirmDeletePost = () => {
        if (!deletingPost) return

        setBlogPosts((prev) => prev.filter((post) => post.BlogID !== deletingPost.BlogID))

        // If we're viewing the post that's being deleted, go back to the list
        if (selectedPost?.BlogID === deletingPost.BlogID) {
            setSelectedPost(null)
        }

        setDeletingPost(null)
        setIsDeleteConfirmOpen(false)
        alert("Bài viết đã được xóa thành công!")
    }

    const handleReportPost = (post: BlogPost) => {
        if (!canReportPost(post)) return

        setReportingPost(post)
        setIsReportDialogOpen(true)
    }

    const handleSubmitReport = (reportData: ReportFormData) => {
        if (!reportingPost || !currentUser) {
            setIsLoginPromptOpen(true)
            return
        }

        // In a real app, we would send this report to the server
        console.log("Report submitted:", {
            reportedBlogId: reportingPost.BlogID,
            reportedBy: currentUser.id,
            ...reportData,
        })

        setReportingPost(null)
        setIsReportDialogOpen(false)
        alert("Báo cáo đã được gửi thành công! Đội ngũ quản trị sẽ xem xét báo cáo của bạn.")
    }

    const handleAddComment = (blogId: number, content: string, parentCommentId?: number) => {
        if (!currentUser) {
            setIsLoginPromptOpen(true)
            return
        }

        const newComment: Comment = {
            CommentID: Math.max(...comments.map((c) => c.CommentID), 0) + 1,
            BlogID: blogId,
            UserID: currentUser.id,
            ParentCommentID: parentCommentId,
            Content: content,
            CommentDate: new Date().toISOString(),
        }

        setComments((prev) => [...prev, newComment])
    }

    const handleDemoLogin = (role: "Normal member" | "Premium member" | "Coach" | "Content Admin") => {
        const demoUsers = {
            "Normal member": { id: "user-normal", name: "Nguyễn Văn A", role: "Normal member" as const },
            "Premium member": { id: "user-premium", name: "Trần Thị B", role: "Premium member" as const },
            Coach: { id: "user-coach", name: "Lê Văn C", role: "Coach" as const },
            "Content Admin": { id: "user-admin", name: "Admin", role: "Content Admin" as const },
        }

        setCurrentUser(demoUsers[role])
        setIsLoginPromptOpen(false)
    }

    // Permission checks
    const canEditPost = (post: BlogPost) => {
        if (!currentUser) return false
        return (
            currentUser.id === post.AuthorID || // Author can edit their own posts
            currentUser.role === "Content Admin" // Admin can edit any post
        )
    }

    const canDeletePost = (post: BlogPost) => {
        if (!currentUser) return false
        return (
            currentUser.id === post.AuthorID || // Author can delete their own posts
            currentUser.role === "Content Admin" // Admin can delete any post
        )
    }

    const canReportPost = (post: BlogPost) => {
        if (!currentUser) return false
        return currentUser.id !== post.AuthorID // Users can't report their own posts
    }

    return (
        <div className="min-h-screen bg-slate-50 dark:bg-slate-900">
            <BlogHeader searchTerm={searchTerm} setSearchTerm={setSearchTerm} />

            <main className="container mx-auto px-6 pb-20">
                <AnimatedSection animation="fadeUp" delay={300}>
                    <UserAuthSection currentUser={currentUser} handleCreateBlogClick={handleCreateBlogClick} />

                    {selectedPost ? (
                        <BlogPostDetail
                            post={selectedPost}
                            currentUser={currentUser}
                            comments={comments}
                            handleBackToList={handleBackToList}
                            handleEditPost={handleEditPost}
                            handleDeletePost={handleDeletePost}
                            handleReportPost={handleReportPost}
                            canEditPost={canEditPost}
                            canDeletePost={canDeletePost}
                            canReportPost={canReportPost}
                            handleAddComment={handleAddComment}
                            setIsLoginPromptOpen={setIsLoginPromptOpen}
                        />
                    ) : (
                        <BlogPostList
                            posts={filteredPosts}
                            currentUser={currentUser}
                            comments={comments}
                            handleViewPost={handleViewPost}
                            handleEditPost={handleEditPost}
                            handleDeletePost={handleDeletePost}
                            handleReportPost={handleReportPost}
                            canEditPost={canEditPost}
                            canDeletePost={canDeletePost}
                            canReportPost={canReportPost}
                            getRootComments={(blogId) => getRootComments(comments, blogId)}
                        />
                    )}
                </AnimatedSection>
            </main>

            {/* Dialogs */}
            <LoginPromptDialog
                isOpen={isLoginPromptOpen}
                onClose={() => setIsLoginPromptOpen(false)}
                handleDemoLogin={handleDemoLogin}
            />

            <BlogFormDialog
                isOpen={isCreateDialogOpen}
                onClose={() => setIsCreateDialogOpen(false)}
                onSubmit={handleCreateBlog}
                currentUserRole={currentUser?.role}
            />

            <BlogFormDialog
                isOpen={isEditDialogOpen}
                onClose={() => {
                    setIsEditDialogOpen(false)
                    setEditingPost(null)
                }}
                onSubmit={handleUpdateBlog}
                initialData={
                    editingPost
                        ? {
                            title: editingPost.Title,
                            content: editingPost.Content,
                        }
                        : undefined
                }
                isEdit={true}
            />

            <ReportDialog
                isOpen={isReportDialogOpen}
                onClose={() => {
                    setIsReportDialogOpen(false)
                    setReportingPost(null)
                }}
                onSubmit={handleSubmitReport}
                postTitle={reportingPost?.Title || ""}
                postAuthor={reportingPost?.AuthorID || ""}
            />

            <DeleteConfirmDialog
                isOpen={isDeleteConfirmOpen}
                onClose={() => {
                    setIsDeleteConfirmOpen(false)
                    setDeletingPost(null)
                }}
                onConfirm={confirmDeletePost}
                postTitle={deletingPost?.Title || ""}
            />
        </div>
    )
}

export default BlogPage
