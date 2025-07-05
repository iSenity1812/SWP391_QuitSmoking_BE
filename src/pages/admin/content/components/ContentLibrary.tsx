"use client"

import { useState } from "react"
import { motion, AnimatePresence } from "framer-motion"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { FileText, ImageIcon, Eye, Edit, Trash2, Plus, Upload, ThumbsUp } from "lucide-react"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"

interface ContentItem {
    id: number
    title: string
    type: "article" | "image"
    description: string
    url?: string
    thumbnail?: string
    createdBy: string
    createdAt: string
    status: "draft" | "published" | "archived"
    views: number
    likes: number
    category: "motivation" | "tips" | "education" | "success-stories"
}

export function ContentLibrary() {
    const [contentItems, setContentItems] = useState<ContentItem[]>([
        {
            id: 2,
            title: "Câu Chuyện Thành Công: Từ 2 Bao/Ngày Đến Hoàn Toàn Sạch",
            type: "article",
            description: "Chia sẻ hành trình cai thuốc thành công của anh Minh",
            createdBy: "Content Team",
            createdAt: "2024-01-12T14:30:00Z",
            status: "published",
            views: 890,
            likes: 156,
            category: "success-stories",
        },
        {
            id: 3,
            title: "Infographic: Lợi Ích Của Việc Cai Thuốc",
            type: "image",
            description: "Hình ảnh minh họa các lợi ích sức khỏe khi cai thuốc lá",
            thumbnail: "/placeholder.svg?height=200&width=300",
            createdBy: "Design Team",
            createdAt: "2024-01-14T11:15:00Z",
            status: "published",
            views: 654,
            likes: 78,
            category: "education",
        },
    ])

    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false)
    const [isEditModalOpen, setIsEditModalOpen] = useState(false)
    const [selectedItem, setSelectedItem] = useState<ContentItem | null>(null)
    const [formData, setFormData] = useState({
        title: "",
        description: "",
        category: "motivation" as ContentItem["category"],
        type: "article" as ContentItem["type"],
        thumbnail: "",
    })

    const handleCreateContent = () => {
        const newItem: ContentItem = {
            id: Math.max(...contentItems.map((item) => item.id)) + 1,
            title: formData.title,
            description: formData.description,
            type: formData.type,
            category: formData.category,
            thumbnail: formData.type === "image" ? formData.thumbnail : undefined,
            createdBy: "Admin",
            createdAt: new Date().toISOString(),
            status: "draft",
            views: 0,
            likes: 0,
        }
        setContentItems((prev) => [newItem, ...prev])
        setIsCreateModalOpen(false)
        resetForm()
    }

    const handleEditContent = (item: ContentItem) => {
        setSelectedItem(item)
        setFormData({
            title: item.title,
            description: item.description,
            category: item.category,
            type: item.type,
            thumbnail: item.thumbnail || "",
        })
        setIsEditModalOpen(true)
    }

    const handleUpdateContent = () => {
        if (!selectedItem) return
        setContentItems((prev) =>
            prev.map((item) =>
                item.id === selectedItem.id
                    ? { ...item, ...formData, thumbnail: formData.type === "image" ? formData.thumbnail : undefined }
                    : item,
            ),
        )
        setIsEditModalOpen(false)
        resetForm()
    }

    const resetForm = () => {
        setFormData({
            title: "",
            description: "",
            category: "motivation",
            type: "article",
            thumbnail: "",
        })
        setSelectedItem(null)
    }

    const handleDeleteContent = (contentId: number) => {
        setContentItems((prev) => prev.filter((item) => item.id !== contentId))
    }

    const getTypeIcon = (type: string) => {
        switch (type) {
            case "article":
                return <FileText className="w-4 h-4" />
            case "image":
                return <ImageIcon className="w-4 h-4" />
            default:
                return <FileText className="w-4 h-4" />
        }
    }

    const getStatusColor = (status: string) => {
        switch (status) {
            case "published":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            case "draft":
                return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/20 dark:text-yellow-400"
            case "archived":
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const getCategoryColor = (category: string) => {
        switch (category) {
            case "motivation":
                return "bg-purple-100 text-purple-800 dark:bg-purple-900/20 dark:text-purple-400"
            case "tips":
                return "bg-blue-100 text-blue-800 dark:bg-blue-900/20 dark:text-blue-400"
            case "education":
                return "bg-orange-100 text-orange-800 dark:bg-orange-900/20 dark:text-orange-400"
            case "success-stories":
                return "bg-green-100 text-green-800 dark:bg-green-900/20 dark:text-green-400"
            default:
                return "bg-gray-100 text-gray-800 dark:bg-gray-900/20 dark:text-gray-400"
        }
    }

    const containerVariants = {
        hidden: { opacity: 0 },
        visible: {
            opacity: 1,
            transition: {
                staggerChildren: 0.1,
            },
        },
    }

    const cardVariants = {
        hidden: { opacity: 0, y: 30, scale: 0.9 },
        visible: {
            opacity: 1,
            y: 0,
            scale: 1,
            transition: {
                duration: 0.4,
                ease: "easeOut",
            },
        },
    }

    return (
        <motion.div className="space-y-6" initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ duration: 0.3 }}>
            <motion.div initial={{ opacity: 0, y: -20 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }}>
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm">
                    <CardHeader>
                        <div className="flex justify-between items-center">
                            <motion.div initial={{ opacity: 0, x: -20 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: 0.2 }}>
                                <CardTitle className="text-slate-900 dark:text-white">Thư Viện Nội Dung</CardTitle>
                                <CardDescription className="text-slate-600 dark:text-slate-400">
                                    Quản lý bài viết, hình ảnh và tài liệu giáo dục
                                </CardDescription>
                            </motion.div>
                            <motion.div
                                className="flex space-x-2"
                                initial={{ opacity: 0, x: 20 }}
                                animate={{ opacity: 1, x: 0 }}
                                transition={{ delay: 0.3 }}
                            >
                                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                    <Button onClick={() => setIsCreateModalOpen(true)}>
                                        <Plus className="w-4 h-4 mr-2" />
                                        Thêm Nội Dung
                                    </Button>
                                </motion.div>
                                <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                    <Button variant="outline">
                                        <Upload className="w-4 h-4 mr-2" />
                                        Tải Lên
                                    </Button>
                                </motion.div>
                            </motion.div>
                        </div>
                    </CardHeader>
                    <CardContent>
                        <motion.div
                            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
                            variants={containerVariants}
                            initial="hidden"
                            animate="visible"
                        >
                            <AnimatePresence>
                                {contentItems.map((item, index) => (
                                    <motion.div
                                        key={item.id}
                                        variants={cardVariants}
                                        custom={index}
                                        whileHover={{
                                            scale: 1.03,
                                            boxShadow: "0 10px 25px rgba(0,0,0,0.15)",
                                            transition: { duration: 0.2 },
                                        }}
                                        whileTap={{ scale: 0.98 }}
                                        layout
                                    >
                                        <Card className="bg-slate-50 dark:bg-slate-700/50 border border-slate-200 dark:border-slate-600 h-full">
                                            <CardHeader className="pb-3">
                                                <div className="flex items-start justify-between">
                                                    <motion.div
                                                        className="flex items-center space-x-2"
                                                        initial={{ opacity: 0, x: -10 }}
                                                        animate={{ opacity: 1, x: 0 }}
                                                        transition={{ delay: index * 0.1 + 0.3 }}
                                                    >
                                                        <motion.div
                                                            whileHover={{ rotate: 15, scale: 1.2 }}
                                                            transition={{ type: "spring", stiffness: 300 }}
                                                        >
                                                            {getTypeIcon(item.type)}
                                                        </motion.div>
                                                        <motion.div
                                                            whileHover={{ scale: 1.05 }}
                                                            initial={{ opacity: 0, scale: 0.8 }}
                                                            animate={{ opacity: 1, scale: 1 }}
                                                            transition={{ delay: index * 0.1 + 0.4 }}
                                                        >
                                                            <Badge className={getStatusColor(item.status)}>
                                                                {item.status === "published"
                                                                    ? "Đã xuất bản"
                                                                    : item.status === "draft"
                                                                        ? "Bản nháp"
                                                                        : "Lưu trữ"}
                                                            </Badge>
                                                        </motion.div>
                                                    </motion.div>
                                                    <motion.div
                                                        whileHover={{ scale: 1.05 }}
                                                        initial={{ opacity: 0, scale: 0.8 }}
                                                        animate={{ opacity: 1, scale: 1 }}
                                                        transition={{ delay: index * 0.1 + 0.5 }}
                                                    >
                                                        <Badge className={getCategoryColor(item.category)}>
                                                            {item.category === "motivation"
                                                                ? "Động lực"
                                                                : item.category === "tips"
                                                                    ? "Mẹo hay"
                                                                    : item.category === "education"
                                                                        ? "Giáo dục"
                                                                        : "Câu chuyện thành công"}
                                                        </Badge>
                                                    </motion.div>
                                                </div>
                                                <motion.div
                                                    initial={{ opacity: 0, y: 10 }}
                                                    animate={{ opacity: 1, y: 0 }}
                                                    transition={{ delay: index * 0.1 + 0.6 }}
                                                >
                                                    <CardTitle className="text-lg text-slate-900 dark:text-white">{item.title}</CardTitle>
                                                </motion.div>
                                            </CardHeader>
                                            <CardContent>
                                                {item.thumbnail && (
                                                    <motion.div
                                                        className="mb-3"
                                                        initial={{ opacity: 0, scale: 0.9 }}
                                                        animate={{ opacity: 1, scale: 1 }}
                                                        transition={{ delay: index * 0.1 + 0.7 }}
                                                        whileHover={{ scale: 1.05 }}
                                                    >
                                                        <img
                                                            src={item.thumbnail || "/placeholder.svg"}
                                                            alt={item.title}
                                                            className="w-full h-32 object-cover rounded-lg"
                                                        />
                                                    </motion.div>
                                                )}
                                                <motion.p
                                                    className="text-sm text-slate-600 dark:text-slate-400 mb-3"
                                                    initial={{ opacity: 0, y: 10 }}
                                                    animate={{ opacity: 1, y: 0 }}
                                                    transition={{ delay: index * 0.1 + 0.8 }}
                                                >
                                                    {item.description}
                                                </motion.p>
                                                <motion.div
                                                    className="flex items-center justify-between text-sm text-slate-500 dark:text-slate-400 mb-4"
                                                    initial={{ opacity: 0 }}
                                                    animate={{ opacity: 1 }}
                                                    transition={{ delay: index * 0.1 + 0.9 }}
                                                >
                                                    <span>Bởi {item.createdBy}</span>
                                                    <span>{new Date(item.createdAt).toLocaleDateString("vi-VN")}</span>
                                                </motion.div>
                                                <motion.div
                                                    className="flex items-center justify-between text-sm text-slate-500 dark:text-slate-400 mb-4"
                                                    initial={{ opacity: 0 }}
                                                    animate={{ opacity: 1 }}
                                                    transition={{ delay: index * 0.1 + 1.0 }}
                                                >
                                                    <motion.span className="flex items-center" whileHover={{ scale: 1.05, color: "#3b82f6" }}>
                                                        <Eye className="w-3 h-3 mr-1" />
                                                        {item.views} lượt xem
                                                    </motion.span>
                                                    <motion.span className="flex items-center" whileHover={{ scale: 1.05, color: "#10b981" }}>
                                                        <ThumbsUp className="w-3 h-3 mr-1" />
                                                        {item.likes} thích
                                                    </motion.span>
                                                </motion.div>
                                                <motion.div
                                                    className="flex space-x-2"
                                                    initial={{ opacity: 0, y: 10 }}
                                                    animate={{ opacity: 1, y: 0 }}
                                                    transition={{ delay: index * 0.1 + 1.1 }}
                                                >
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="flex-1">
                                                        <Button size="sm" variant="ghost" className="w-full">
                                                            <Eye className="w-4 h-4 mr-1" />
                                                            Xem
                                                        </Button>
                                                    </motion.div>
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }} className="flex-1">
                                                        <Button
                                                            size="sm"
                                                            variant="ghost"
                                                            className="w-full"
                                                            onClick={() => handleEditContent(item)}
                                                        >
                                                            <Edit className="w-4 h-4 mr-1" />
                                                            Sửa
                                                        </Button>
                                                    </motion.div>
                                                    <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
                                                        <Button
                                                            size="sm"
                                                            variant="ghost"
                                                            onClick={() => handleDeleteContent(item.id)}
                                                            className="text-red-600 hover:text-red-700 hover:bg-red-50 dark:hover:bg-red-900/20"
                                                        >
                                                            <Trash2 className="w-4 h-4" />
                                                        </Button>
                                                    </motion.div>
                                                </motion.div>
                                            </CardContent>
                                        </Card>
                                    </motion.div>
                                ))}
                            </AnimatePresence>
                        </motion.div>
                    </CardContent>
                </Card>
            </motion.div>

            {/* Create Content Modal */}
            <Dialog open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen}>
                <DialogContent className="sm:max-w-[600px]">
                    <DialogHeader>
                        <DialogTitle>Tạo Nội Dung Mới</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="title">Tiêu đề</Label>
                            <Input
                                id="title"
                                value={formData.title}
                                onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
                                placeholder="Nhập tiêu đề nội dung..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="type">Loại nội dung</Label>
                            <select
                                id="type"
                                value={formData.type}
                                onChange={(e) => setFormData((prev) => ({ ...prev, type: e.target.value as ContentItem["type"] }))}
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="article">Bài viết</option>
                                <option value="image">Hình ảnh</option>
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="category">Danh mục</Label>
                            <select
                                id="category"
                                value={formData.category}
                                onChange={(e) =>
                                    setFormData((prev) => ({ ...prev, category: e.target.value as ContentItem["category"] }))
                                }
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="motivation">Động lực</option>
                                <option value="tips">Mẹo hay</option>
                                <option value="education">Giáo dục</option>
                                <option value="success-stories">Câu chuyện thành công</option>
                            </select>
                        </div>
                        {formData.type === "image" && (
                            <div className="grid gap-2">
                                <Label htmlFor="thumbnail">URL Hình ảnh</Label>
                                <Input
                                    id="thumbnail"
                                    value={formData.thumbnail}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, thumbnail: e.target.value }))}
                                    placeholder="https://example.com/image.jpg"
                                />
                            </div>
                        )}
                        <div className="grid gap-2">
                            <Label htmlFor="description">Mô tả</Label>
                            <Textarea
                                id="description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Nhập mô tả nội dung..."
                                className="min-h-[100px]"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsCreateModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleCreateContent} disabled={!formData.title.trim() || !formData.description.trim()}>
                            Tạo Nội Dung
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>

            {/* Edit Content Modal */}
            <Dialog open={isEditModalOpen} onOpenChange={setIsEditModalOpen}>
                <DialogContent className="sm:max-w-[600px]">
                    <DialogHeader>
                        <DialogTitle>Chỉnh Sửa Nội Dung</DialogTitle>
                    </DialogHeader>
                    <div className="grid gap-4 py-4">
                        <div className="grid gap-2">
                            <Label htmlFor="edit-title">Tiêu đề</Label>
                            <Input
                                id="edit-title"
                                value={formData.title}
                                onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
                                placeholder="Nhập tiêu đề nội dung..."
                            />
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-type">Loại nội dung</Label>
                            <select
                                id="edit-type"
                                value={formData.type}
                                onChange={(e) => setFormData((prev) => ({ ...prev, type: e.target.value as ContentItem["type"] }))}
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="article">Bài viết</option>
                                <option value="image">Hình ảnh</option>
                            </select>
                        </div>
                        <div className="grid gap-2">
                            <Label htmlFor="edit-category">Danh mục</Label>
                            <select
                                id="edit-category"
                                value={formData.category}
                                onChange={(e) =>
                                    setFormData((prev) => ({ ...prev, category: e.target.value as ContentItem["category"] }))
                                }
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                            >
                                <option value="motivation">Động lực</option>
                                <option value="tips">Mẹo hay</option>
                                <option value="education">Giáo dục</option>
                                <option value="success-stories">Câu chuyện thành công</option>
                            </select>
                        </div>
                        {formData.type === "image" && (
                            <div className="grid gap-2">
                                <Label htmlFor="edit-thumbnail">URL Hình ảnh</Label>
                                <Input
                                    id="edit-thumbnail"
                                    value={formData.thumbnail}
                                    onChange={(e) => setFormData((prev) => ({ ...prev, thumbnail: e.target.value }))}
                                    placeholder="https://example.com/image.jpg"
                                />
                            </div>
                        )}
                        <div className="grid gap-2">
                            <Label htmlFor="edit-description">Mô tả</Label>
                            <Textarea
                                id="edit-description"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                                placeholder="Nhập mô tả nội dung..."
                                className="min-h-[100px]"
                            />
                        </div>
                    </div>
                    <div className="flex justify-end gap-4">
                        <Button variant="outline" onClick={() => setIsEditModalOpen(false)}>
                            Hủy
                        </Button>
                        <Button onClick={handleUpdateContent} disabled={!formData.title.trim() || !formData.description.trim()}>
                            Cập Nhật
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </motion.div>
    )
}
