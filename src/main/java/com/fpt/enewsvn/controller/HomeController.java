package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("categories", blogCategoryService.getBLogsCategory());
        model.addAttribute("blogs", blogService.getAll());
        return "Home";
    }

    @GetMapping("/home")
    public String homes(Model model) {
        model.addAttribute("categories", blogCategoryService.getBLogsCategory());
        model.addAttribute("blogs", blogService.getAll());
        return "Home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin";
    }

    @GetMapping("/continue")
    public String doctiep() {
        return "detail";
    }

    @GetMapping("/blog/{slug}")
    public String blogDetail(@PathVariable String slug, Model model) {
        BlogEntity blog = blogService.searchBySlug(slug);
        if (blog != null) {
            model.addAttribute("blog", blog);
            // Lấy các bài viết liên quan cùng danh mục
            if (blog.getBlogCategory() != null) {
                List<BlogEntity> relatedBlogs = blogService.findByCategory(blog.getBlogCategory().getTitle());
                relatedBlogs.remove(blog); // Loại bỏ bài viết hiện tại
                model.addAttribute("relatedBlogs", relatedBlogs);
            }
            return "blog-detail";
        }
        return "redirect:/home";
    }
}
