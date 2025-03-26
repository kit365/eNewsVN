package com.fpt.enewsvn.controller.admin;

import com.fpt.enewsvn.dto.request.blog.BlogCreationRequest;
import com.fpt.enewsvn.dto.request.blog.BlogUpdateRequest;
import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.role.CreateRoleRequest;
import com.fpt.enewsvn.dto.request.role.UpdateRoleRequest;
import com.fpt.enewsvn.dto.request.user.CreateUserRequest;
import com.fpt.enewsvn.dto.request.user.UpdateUserRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.dto.response.BlogResponse;
import com.fpt.enewsvn.dto.response.RoleResponseDTO;
import com.fpt.enewsvn.dto.response.UserResponseDTO;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.entity.RoleEntity;
import com.fpt.enewsvn.exception.AppException;
import com.fpt.enewsvn.service.BlogCategoryService;
import com.fpt.enewsvn.service.BlogService;
import com.fpt.enewsvn.service.RoleService;
import com.fpt.enewsvn.service.UserService;
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

    @Autowired
    private UserService userService;


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
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody CreateRoleRequest request) {
        try {
            RoleResponseDTO newRole = roleService.add(request);
            return ResponseEntity.ok(newRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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

    @GetMapping("roles/{id}")
    public ResponseEntity<RoleResponseDTO> getRole(@PathVariable Long id) {
        try {
            RoleResponseDTO role = roleService.getRoleById(id);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("roles/edit/{id}")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {
        try {
            RoleResponseDTO updatedRole = roleService.updateRole(id, request);
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //ADMIN

    @GetMapping("/accounts")
    public String account(Model model,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(defaultValue = "userID") String sortKey,
                          @RequestParam(defaultValue = "asc") String sortDirection,
                          @RequestParam(defaultValue = "") String keyword,
                          @RequestParam(defaultValue = "ACTIVE") String status) {

        Page<UserResponseDTO> pageResult = userService.getAll(page, size, sortKey, sortDirection, keyword, status);
        List<RoleResponseDTO> roles = roleService.getAllRoles();

        model.addAttribute("accounts", pageResult.getContent());
        model.addAttribute("roles", roles);
        model.addAttribute("page", page);
        model.addAttribute("totalPages", pageResult.getTotalPages());
        model.addAttribute("currentPage", page);
        return "accounts";
    }

    @PostMapping("/accounts/add")
    public String addAccount(@ModelAttribute CreateUserRequest request) {
        userService.add(request);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/accounts/delete/{id}")
    public String deleteAccount(@PathVariable("id") Long id) {
        userService.deleteAccount(id);
        return "redirect:/admin/accounts";
    }

    @DeleteMapping("/accounts/delete")
    public ResponseEntity<String> deleteAccount(@RequestBody List<Long> ids) {
        // Gọi dịch vụ để xóa danh mục theo danh sách ID
        userService.deleteSelectedAccount(ids);
        return ResponseEntity.ok("Xóa thành công");
    }

    @GetMapping("/accounts/{id}")
    @ResponseBody // Sử dụng để trả về JSON
    public UserResponseDTO showAccountByID(@PathVariable("id") Long id) {
        return userService.showDetail(id);
    }

    @GetMapping("/accounts/edit/{id}")
    public ResponseEntity<UserResponseDTO> getAccountById(@PathVariable Long id) {
        UserResponseDTO userResponse = userService.getUser(id);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/accounts/save")
    public ResponseEntity<String> saveAccount(@RequestBody UpdateUserRequest request) {
        try {
            userService.saveUser(request);
            return ResponseEntity.ok("User saved successfully");
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}