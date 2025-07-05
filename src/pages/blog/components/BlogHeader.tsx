"use client"

import type React from "react"
import { motion } from "framer-motion"
import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import { AnimatedSection } from "@/components/ui/AnimatedSection"

interface BlogHeaderProps {
    searchTerm: string
    setSearchTerm: (value: string) => void
}

const BlogHeader: React.FC<BlogHeaderProps> = ({ searchTerm, setSearchTerm }) => {
    return (
        <section className="py-20 mt-16 relative overflow-hidden">
            <div className="absolute top-0 right-0 w-96 h-96 bg-gradient-to-br from-emerald-200/30 to-emerald-300/20 dark:from-emerald-500/10 dark:to-emerald-600/5 rounded-full blur-3xl animate-pulse"></div>
            <div className="absolute bottom-0 left-0 w-96 h-96 bg-gradient-to-tr from-emerald-100/40 to-emerald-200/30 dark:from-emerald-600/10 dark:to-emerald-500/5 rounded-full blur-3xl animate-pulse delay-1000"></div>

            <AnimatedSection animation="fadeUp" delay={200}>
                <div className="container mx-auto px-6 relative z-10">
                    <div className="text-center mb-12">
                        <motion.div
                            initial={{ scale: 0.9, opacity: 0 }}
                            animate={{ scale: 1, opacity: 1 }}
                            transition={{ duration: 0.5 }}
                            className="inline-block px-4 py-2 rounded-full text-sm font-bold text-white bg-gradient-to-r from-emerald-500 to-emerald-600 shadow-lg shadow-emerald-200/50 dark:shadow-emerald-500/25 mb-6"
                        >
                            ✨ Blog cộng đồng cai thuốc lá
                        </motion.div>
                        <h1 className="text-4xl lg:text-6xl font-black mb-6 text-slate-800 dark:text-white">
                            Quit Smoking
                            <span className="bg-gradient-to-r from-emerald-500 to-emerald-600 bg-clip-text text-transparent">
                                {" "}
                                Blog
                            </span>
                        </h1>
                        <p className="text-xl text-slate-600 dark:text-slate-300 max-w-3xl mx-auto font-medium leading-relaxed">
                            Chia sẻ kinh nghiệm, câu chuyện và hỗ trợ lẫn nhau trong hành trình cai thuốc lá
                        </p>
                    </div>

                    {/* Search Section */}
                    <div className="max-w-2xl mx-auto mb-12">
                        <div className="relative">
                            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-slate-400 w-5 h-5" />
                            <Input
                                type="text"
                                placeholder="Tìm kiếm bài viết..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className="pl-10 pr-4 py-3 rounded-xl border-2 border-emerald-200 dark:border-slate-600 focus:border-emerald-400 dark:focus:border-emerald-500 bg-white/80 dark:bg-slate-800/80 backdrop-blur-sm"
                            />
                        </div>
                    </div>
                </div>
            </AnimatedSection>
        </section>
    )
}

export default BlogHeader
