package com.fpt.enewsvn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.dto.request.blog.BlogCreationRequest;
import com.fpt.enewsvn.dto.request.blog.BlogUpdateRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.dto.response.BlogResponse;
import com.fpt.enewsvn.dto.response.UserResponseDTO;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.exception.AppException;
import com.fpt.enewsvn.exception.ErrorCode;
import com.fpt.enewsvn.mapper.BlogMapper;
import com.fpt.enewsvn.repository.BlogCategoryRepository;
import com.fpt.enewsvn.repository.BlogRepository;

import com.fpt.enewsvn.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BlogService {

    BlogRepository blogRepository;
    BlogMapper blogMapper;
    BlogCategoryRepository blogCategoryRepository;
    Cloudinary cloudinary;
    UserRepository userRepository;

    public boolean add(BlogCreationRequest request) {
        BlogEntity blogEntity = blogMapper.toBlogEntity(request);
        BlogCategoryEntity blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID()).orElse(null);

        if (request.getCategoryID() != null) {
            blogEntity.setBlogCategory(blogCategoryEntity);
        } else {
            blogEntity.setBlogCategory(null);
        }

        if (request.getUser() != null) {
            blogEntity.setUser(userRepository.findById(request.getUser())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        }

        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = blogRepository.findAll().size();
            blogEntity.setPosition(size + 1);
        }

        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {

            int count = 0;
            List<String> images = new ArrayList<>();

            for (MultipartFile file : request.getThumbnail()) {
                try {
                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
                    images.add(url);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            blogEntity.setThumbnail(images);
        }

        blogEntity.setSlug(getSlug(request.getTitle()));

        BlogResponse blogResponse = blogMapper.toBlogResponse(blogRepository.save(blogEntity));
        return true;
    }

    public List<BlogResponse> getAll() {
        return blogMapper.toBlogsResponseDTO(blogRepository.findAll());
    }

    public BlogResponse update(Long id, BlogUpdateRequest request) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }

        // Check nếu không thay đổi BlogCategory thì sẽ ko set giá trị mới
        BlogCategoryEntity blogCategoryEntity = null;

        if (request.getCategoryID() != null) {
            blogCategoryEntity = blogCategoryRepository.findById(request.getCategoryID())
                    .orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
            blogEntity.setBlogCategory(blogCategoryEntity);
        } else {
            blogEntity.setBlogCategory(null);
        }

        if (request.getUser() != null) {
            blogEntity.setUser(userRepository.findById(request.getUser())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            blogEntity.setSlug(getSlug(request.getTitle()));
        }

        // if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
        // int count = 0;
        // List<String> images = new ArrayList<>();
        // for (String file : blogCategoryEntity.getImage()) {
        // try {
        // deleteImageFromCloudinary(file);
        // } catch (IOException e) {
        // log.error(e.getMessage());
        // throw new RuntimeException(e);
        // }
        // }
        //
        // for (MultipartFile file : request.getThumbnail()) {
        // String slug = getSlug(request.getTitle());
        //
        // if (!StringUtils.hasLength(slug)) {
        // slug = getSlug(request.getTitle());
        // }
        // try {
        // String url = uploadImageFromFile(file, slug, count++);
        // } catch (IOException e) {
        // log.error(e.getMessage());
        // throw new RuntimeException(e);
        // }
        // }
        // blogCategoryEntity.setImage(images);
        // }

        blogMapper.updateBlogEntity(blogEntity, request);
        BlogResponse blogResponse = blogMapper.toBlogResponse(blogRepository.save(blogEntity));

        // Chỉ trả về 2 fields là ID và Title của thằng con, không trả hết
        BlogCategoryResponse blogCategoryResponse = new BlogCategoryResponse();
        if (blogEntity.getBlogCategory() != null) {
            blogCategoryResponse.setId(blogEntity.getBlogCategory().getId());
            blogCategoryResponse.setTitle(blogEntity.getBlogCategory().getTitle());
            blogResponse.setBlogCategory(blogCategoryResponse);
        }

        // Chỉ trả về 2 fields là ID và First và Last Name của thằng con, không trả hết
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        if (blogEntity.getUser() != null) {
            userResponseDTO.setUserID(blogEntity.getUser().getUserID());
            userResponseDTO.setFirstName(blogEntity.getUser().getFirstName());
            userResponseDTO.setLastName(blogEntity.getUser().getLastName());
            // blogResponse.setUser(userResponseDTO);
        }
        return blogResponse;
    }

    public BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }

    // Cập nhật trạng thái, xóa mềm, khôi phục cho All ID được chọn
    // Dùng khi user tích vào nhiều ô phẩn tử, sau đó chọn thao tác, ẩn, xóa mềm,
    // khôi phục

    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<BlogEntity> blogEntities = blogRepository.findAllById(id);

        // CẬP NHẬT TRẠNG THÁI BLOG
        if (statusEnum == Status.INACTIVE || statusEnum == Status.ACTIVE) {
            blogEntities.forEach(blogEntity -> blogEntity.setStatus(statusEnum));
            blogRepository.saveAll(blogEntities);
            return "Cập nhật trạng thái thành công";

            // CẬP NHẬT XÓA MỀM BLOG
        } else if (statusEnum == Status.SOFT_DELETED) {
            blogEntities.forEach(blogEntity -> blogEntity.setDeleted(true));

            // CẬP NHẬT KHÔI PHỤC BLOG
            blogRepository.saveAll(blogEntities);
            return "Xóa mềm thành công";
        } else if (statusEnum == Status.RESTORED) {
            blogEntities.forEach(blogEntity -> blogEntity.setDeleted(false));

            blogRepository.saveAll(blogEntities);
            return "Phục hồi thành công";
        }
        return "Cập nhật hất bại";
    }

    // XÓA CỨNG 1 BLOG
    public boolean delete(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }

        if (blogEntity.getThumbnail() != null && !blogEntity.getThumbnail().isEmpty()) {
            blogEntity.getThumbnail().forEach(s -> {
                try {
                    deleteImageFromCloudinary(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        log.info("Delete: {}", id);
        blogRepository.delete(blogEntity);
        return true;
    }

    // XÓA CỨNG NHIỀU BLOG
    public boolean delete(List<Long> longs) {
        List<BlogEntity> blogEntities = blogRepository.findAllById(longs);

        for (BlogEntity blogEntity : blogEntities) {
            if (blogEntity.getThumbnail() != null && !blogEntity.getThumbnail().isEmpty()) {
                for (String url : blogEntity.getThumbnail()) {
                    try {
                        deleteImageFromCloudinary(url);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        blogRepository.deleteAll(blogEntities);
        return true;
    }

    // XÓA MỀM 1 BLOG
    public boolean deleteTemporarily(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(true);
        blogRepository.save(blogEntity);
        return true;
    }

    public boolean restore(Long id) {
        BlogEntity blogEntity = getBlogEntityById(id);
        if (blogEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", id);
        blogEntity.setDeleted(false);
        blogRepository.save(blogEntity);
        return true;
    }

    public BlogResponse showDetail(Long aLong) {
        BlogEntity blogEntity = getBlogEntityById(aLong);
        BlogResponse blogResponse = blogMapper.toBlogResponse(getBlogEntityById(aLong));
        if (blogEntity.getBlogCategory() != null) {
            blogResponse.setTitle(blogEntity.getBlogCategory().getTitle());
            blogResponse.setId(blogEntity.getBlogCategory().getId());
        }

        return blogResponse;
    }

    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status,
            String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogEntity> blogEntityPage;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum,
                        pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogEntityPage = blogRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<BlogResponse> list = blogEntityPage.map(blogMapper::toBlogResponse);

        map.put("blogs", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status,
            String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogEntity> blogEntityPage;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum,
                        pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogEntityPage = blogRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogEntityPage = blogRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<BlogResponse> list = blogEntityPage.map(blogMapper::toBlogResponse);

        map.put("blogs", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    private String getSlug(String slug) {
        return Normalizer.normalize(slug, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .trim()
                .replaceAll("\\s+", "-")
                .toLowerCase();
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    // CHECK VIẾT HOA STATUS KHI TRUYỀN VÀO (ACTIVE, INACTIVE)
    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

    private BlogEntity getBlogEntityById(Long id) {
        return blogRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_NOT_FOUND));
    }

    private String getNameFile(String slug, int count) {
        String fileName;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        if (count <= 0) {
            return slug + "_" + timestamp;
        }
        return slug + "_" + timestamp + "_" + (count + 1);

    }

    private String uploadImageFromFile(MultipartFile file, String slug, int count) throws IOException {

        String fileName = getNameFile(slug, count);

        Map params = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", false,
                "folder", "blog",
                "public_id", fileName);

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    private void deleteImageFromCloudinary(String imageUrl) throws IOException {
        if (imageUrl != null) {
            Map options = ObjectUtils.asMap("invalidate", true);
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    // lấy hình từ ID
    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return publicId;
    }

    public List<BlogEntity> findByCategory(String category) {
        return blogRepository.findByBlogCategory_Name(category);
    }

}
