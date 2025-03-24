package com.fpt.enewsvn.controller.admin;

import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @GetMapping("/blogs")
    public String blog() {
        return "blogs";
    }

    @GetMapping("/blog-category")
    public String blogcategory(Model model) {
        List<BlogCategoryResponse> list = blogCategoryService.getAll();
        model.addAttribute("blogs", list);
        return "categories";
    }

    @DeleteMapping("blogs/delete/{id}")
    public String deleteBlog(@PathVariable("id") Long id) {
        blogService.delete(id);
        return "redirect:/blogs";
    }

    @PostMapping("/category/add")
    public String addCategory(@ModelAttribute CreateBlogCategoryRequest request) {
     blogCategoryService.add(request);
        return "redirect:/admin/blog-category";
    }

    @GetMapping("/category/delete/{id}")
    public String addCategory(@PathVariable("id") Long id) {
        blogCategoryService.delete(id);
        return "redirect:/admin/blog-category";
    }

}
