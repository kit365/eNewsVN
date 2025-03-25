package com.fpt.enewsvn.controller;

import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private BlogService blogService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("categories", blogCategoryService.getBLogsCategory());
        model.addAttribute("blogs", blogCategoryService.getBLogsCategory());
        return "Home";
    }

    @GetMapping("/home")
    public String homes(Model model) {
        return "Home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin";
    }


}
