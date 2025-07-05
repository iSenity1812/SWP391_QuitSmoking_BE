import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { userService } from "@/services/userService";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Plus, MessageCircle, Trophy } from "lucide-react"
import type { User } from "../../types/user-types"
import axiosConfig from "@/config/axiosConfig";

interface SocialTabProps {
    user: User
}

export function SocialTab({ user }: SocialTabProps) {
    console.log("SocialTab mounted, user:", user);
    const [search, setSearch] = useState("");
    const [results, setResults] = useState<Array<{
        userId: string;
        username: string;
        email: string;
        profilePicture?: string;
    }>>([]);
    const [showDropdown, setShowDropdown] = useState(false);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const inputRef = useRef<HTMLInputElement>(null);
    const [following, setFollowing] = useState<Array<{
        userId: string;
        username: string;
        email: string;
        profilePicture?: string;
    }>>([]);

    const handleSearchChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setSearch(value);
        if (value.trim().length < 2) {
            setResults([]);
            setShowDropdown(false);
            return;
        }
        setLoading(true);
        try {
            const users = await userService.searchUsers(value);
            setResults(users);
            setShowDropdown(true);
        } catch {
            setResults([]);
            setShowDropdown(false);
        } finally {
            setLoading(false);
        }
    };

    const handleSearchButton = () => {
        if (search.trim().length >= 2) {
            inputRef.current?.focus();
            setLoading(true);
            userService.searchUsers(search).then(users => {
                setResults(users);
                setShowDropdown(true);
            }).catch(() => {
                setResults([]);
                setShowDropdown(false);
            }).finally(() => setLoading(false));
        } else {
            inputRef.current?.focus();
        }
    };

    const handleSelectUser = (userId: string) => {
        setSearch("");
        setShowDropdown(false);
        navigate(`/user/${userId}`);
    };

    const refreshFollowing = () => {
        if (!user?.userId) return;
        axiosConfig.get(`/api/follow/following/${user.userId}`, {
            headers: { 'X-User-Id': user.userId }
        })
            .then(res => {
                setFollowing(Array.isArray(res.data) ? res.data : []);
            })
            .catch(() => setFollowing([]));
    };

    useEffect(() => {
        refreshFollowing();
    }, [user?.userId]);

    const handleFollow = async (targetUserId: string) => {
        await userService.follow(targetUserId, user.userId);
        refreshFollowing();
    };

    const handleUnfollow = async (targetUserId: string) => {
        await userService.unfollow(targetUserId, user.userId);
        refreshFollowing();
    };

    return (
        <div className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <Card>
                    <CardHeader>
                        <CardTitle className="flex items-center justify-between">
                            <span>Bạn bè</span>
                        </CardTitle>
                    </CardHeader>
                    <CardContent>
                        {/* Search user box */}
                        <div className="relative mb-4 w-full">
                            <div className="flex items-center gap-2">
                                <input
                                    ref={inputRef}
                                    type="text"
                                    value={search}
                                    onChange={handleSearchChange}
                                    placeholder="Tìm người dùng (tên hoặc email)"
                                    className="w-full px-3 py-2 rounded-lg border border-emerald-200 dark:border-slate-600 bg-white dark:bg-slate-800 text-slate-800 dark:text-white focus:outline-none focus:ring-2 focus:ring-emerald-400"
                                    onFocus={() => search.length > 1 && setShowDropdown(true)}
                                    onBlur={() => setTimeout(() => setShowDropdown(false), 200)}
                                />
                                <button
                                    type="button"
                                    onClick={handleSearchButton}
                                    className="p-2 rounded-lg bg-emerald-500 hover:bg-emerald-600 text-white focus:outline-none focus:ring-2 focus:ring-emerald-400"
                                    title="Tìm kiếm"
                                >
                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" d="M21 21l-4-4m0 0A7 7 0 104 4a7 7 0 0013 13z" /></svg>
                                </button>
                            </div>
                            {showDropdown && (
                                <div className="absolute left-0 right-0 mt-1 bg-white dark:bg-slate-800 border border-emerald-200 dark:border-slate-600 rounded-lg shadow-lg z-50 max-h-60 w-full min-w-[220px] max-w-[350px] overflow-y-auto" style={{position: 'absolute'}}>
                                    {loading && <div className="p-2 text-center text-sm text-slate-400">Đang tìm...</div>}
                                    {!loading && (!Array.isArray(results) || results.length === 0) && <div className="p-2 text-center text-sm text-slate-400">Không tìm thấy người dùng</div>}
                                    {Array.isArray(results) && results.slice(0, 5).map((u) => (
                                        <div
                                            key={u.userId}
                                            className="flex items-center gap-2 px-3 py-2 hover:bg-emerald-50 dark:hover:bg-slate-700 cursor-pointer"
                                            onClick={() => handleSelectUser(u.userId)}
                                        >
                                            {u.profilePicture ? (
                                                <img src={u.profilePicture} alt={u.username} className="w-7 h-7 rounded-full object-cover" />
                                            ) : (
                                                <div className="w-7 h-7 rounded-full bg-slate-200 dark:bg-slate-700 flex items-center justify-center text-xl">👤</div>
                                            )}
                                            <div className="flex flex-col">
                                                <span className="font-medium text-slate-800 dark:text-white">{u.username}</span>
                                                <span className="text-xs text-slate-500">{u.email}</span>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                        <div className="space-y-4">
                            {user.friends.map((friend, index) => (
                                <div
                                    key={index}
                                    className="flex items-center justify-between p-3 bg-slate-50 dark:bg-slate-800 rounded-lg"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="relative">
                                            <Avatar className="h-10 w-10">
                                                <AvatarImage src={friend.avatar || "/placeholder.svg"} alt={friend.name} />
                                                <AvatarFallback>{friend.name.charAt(0)}</AvatarFallback>
                                            </Avatar>
                                            <div
                                                className={`absolute -bottom-1 -right-1 w-3 h-3 rounded-full border-2 border-white dark:border-slate-800 ${friend.status === "online" ? "bg-green-500" : "bg-slate-400"
                                                    }`}
                                            />
                                        </div>
                                        <div>
                                            <p className="font-medium text-slate-900 dark:text-white">{friend.name}</p>
                                            <p className="text-sm text-slate-500 dark:text-slate-400">{friend.streak} ngày streak</p>
                                        </div>
                                    </div>
                                    <Button variant="outline" size="sm">
                                        <MessageCircle className="h-4 w-4" />
                                    </Button>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Đang theo dõi</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            {following.length === 0 && <div className="text-slate-400 text-center">Bạn chưa theo dõi ai.</div>}
                            {following.map((u, idx) => (
                                <div key={u.userId} className="flex items-center gap-3 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                    <div className="w-8 h-8 bg-emerald-500 rounded-full flex items-center justify-center text-white font-bold">
                                        {idx + 1}
                                    </div>
                                    <Avatar className="h-8 w-8">
                                        {u.profilePicture ? (
                                            <AvatarImage src={u.profilePicture} alt={u.username} />
                                        ) : (
                                            <AvatarFallback>{u.username?.charAt(0) || "?"}</AvatarFallback>
                                        )}
                                    </Avatar>
                                    <div className="flex-1">
                                        <p className="font-medium text-slate-900 dark:text-white">{u.username}</p>
                                        <p className="text-sm text-slate-500 dark:text-slate-400">{u.email}</p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </CardContent>
                </Card>

                <Card>
                    <CardHeader>
                        <CardTitle>Bảng xếp hạng</CardTitle>
                    </CardHeader>
                    <CardContent>
                        <div className="space-y-3">
                            <div className="flex items-center gap-3 p-3 bg-yellow-50 dark:bg-yellow-900/20 rounded-lg">
                                <div className="w-8 h-8 bg-yellow-500 rounded-full flex items-center justify-center text-white font-bold">
                                    1
                                </div>
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback>LC</AvatarFallback>
                                </Avatar>
                                <div className="flex-1">
                                    <p className="font-medium text-slate-900 dark:text-white">Lê Văn C</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">67 ngày</p>
                                </div>
                                <Trophy className="h-5 w-5 text-yellow-500" />
                            </div>

                            <div className="flex items-center gap-3 p-3 bg-emerald-50 dark:bg-emerald-900/20 rounded-lg">
                                <div className="w-8 h-8 bg-emerald-500 rounded-full flex items-center justify-center text-white font-bold">
                                    2
                                </div>
                                <Avatar className="h-8 w-8">
                                    <AvatarImage src={user.avatar || "/placeholder.svg"} alt={user.name} />
                                    <AvatarFallback>{user.name.charAt(0)}</AvatarFallback>
                                </Avatar>
                                <div className="flex-1">
                                    <p className="font-medium text-slate-900 dark:text-white">{user.name} (Bạn)</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">{user.daysSmokeFreee} ngày</p>
                                </div>
                                <Badge variant="secondary" className="dark:bg-slate-700 dark:text-slate-300">
                                    Bạn
                                </Badge>
                            </div>

                            <div className="flex items-center gap-3 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                <div className="w-8 h-8 bg-slate-500 rounded-full flex items-center justify-center text-white font-bold">
                                    3
                                </div>
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback>TTB</AvatarFallback>
                                </Avatar>
                                <div className="flex-1">
                                    <p className="font-medium text-slate-900 dark:text-white">Trần Thị B</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">32 ngày</p>
                                </div>
                            </div>

                            <div className="flex items-center gap-3 p-3 bg-slate-50 dark:bg-slate-800 rounded-lg">
                                <div className="w-8 h-8 bg-slate-500 rounded-full flex items-center justify-center text-white font-bold">
                                    4
                                </div>
                                <Avatar className="h-8 w-8">
                                    <AvatarFallback>PTD</AvatarFallback>
                                </Avatar>
                                <div className="flex-1">
                                    <p className="font-medium text-slate-900 dark:text-white">Phạm Thị D</p>
                                    <p className="text-sm text-slate-500 dark:text-slate-400">21 ngày</p>
                                </div>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    )
}
