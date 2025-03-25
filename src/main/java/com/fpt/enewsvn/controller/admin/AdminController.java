package com.fpt.enewsvn.controller.admin;

import com.fpt.enewsvn.dto.request.blog.BlogCreationRequest;
import com.fpt.enewsvn.dto.request.blog.BlogUpdateRequest;
import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.role.CreateRoleRequest;
import com.fpt.enewsvn.dto.request.role.UpdateRoleRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.dto.response.BlogResponse;
import com.fpt.enewsvn.dto.response.RoleResponseDTO;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import com.fpt.enewsvn.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin")
@Slf4j
public class AdminController {
    @Autowired
    private BlogService blogService;

    @Autowired
    private BlogCategoryService blogCategoryService;

    @Autowired
    private RoleService roleService;


    // BLOG

    @GetMapping("/blogs")
    public String blog(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "createdAt") String sortKey,
                       @RequestParam(defaultValue = "desc") String sortDirection,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "ACTIVE") String status) {
        Page<BlogResponse> pageResult = blogService.getAll(page, size, sortKey, sortDirection, keyword, status);
        model.addAttribute("blogs", pageResult.getContent());
        log.info("blogs: {}", pageResult.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());

        // Add categories for dropdowns
        List<BlogCategoryResponse> categories = blogCategoryService.getAll();
        log.info("categories: {}", categories);
        model.addAttribute("categories", categories);

        return "blogs";
    }

    @PostMapping("/blogs/add")
    public String addBlog(@ModelAttribute BlogCreationRequest request) {
        log.info("before add: {}", request);
        blogService.add(request);
        log.info("after add: {}", request);
        return "redirect:/admin/blogs";
    }

    @GetMapping("/blogs/delete/{id}")
    public String deleteBlog(@PathVariable("id") Long id) {
        blogService.delete(id);
        return "redirect:/admin/blogs";
    }

    @DeleteMapping("/blogs/delete")
    public ResponseEntity<String> deleteBlog(@RequestBody List<Long> ids) {
        // Gọi dịch vụ để xóa danh mục theo danh sách ID
        blogService.delete(ids);
        return ResponseEntity.ok("Xóa thành công");
    }

    @GetMapping("/blogs/{id}")
    @ResponseBody // Sử dụng để trả về JSON
    public BlogResponse showBlogByID(@PathVariable("id") Long id) {
        return blogService.showDetail(id);
    }

    @PostMapping("/blogs/edit/{id}")
    public ResponseEntity<String> editBlog(@PathVariable("id") Long id, @RequestBody BlogUpdateRequest request) {
        blogService.update(id,request);
        return ResponseEntity.ok("Cập nhật thành công"); // Hoặc trả về thông báo khác
    }

//BLOG CATEGORY

    @GetMapping("/blog-category")
    public String blogCategory(Model model,
                               @RequestParam(required = false) String status,
                               @RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String sortOrder) {
        List<BlogCategoryResponse> list = blogCategoryService.getFilteredCategories(status, keyword, sortOrder);
        model.addAttribute("blogs", list);
        return "categories"; // Trả về view tương ứng
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


    // ROLE

    @GetMapping("/roles")
    public String role(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "roleId") String sortKey,
                       @RequestParam(defaultValue = "asc") String sortDirection,
                       @RequestParam(defaultValue = "") String keyword,
                       @RequestParam(defaultValue = "ACTIVE") String status) {
        Page<RoleResponseDTO> pageResult = roleService.getAll(page, size, sortKey, sortDirection, keyword, status);
        model.addAttribute("roles", pageResult.getContent());
        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        return "roles";
    }

    @PostMapping("/roles/add")
    public String addRole(@ModelAttribute CreateRoleRequest request) {
        roleService.add(request);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/delete/{id}")
    public String deleteRole(@PathVariable("id") Long id) {
        roleService.delete(id);
        return "redirect:/admin/roles";
    }

    @DeleteMapping("/roles/delete")
    public ResponseEntity<String> deleteRole(@RequestBody List<Long> ids) {
        // Gọi dịch vụ để xóa danh mục theo danh sách ID
        roleService.delete(ids);
        return ResponseEntity.ok("Xóa thành công");
    }

    @GetMapping("/roles/{id}")
    @ResponseBody // Sử dụng để trả về JSON
    public RoleResponseDTO showRoleByID(@PathVariable("id") Long id) {
        return roleService.showDetail(id);
    }

    @PostMapping("/roles/edit/{id}")
    public ResponseEntity<String> editRole(@PathVariable("id") Long id, @RequestBody UpdateRoleRequest request) {
        roleService.update(id, request);
        return ResponseEntity.ok("Cập nhật thành công"); // Hoặc trả về thông báo khác
    }

}