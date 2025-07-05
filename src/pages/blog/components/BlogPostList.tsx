import type React from "react"
import { Card, CardContent } from "@/components/ui/card"
import type { BlogPost, BlogUser, Comment } from "../types/blog-types"
import BlogPostCard from "./BlogPostCard"

interface BlogPostListProps {
    posts: BlogPost[]
    currentUser: BlogUser | null
    comments: Comment[]
    handleViewPost: (post: BlogPost) => void
    handleEditPost: (post: BlogPost) => void
    handleDeletePost: (post: BlogPost) => void
    handleReportPost: (post: BlogPost) => void
    canEditPost: (post: BlogPost) => boolean
    canDeletePost: (post: BlogPost) => boolean
    canReportPost: (post: BlogPost) => boolean
    getRootComments: (blogId: number) => Comment[]
}

const BlogPostList: React.FC<BlogPostListProps> = ({
    posts,
    currentUser,
    handleViewPost,
    handleEditPost,
    handleDeletePost,
    handleReportPost,
    canEditPost,
    canDeletePost,
    canReportPost,
    getRootComments,
}) => {
    return (
        <div className="space-y-6">
            <h2 className="text-2xl font-bold text-slate-800 dark:text-white mb-6 flex items-center gap-2">
                <span className="w-2 h-8 bg-gradient-to-b from-emerald-500 to-emerald-600 rounded-full"></span>
                Bài viết ({posts.length})
            </h2>

            {posts.length === 0 ? (
                <Card className="text-center py-12">
                    <CardContent>
                        <p className="text-slate-500 dark:text-slate-400">Không tìm thấy bài viết nào.</p>
                    </CardContent>
                </Card>
            ) : (
                <div className="grid gap-6">
                    {posts.map((post, index) => (
                        <BlogPostCard
                            key={post.BlogID}
                            post={post}
                            index={index}
                            currentUser={currentUser}
                            handleViewPost={handleViewPost}
                            handleEditPost={handleEditPost}
                            handleDeletePost={handleDeletePost}
                            handleReportPost={handleReportPost}
                            canEditPost={canEditPost}
                            canDeletePost={canDeletePost}
                            canReportPost={canReportPost}
                            getRootComments={getRootComments}
                        />
                    ))}
                </div>
            )}
        </div>
    )
}

export default BlogPostList
