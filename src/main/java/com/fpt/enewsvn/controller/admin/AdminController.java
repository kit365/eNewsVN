package com.fpt.enewsvn.controller.admin;

import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public String blogCategory(Model model) {
        List<BlogCategoryResponse> list = blogCategoryService.getAll();
        model.addAttribute("blogs", list);
        return "categories";
    }


    @DeleteMapping("/blogs/delete/{id}")
    public String deleteBlog(@PathVariable("id") Long id) {
        blogService.delete(id);
        return "redirect:/admin/blogs"; // Đảm bảo redirect đến đúng URL
    }

    @PostMapping("/category/add")
    public String addCategory(@ModelAttribute CreateBlogCategoryRequest request) {
        blogCategoryService.add(request);
        return "redirect:/admin/blog-category";
    }

    @DeleteMapping("/category/delete")
    public ResponseEntity<String> deleteBlogCategory(@RequestBody List<Long> ids) {
        // Gọi dịch vụ để xóa danh mục theo danh sách ID
        blogCategoryService.delete(ids);
        return ResponseEntity.ok("Xóa thành công");
    }


    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id) {
        blogCategoryService.delete(id);
        return "redirect:/admin/blog-category";
    }

    @GetMapping("/category/{id}")
    @ResponseBody // Sử dụng để trả về JSON
    public BlogCategoryResponse showCategoryByID(@PathVariable("id") Long id) {
        return blogCategoryService.showDetail(id);
    }

    @PostMapping("/category/edit/{id}")
    public ResponseEntity<String> editCategory(@PathVariable("id") Long id, @RequestBody UpdateBlogCategoryRequest request) {
        System.out.println("test");
        blogCategoryService.update(id, request);
        System.out.println(request);
        return ResponseEntity.ok("Cập nhật thành công"); // Hoặc trả về thông báo khác
    }
}