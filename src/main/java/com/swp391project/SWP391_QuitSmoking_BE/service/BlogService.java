package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Blog;
import com.swp391project.SWP391_QuitSmoking_BE.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlogService {
    @Autowired
    private BlogRepository blogRepository;

    public List<Blog> getAllBlogs() {
        return blogRepository.findAll();
    }

    public Optional<Blog> getBlogById(Integer id) {
        return blogRepository.findById(id);
    }

    public Blog createBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    public Blog updateBlog(Integer id, Blog blogDetails) {
        return blogRepository.findById(id).map(blog -> {
            blog.setAuthorId(blogDetails.getAuthorId());
            blog.setTitle(blogDetails.getTitle());
            blog.setContent(blogDetails.getContent());
            blog.setLastUpdated(blogDetails.getLastUpdated());
            blog.setStatus(blogDetails.getStatus());
            blog.setApprovedBy(blogDetails.getApprovedBy());
            blog.setApprovedAt(blogDetails.getApprovedAt());
            return blogRepository.save(blog);
        }).orElseThrow(() -> new RuntimeException("Blog not found"));
    }

    public void deleteBlog(Integer id) {
        blogRepository.deleteById(id);
    }
}