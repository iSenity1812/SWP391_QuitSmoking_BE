"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Calendar, Clock, MessageCircle, ArrowRight, MoreVertical, Edit, Trash2, Flag } from "lucide-react"
import { Card, CardHeader, CardTitle, CardDescription, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import type { BlogPost, BlogUser, Comment } from "../types/blog-types"
import { formatDate } from "../utils/blog-utils"
import { getRoleIcon, getStatusBadge, getRoleBadge } from "./UserBadges"

interface BlogPostCardProps {
    post: BlogPost
    index: number
    currentUser: BlogUser | null
    handleViewPost: (post: BlogPost) => void
    handleEditPost: (post: BlogPost) => void
    handleDeletePost: (post: BlogPost) => void
    handleReportPost: (post: BlogPost) => void
    canEditPost: (post: BlogPost) => boolean
    canDeletePost: (post: BlogPost) => boolean
    canReportPost: (post: BlogPost) => boolean
    getRootComments: (blogId: number) => Comment[]
}

const BlogPostCard: React.FC<BlogPostCardProps> = ({
    post,
    index,
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
        <motion.div initial={{ y: 20, opacity: 0 }} animate={{ y: 0, opacity: 1 }} transition={{ delay: index * 0.1 }}>
            <Card className="group overflow-hidden border-2 border-slate-200 dark:border-slate-700 hover:border-emerald-300 dark:hover:border-emerald-500 transition-all duration-300 hover:shadow-xl bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                <CardHeader>
                    <div className="flex items-center justify-between mb-2">
                        <div className="flex items-center gap-2">
                            {getStatusBadge(post.Status)}
                            {getRoleBadge(post.AuthorRole)}
                        </div>
                        <div className="flex items-center gap-2">
                            {getRoleIcon(post.AuthorRole)}
                            <span className="text-sm text-slate-500 dark:text-slate-400">{post.AuthorID}</span>

                            {/* Action Menu for List View */}
                            {currentUser && (canEditPost(post) || canDeletePost(post) || canReportPost(post)) && (
                                <DropdownMenu>
                                    <DropdownMenuTrigger asChild>
                                        <Button variant="ghost" size="sm">
                                            <MoreVertical className="w-4 h-4" />
                                        </Button>
                                    </DropdownMenuTrigger>
                                    <DropdownMenuContent align="end">
                                        {canEditPost(post) && (
                                            <DropdownMenuItem onClick={() => handleEditPost(post)}>
                                                <Edit className="w-4 h-4 mr-2" />
                                                Chỉnh sửa
                                            </DropdownMenuItem>
                                        )}
                                        {canDeletePost(post) && (
                                            <DropdownMenuItem
                                                onClick={() => handleDeletePost(post)}
                                                className="text-red-600 focus:text-red-600"
                                            >
                                                <Trash2 className="w-4 h-4 mr-2" />
                                                Xóa
                                            </DropdownMenuItem>
                                        )}
                                        {canReportPost(post) && (
                                            <>
                                                {(canEditPost(post) || canDeletePost(post)) && <DropdownMenuSeparator />}
                                                <DropdownMenuItem
                                                    onClick={() => handleReportPost(post)}
                                                    className="text-orange-600 focus:text-orange-600"
                                                >
                                                    <Flag className="w-4 h-4 mr-2" />
                                                    Báo cáo
                                                </DropdownMenuItem>
                                            </>
                                        )}
                                    </DropdownMenuContent>
                                </DropdownMenu>
                            )}
                        </div>
                    </div>
                    <CardTitle className="text-xl font-bold text-slate-800 dark:text-white group-hover:text-emerald-600 dark:group-hover:text-emerald-400 transition-colors">
                        {post.Title}
                    </CardTitle>
                    <CardDescription className="text-slate-600 dark:text-slate-300 line-clamp-2">
                        {post.Content.substring(0, 150)}...
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <div className="flex items-center justify-between text-sm text-slate-500 dark:text-slate-400 mb-4">
                        <div className="flex items-center gap-4">
                            <span className="flex items-center gap-1">
                                <Calendar className="w-4 h-4" />
                                {formatDate(post.CreatedAt)}
                            </span>
                            {post.LastUpdated && (
                                <span className="flex items-center gap-1">
                                    <Clock className="w-4 h-4" />
                                    Cập nhật: {formatDate(post.LastUpdated)}
                                </span>
                            )}
                        </div>
                    </div>
                    <div className="flex items-center justify-between">
                        <div className="flex items-center gap-4 text-sm text-slate-500 dark:text-slate-400">
                            <span className="flex items-center gap-1">
                                <MessageCircle className="w-4 h-4" />
                                {getRootComments(post.BlogID).length} bình luận
                            </span>
                        </div>
                        <Button
                            variant="ghost"
                            size="sm"
                            className="text-emerald-600 dark:text-emerald-400 hover:text-emerald-700 dark:hover:text-emerald-300 font-semibold"
                            onClick={() => handleViewPost(post)}
                        >
                            Đọc thêm
                            <ArrowRight className="w-4 h-4 ml-1" />
                        </Button>
                    </div>
                </CardContent>
            </Card>
        </motion.div>
    )
}

export default BlogPostCard
