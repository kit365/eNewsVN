package com.fpt.enewsvn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping
    public String home() {
        return "Home";
    }

    @GetMapping("/home")
    public String home(Model model) {
        return "Home";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Admin";
    }


}
