package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @GetMapping("/{slug}")
    public String categoryPage(@PathVariable String title, Model model) {
        List<BlogEntity> categoryBlogs = blogService.findByCategory(title);
        model.addAttribute("blogs", categoryBlogs);
        model.addAttribute("categoryName", title);
        model.addAttribute("categories", blogCategoryService.getBLogsCategory());
        return "category";
    }
}