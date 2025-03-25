package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/technology")
public class TechnologyController {

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String technologyPage(Model model) {
        List<BlogEntity> technologyBlogs = blogService.findByCategory("technology");
        model.addAttribute("blogs", technologyBlogs);
        return "technology"; // Tên của view template
    }
}