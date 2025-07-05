"use client"

import { useState, useEffect, useRef } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { MessageSquare, Users, Search, Send, Plus } from "lucide-react"

// Types for chat system
interface Message {
    id: string
    senderId: string
    senderName: string
    senderType: "coach" | "user"
    content: string
    timestamp: Date
    isRead: boolean
}

interface Conversation {
    id: string
    participantId: string
    participantName: string
    participantAvatar?: string
    lastMessage: string
    lastMessageTime: Date
    unreadCount: number
    isUrgent: boolean
    isGroup?: boolean
}

export function CommunicationCenter() {
    const [conversations, setConversations] = useState<Conversation[]>([])
    const [selectedConversation, setSelectedConversation] = useState<string | null>(null)
    const [messages, setMessages] = useState<{ [conversationId: string]: Message[] }>({})
    const [newMessage, setNewMessage] = useState("")
    const [searchTerm, setSearchTerm] = useState("")
    const messagesEndRef = useRef<HTMLDivElement>(null)

    // Initialize mock data
    useEffect(() => {
        const mockConversations: Conversation[] = [
            {
                id: "conv1",
                participantId: "user1",
                participantName: "Nguyễn Văn An",
                participantAvatar: "/placeholder.svg?height=40&width=40",
                lastMessage: "Chào coach, em đang gặp khó khán trong việc kiểm soát cơn thèm thuốc vào buổi tối.",
                lastMessageTime: new Date(Date.now() - 30 * 60 * 1000), // 30 minutes ago
                unreadCount: 2,
                isUrgent: false,
            },
            {
                id: "conv2",
                participantId: "group1",
                participantName: "Nhóm Hỗ Trợ Tuần 1",
                participantAvatar: "/placeholder.svg?height=40&width=40",
                lastMessage: "Mọi người cùng chia sẻ kinh nghiệm nhé",
                lastMessageTime: new Date(Date.now() - 75 * 60 * 1000), // 1 hour 15 minutes ago
                unreadCount: 0,
                isUrgent: false,
                isGroup: true,
            },
            {
                id: "conv3",
                participantId: "user3",
                participantName: "Lê Văn Cường",
                participantAvatar: "/placeholder.svg?height=40&width=40",
                lastMessage: "Cảm ơn coach rất nhiều!",
                lastMessageTime: new Date(Date.now() - 24 * 60 * 60 * 1000), // Yesterday
                unreadCount: 0,
                isUrgent: false,
            },
        ]

        const mockMessages: { [key: string]: Message[] } = {
            conv1: [
                {
                    id: "msg1",
                    senderId: "user1",
                    senderName: "Nguyễn Văn An",
                    senderType: "user",
                    content: "Chào coach, em đang gặp khó khán trong việc kiểm soát cơn thèm thuốc vào buổi tối.",
                    timestamp: new Date(Date.now() - 30 * 60 * 1000),
                    isRead: false,
                },
                {
                    id: "msg2",
                    senderId: "coach1",
                    senderName: "Coach Minh",
                    senderType: "coach",
                    content: "Chào Anh! Coach hiểu cảm giác của em. Hãy thử áp dụng kỹ thuật thở sâu khi có cơn thèm nhé.",
                    timestamp: new Date(Date.now() - 25 * 60 * 1000),
                    isRead: true,
                },
            ],
            conv2: [
                {
                    id: "msg3",
                    senderId: "coach1",
                    senderName: "Coach Minh",
                    senderType: "coach",
                    content: "Chào mọi người! Hôm nay chúng ta sẽ thảo luận về cách vượt qua tuần đầu tiên.",
                    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
                    isRead: true,
                },
                {
                    id: "msg4",
                    senderId: "user2",
                    senderName: "Trần Thị Mai",
                    senderType: "user",
                    content: "Mọi người cùng chia sẻ kinh nghiệm nhé",
                    timestamp: new Date(Date.now() - 75 * 60 * 1000),
                    isRead: true,
                },
            ],
            conv3: [
                {
                    id: "msg5",
                    senderId: "user3",
                    senderName: "Lê Văn Cường",
                    senderType: "user",
                    content: "Cảm ơn coach rất nhiều!",
                    timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000),
                    isRead: true,
                },
            ],
        }

        setConversations(mockConversations)
        setMessages(mockMessages)

        // Load from localStorage if available
        const savedConversations = localStorage.getItem("coach-conversations")
        const savedMessages = localStorage.getItem("coach-messages")

        if (savedConversations) {
            setConversations(JSON.parse(savedConversations))
        }
        if (savedMessages) {
            setMessages(JSON.parse(savedMessages))
        }
    }, [])

    // Save to localStorage whenever conversations or messages change
    useEffect(() => {
        localStorage.setItem("coach-conversations", JSON.stringify(conversations))
    }, [conversations])

    useEffect(() => {
        localStorage.setItem("coach-messages", JSON.stringify(messages))
    }, [messages])

    // Auto scroll to bottom when new messages arrive
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }, [messages, selectedConversation])

    // Send message function
    const sendMessage = () => {
        if (!newMessage.trim() || !selectedConversation) return

        const newMsg: Message = {
            id: `msg_${Date.now()}`,
            senderId: "coach1",
            senderName: "Coach Minh",
            senderType: "coach",
            content: newMessage.trim(),
            timestamp: new Date(),
            isRead: true,
        }

        // Add message to conversation
        setMessages((prev) => ({
            ...prev,
            [selectedConversation]: [...(prev[selectedConversation] || []), newMsg],
        }))

        // Update conversation's last message
        setConversations((prev) =>
            prev.map((conv) =>
                conv.id === selectedConversation
                    ? { ...conv, lastMessage: newMessage.trim(), lastMessageTime: new Date() }
                    : conv,
            ),
        )

        setNewMessage("")
    }

    // Mark messages as read when conversation is selected
    const selectConversation = (conversationId: string) => {
        setSelectedConversation(conversationId)

        // Mark all messages in this conversation as read
        setMessages((prev) => ({
            ...prev,
            [conversationId]: prev[conversationId]?.map((msg) => ({ ...msg, isRead: true })) || [],
        }))

        // Reset unread count
        setConversations((prev) => prev.map((conv) => (conv.id === conversationId ? { ...conv, unreadCount: 0 } : conv)))
    }

    // Filter conversations based on search
    const filteredConversations = conversations.filter((conv) =>
        conv.participantName.toLowerCase().includes(searchTerm.toLowerCase()),
    )

    // Calculate statistics
    const totalUnread = conversations.reduce((sum, conv) => sum + conv.unreadCount, 0)
    const totalConversations = conversations.length

    const formatTime = (date: Date) => {
        const now = new Date()
        const diffInHours = Math.floor((now.getTime() - date.getTime()) / (1000 * 60 * 60))

        if (diffInHours < 1) {
            const diffInMinutes = Math.floor((now.getTime() - date.getTime()) / (1000 * 60))
            return diffInMinutes < 1 ? "Vừa xong" : `${diffInMinutes} phút trước`
        } else if (diffInHours < 24) {
            return date.toLocaleTimeString("vi-VN", { hour: "2-digit", minute: "2-digit" })
        } else if (diffInHours < 48) {
            return "Yesterday"
        } else {
            return date.toLocaleDateString("vi-VN")
        }
    }

    return (
        <div className="space-y-6">
            {/* Statistics Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium text-slate-700 dark:text-slate-300">Tin nhắn chưa đọc</CardTitle>
                        <MessageSquare className="h-4 w-4 text-blue-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-slate-900 dark:text-white">{totalUnread}</div>
                    </CardContent>
                </Card>

                <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-sm font-medium text-slate-700 dark:text-slate-300">Cuộc trò chuyện</CardTitle>
                        <Users className="h-4 w-4 text-purple-500" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-bold text-slate-900 dark:text-white">{totalConversations}</div>
                    </CardContent>
                </Card>
            </div>

            {/* Main Chat Interface */}
            <Card className="bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm border-slate-200 dark:border-slate-700/50 h-[600px]">
                <div className="flex h-full">
                    {/* Left Sidebar - Conversations List */}
                    <div className="w-1/3 border-r border-slate-200 dark:border-slate-700/50 flex flex-col">
                        {/* Header */}
                        <div className="p-4 border-b border-slate-200 dark:border-slate-700/50">
                            <h3 className="text-lg font-semibold text-slate-900 dark:text-white mb-3">Trung Tâm Giao Tiếp</h3>
                            <div className="relative">
                                <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 h-4 w-4" />
                                <Input
                                    placeholder="Tìm kiếm..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="pl-10"
                                />
                            </div>
                        </div>

                        {/* Conversations Header */}
                        <div className="p-4 border-b border-slate-200 dark:border-slate-700/50">
                            <div className="flex items-center justify-between">
                                <h4 className="font-medium text-slate-900 dark:text-white">Cuộc Trò Chuyện</h4>
                                <Button size="sm" variant="ghost">
                                    <Plus className="h-4 w-4" />
                                </Button>
                            </div>
                        </div>

                        {/* Conversations List */}
                        <div className="flex-1 overflow-y-auto">
                            {filteredConversations.map((conversation) => (
                                <div
                                    key={conversation.id}
                                    onClick={() => selectConversation(conversation.id)}
                                    className={`p-4 border-b border-slate-100 dark:border-slate-700/30 cursor-pointer hover:bg-slate-50 dark:hover:bg-slate-700/30 transition-colors ${selectedConversation === conversation.id
                                        ? "bg-blue-50 dark:bg-blue-900/20 border-l-4 border-l-blue-500"
                                        : ""
                                        }`}
                                >
                                    <div className="flex items-center space-x-3">
                                        <div className="relative">
                                            <Avatar className="h-10 w-10">
                                                <AvatarImage src={conversation.participantAvatar || "/placeholder.svg"} />
                                                <AvatarFallback className="bg-blue-500 text-white">
                                                    {conversation.participantName.charAt(0)}
                                                </AvatarFallback>
                                            </Avatar>
                                            {conversation.unreadCount > 0 && (
                                                <Badge className="absolute -top-1 -right-1 h-5 w-5 rounded-full bg-red-500 text-white text-xs flex items-center justify-center p-0">
                                                    {conversation.unreadCount}
                                                </Badge>
                                            )}
                                        </div>
                                        <div className="flex-1 min-w-0">
                                            <div className="flex items-center justify-between">
                                                <p className="font-medium text-slate-900 dark:text-white truncate">
                                                    {conversation.participantName}
                                                </p>
                                                <span className="text-xs text-slate-500">{formatTime(conversation.lastMessageTime)}</span>
                                            </div>
                                            <p className="text-sm text-slate-600 dark:text-slate-400 truncate">{conversation.lastMessage}</p>
                                        </div>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Right Side - Chat Area */}
                    <div className="flex-1 flex flex-col">
                        {selectedConversation ? (
                            <>
                                {/* Chat Header */}
                                <div className="p-4 border-b border-slate-200 dark:border-slate-700/50">
                                    <div className="flex items-center space-x-3">
                                        <Avatar className="h-10 w-10">
                                            <AvatarImage
                                                src={
                                                    conversations.find((c) => c.id === selectedConversation)?.participantAvatar ||
                                                    "/placeholder.svg" ||
                                                    "/placeholder.svg"
                                                }
                                            />
                                            <AvatarFallback className="bg-blue-500 text-white">
                                                {conversations.find((c) => c.id === selectedConversation)?.participantName.charAt(0)}
                                            </AvatarFallback>
                                        </Avatar>
                                        <div>
                                            <h3 className="font-semibold text-slate-900 dark:text-white">Trò Chuyện</h3>
                                            <p className="text-sm text-slate-600 dark:text-slate-400">
                                                {conversations.find((c) => c.id === selectedConversation)?.participantName}
                                            </p>
                                        </div>
                                    </div>
                                </div>

                                {/* Messages Area */}
                                <div className="flex-1 overflow-y-auto p-4 space-y-4">
                                    {messages[selectedConversation]?.map((message) => (
                                        <div
                                            key={message.id}
                                            className={`flex ${message.senderType === "coach" ? "justify-end" : "justify-start"}`}
                                        >
                                            <div
                                                className={`max-w-[70%] rounded-lg p-3 ${message.senderType === "coach"
                                                    ? "bg-blue-500 text-white"
                                                    : "bg-slate-100 dark:bg-slate-700 text-slate-900 dark:text-white"
                                                    }`}
                                            >
                                                <p className="text-sm">{message.content}</p>
                                                <div className="flex items-center justify-between mt-1">
                                                    <span
                                                        className={`text-xs ${message.senderType === "coach" ? "text-blue-100" : "text-slate-500 dark:text-slate-400"
                                                            }`}
                                                    >
                                                        {formatTime(message.timestamp)}
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                    <div ref={messagesEndRef} />
                                </div>

                                {/* Message Input */}
                                <div className="p-4 border-t border-slate-200 dark:border-slate-700/50">
                                    <div className="flex space-x-2">
                                        <Input
                                            placeholder="Nhập tin nhắn..."
                                            value={newMessage}
                                            onChange={(e) => setNewMessage(e.target.value)}
                                            onKeyPress={(e) => e.key === "Enter" && sendMessage()}
                                            className="flex-1"
                                        />
                                        <Button onClick={sendMessage} className="bg-black hover:bg-gray-800">
                                            <Send className="h-4 w-4" />
                                        </Button>
                                    </div>
                                </div>
                            </>
                        ) : (
                            <div className="flex-1 flex items-center justify-center">
                                <div className="text-center">
                                    <MessageSquare className="h-12 w-12 text-slate-400 mx-auto mb-4" />
                                    <p className="text-slate-600 dark:text-slate-400">Chọn một cuộc trò chuyện để bắt đầu nhắn tin</p>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </Card>
        </div>
    )
}
