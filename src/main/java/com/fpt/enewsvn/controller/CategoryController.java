package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @GetMapping("/{slug}")
    public String categoryPage(@PathVariable String slug, Model model) {
        try {
            log.info("Accessing category with slug: {}", slug);

            // Lấy danh mục theo slug
            BlogCategoryEntity category = blogService.getBlogCategoryBySlug(slug);
            if (category == null) {
                log.warn("Category not found with slug: {}", slug);
                return "redirect:/";
            }

            List<BlogEntity> categoryBlogs = blogService.findByCategory(slug);
            log.info("Found {} blogs for category: {}", categoryBlogs.size(), category.getTitle());

            model.addAttribute("blogs", categoryBlogs);
            model.addAttribute("categoryName", category.getTitle());
            model.addAttribute("categories", blogCategoryService.getBLogsCategory());
            return "category";
        } catch (Exception e) {
            log.error("Error accessing category with slug: " + slug, e);
            return "redirect:/";
        }
    }
}