package com.fpt.enewsvn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fpt.enewsvn.Enum.Status;
import com.fpt.enewsvn.dto.request.blog_category.CreateBlogCategoryRequest;
import com.fpt.enewsvn.dto.request.blog_category.UpdateBlogCategoryRequest;
import com.fpt.enewsvn.dto.response.BlogCategoryResponse;
import com.fpt.enewsvn.dto.response.BlogResponse;
import com.fpt.enewsvn.entity.BlogCategoryEntity;
import com.fpt.enewsvn.entity.BlogEntity;
import com.fpt.enewsvn.exception.AppException;
import com.fpt.enewsvn.exception.ErrorCode;
import com.fpt.enewsvn.mapper.BlogCategoryMapper;
import com.fpt.enewsvn.repository.BlogCategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BlogCategoryService {

    BlogCategoryRepository blogCategoryRepository;

    BlogCategoryMapper blogCategoryMapper;

    BlogService blogService;

    Cloudinary cloudinary;

    public List<BlogCategoryResponse> getFilteredCategories(String status, String keyword, String sortOrder) {
        // Lấy danh sách tất cả danh mục
        List<BlogCategoryResponse> categories = getAll();

        // Lọc theo trạng thái
        if (status != null && !status.isEmpty()) {
            categories = categories.stream()
                    .filter(category -> category.getStatus().equals(status))
                    .collect(Collectors.toList());
        }

        // Lọc theo từ khóa
        if (keyword != null && !keyword.isEmpty()) {
            categories = categories.stream()
                    .filter(category -> category.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Sắp xếp
        if ("asc".equals(sortOrder)) {
            categories.sort(Comparator.comparing(BlogCategoryResponse::getTitle));
        } else if ("desc".equals(sortOrder)) {
            categories.sort(Comparator.comparing(BlogCategoryResponse::getTitle).reversed());
        }

        return categories;
    }

    public boolean add(CreateBlogCategoryRequest request) {
        log.info("Request JSON: {}", request);
//        if(!blogCategoryRepository.existsByblogCategoryName(request.getBlogCategoryName())){
//            throw new AppException(ErrorCode.BLOG_CATEGORY_NAME_EXISTED);
//        }
        BlogCategoryEntity blogCategoryEntity = blogCategoryMapper.toBlogCategory(request);
        if (request.getPosition() == null || request.getPosition() <= 0) {
            Integer size = blogCategoryRepository.findAll().size();
            blogCategoryEntity.setPosition(size + 1);
        }

//        if (request.getImage() != null && !request.getImage().isEmpty()) {
//
//            List<String> images = new ArrayList<>();
//            int count = 0;
//            for (MultipartFile file : request.getImage()) {
//                try {
//                    String url = uploadImageFromFile(file, getSlug(request.getTitle()), count++);
//                    images.add(url);
//                } catch (IOException e) {
//                    log.error(e.getMessage());
//                    throw new RuntimeException(e);
//                }
//                blogCategoryEntity.setImage(images);
//            }
//        }
        blogCategoryEntity.setSlug(getSlug(request.getTitle()));
        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }

    public List<BlogCategoryResponse> getAll() {
        return blogCategoryMapper.toBlogCateroiesResponseDTO(blogCategoryRepository.findAll());
    }


    public BlogCategoryResponse update(Long id, UpdateBlogCategoryRequest request) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND);
        }

        if (StringUtils.hasLength(request.getStatus())) {
            blogCategoryEntity.setStatus(getStatus(request.getStatus()));
        }

        if (StringUtils.hasLength(request.getTitle())) {
            blogCategoryEntity.setSlug(getSlug(request.getTitle()));
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            for (String requests : blogCategoryEntity.getImage()) {
                try {
                    deleteImageFromCloudinary(requests);
                } catch (IOException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            int count = 0;
            List<String> images = new ArrayList<>();
            for (MultipartFile requests : request.getImage()) {
                String slug = getSlug(blogCategoryEntity.getTitle());

                if (StringUtils.hasLength(request.getTitle())) {
                    slug = getSlug(request.getTitle());
                }

                try {
                    String url = uploadImageFromFile(requests, slug, count++);
                    images.add(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            blogCategoryEntity.setImage(images);
        }
        blogCategoryMapper.updateBlogCategory(blogCategoryEntity, request);
        return blogCategoryMapper.toBlogCategoryResponse(blogCategoryRepository.save(blogCategoryEntity));
    }


    public String update(List<Long> id, String status) {
        Status statusEnum = getStatus(status);
        List<BlogCategoryEntity> blogCategoryEntities = blogCategoryRepository.findAllById(id);
        if (statusEnum == Status.ACTIVE || statusEnum == Status.INACTIVE) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setStatus(statusEnum));
            blogCategoryRepository.saveAll(blogCategoryEntities);
            return "Cặp nhật trạng thái thành công";
        } else if (statusEnum == Status.SOFT_DELETED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(true));
            blogCategoryRepository.saveAll(blogCategoryEntities);
            return "Xóa mềm danh mục blog thành công";
        } else if (statusEnum == Status.RESTORED) {
            blogCategoryEntities.forEach(productEntity -> productEntity.setDeleted(false));
            blogCategoryRepository.saveAll(blogCategoryEntities);
            return "Phục hồi danh mục blog thành công";
        }

        return "Cập nhật danh mục blog thất bại";
    }

//    @Override
//    public UserResponseDTO addOrder(Long id, CreateUserRequest request) {
//        return null;
//    }


    public boolean delete(Long id) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND);
        }

        if (blogCategoryEntity.getImage() == null || blogCategoryEntity.getImage().isEmpty()) {
            for (String url : blogCategoryEntity.getImage()) {
                try {
                    deleteImageFromCloudinary(url);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        log.info("Delete: {}", id);
        blogCategoryEntity.getBlog().forEach(blog -> blog.setBlogCategory(null));
        blogCategoryRepository.delete(blogCategoryEntity);
        return true;
    }

    public boolean delete(List<Long> longs) {
        List<BlogCategoryEntity> blogCategoryEntities = blogCategoryRepository.findAllById(longs);

        for (BlogCategoryEntity blogCategoryEntity : blogCategoryEntities) {
            if (blogCategoryEntity.getImage() == null || blogCategoryEntity.getImage().isEmpty()) {
                for (String url : blogCategoryEntity.getImage()) {
                    try {
                        deleteImageFromCloudinary(url);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        blogCategoryRepository.deleteAll(blogCategoryEntities);
        return true;
    }


    public boolean deleteTemporarily(Long id) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(id);

        log.info("Delete temporarily : {}", id);
        List<BlogEntity> blogEntities = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntities) {
            blogEntity.setBlogCategory(null);
        }
        blogCategoryEntity.setDeleted(true);
        List<BlogEntity> blogEntityList = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntityList) {
            blogEntity.setStatus(Status.INACTIVE);
        }
        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }


    public boolean restore(Long aLong) {
        BlogCategoryEntity blogCategoryEntity = getBlogCategoryEntityById(aLong);
        if (blogCategoryEntity == null) {
            throw new AppException(ErrorCode.BLOG_NOT_FOUND);
        }
        log.info("Delete temporarily : {}", aLong);
        blogCategoryEntity.setDeleted(false);

        List<BlogEntity> blogEntityList = blogCategoryEntity.getBlog();
        for (BlogEntity blogEntity : blogEntityList) {
            blogEntity.setStatus(Status.ACTIVE);
        }

        blogCategoryRepository.save(blogCategoryEntity);
        return true;
    }


    public BlogCategoryResponse showDetail(Long id) {
        return blogCategoryMapper.toBlogCategoryResponse(getBlogCategoryEntityById(id));
    }

    private Sort.Direction getSortDirection(String sortDirection) {

        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            log.info("SortDirection {} is invalid", sortDirection);
            throw new AppException(ErrorCode.SORT_DIRECTION_INVALID);
        }

        return sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public Map<String, Object> getAll(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogCategoryEntity> blogCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, false, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, false);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogCategoryEntities = blogCategoryRepository.findAllByDeleted(false, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findAllByStatusAndDeleted(statusEnum, false, pageable);
            }
        }

        Page<BlogCategoryResponse> list = blogCategoryEntities.map(blogCategoryMapper::toBlogCategoryResponse);

        map.put("blog_category", list.getContent());
        map.put("currentPage", list.getNumber() + 1);
        map.put("totalItems", list.getTotalElements());
        map.put("totalPages", list.getTotalPages());
        map.put("pageSize", list.getSize());
        return map;
    }

    public Map<String, Object> getTrash(int page, int size, String sortKey, String sortDirection, String status, String keyword) {
        Map<String, Object> map = new HashMap<>();

        Sort.Direction direction = getSortDirection(sortDirection);
        Sort sort = Sort.by(direction, sortKey);
        int p = (page > 0) ? page - 1 : 0;
        Pageable pageable = PageRequest.of(p, size, sort);

        Page<BlogCategoryEntity> blogCategoryEntities;

        // Tìm kiếm theo keyword trước
        if (keyword != null && !keyword.trim().isEmpty()) {
            if (status.equalsIgnoreCase("ALL")) {
                // Tìm kiếm theo tên sản phẩm, không lọc theo status
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndDeleted(keyword, true, pageable);
            } else {
                // Tìm kiếm theo tên sản phẩm và status
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findByTitleContainingIgnoreCaseAndStatusAndDeleted(keyword, statusEnum, pageable, true);
            }
        } else {
            // Nếu không có keyword, chỉ lọc theo status
            if (status == null || status.equalsIgnoreCase("ALL")) {
                blogCategoryEntities = blogCategoryRepository.findAllByDeleted(true, pageable);
            } else {
                Status statusEnum = getStatus(status);
                blogCategoryEntities = blogCategoryRepository.findAllByStatusAndDeleted(statusEnum, true, pageable);
            }
        }

        Page<BlogCategoryResponse> list = blogCategoryEntities.map(blogCategoryMapper::toBlogCategoryResponse);

        map.put("blog_category", list.getContent());
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

    //CHECK VIẾT HOA STATUS KHI TRUYỀN VÀO (ACTIVE, INACTIVE)
    private Status getStatus(String status) {
        try {
            return Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status provided: '{}'", status);
            throw new AppException(ErrorCode.STATUS_INVALID);
        }
    }

//    @Override
//    public UserResponseDTO addOrder(Long id, OrderRequest request) {
//        return null;
//    }

    private BlogCategoryEntity getBlogCategoryEntityById(Long id) {
        return blogCategoryRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.BLOG_CATEGORY_NOT_FOUND));
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
                "folder", "blog-category",
                "public_id", fileName
        );

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


    //lấy hình từ ID
    private String extractPublicId(String imageUrl) {
        String temp = imageUrl.substring(imageUrl.indexOf("upload/") + 7);
        String publicId = temp.substring(temp.indexOf("/") + 1, temp.lastIndexOf("."));
        System.out.println(publicId);
        return publicId;
    }

    /*

    User
     */



    public static String generateSummary(String content, int maxLines) {
        if (content == null || maxLines <= 0) {
            return "";
        }

        // Dùng Jsoup để parse HTML
        Document doc = Jsoup.parse(content);
        Element body = doc.body();

        // Chọn tất cả thẻ con của <body> (thường là <p>, <div>, ...)
        Elements elements = body.children();

        StringBuilder summary = new StringBuilder();
        int lines = 0;

        for (Element element : elements) {
            if (lines >= maxLines) {
                break; // Dừng lại sau khi đủ số dòng
            }
            summary.append(element.outerHtml()).append("\n");
            lines++;
        }

        return summary.toString();
    }


    private List<BlogCategoryResponse> mapToCategoryResponse(List<BlogCategoryEntity> blogCategoryEntities) {
        List<BlogCategoryResponse> blogCategoryResponses = new ArrayList<>();

        blogCategoryEntities.forEach(blogCategoryEntity -> {
            BlogCategoryResponse blogCategoryResponse = new BlogCategoryResponse();
            blogCategoryResponse.setId(blogCategoryEntity.getId());
            blogCategoryResponse.setTitle(blogCategoryEntity.getTitle());
            blogCategoryResponse.setSlug(blogCategoryEntity.getSlug());
            List<BlogResponse> blogResponses = new ArrayList<>();

            blogCategoryEntity.getBlog().forEach(blogEntity -> {
                BlogResponse blogResponse = new BlogResponse();
                blogResponse.setId(blogEntity.getId());
                blogResponse.setAuthor(blogEntity.getAuthor());
                blogResponse.setThumbnail(blogEntity.getThumbnail());
                blogResponse.setTitle(blogEntity.getTitle());
                blogResponse.setSlug(blogEntity.getSlug());
                blogResponse.setContent(blogEntity.getContent());
                blogResponse.setCreatedAt(blogEntity.getCreatedAt());
                blogResponse.setFeatured(blogEntity.isFeatured());
                blogResponses.add(blogResponse);
            });

            blogCategoryResponse.setBlogs(blogResponses);
            blogCategoryResponses.add(blogCategoryResponse);
        });

        return blogCategoryResponses;
    }





}
