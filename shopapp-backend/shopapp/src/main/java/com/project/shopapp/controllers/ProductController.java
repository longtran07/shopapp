package com.project.shopapp.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.*;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.services.product.IProductRedisService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {
    private final IProductService productService;
    private final IProductRedisService productRedisService;
    private final LocalizationUtils localizationUtils;
    private static final Logger logger= LoggerFactory.getLogger(ProductController.class);

    @PostMapping("")
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result
    ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(value = "uploads/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    //POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @ModelAttribute("files") List<MultipartFile> files
    ){
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT ){
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKeys.ERROR_MAXIMUM_IMAGE));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if(file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if(file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.FILE_LARGE));
                }
                String contentType = file.getContentType();
                if(contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body(localizationUtils.getLocalizedMessage(MessageKeys.FILE_MUST_BE_IMAGE));
                }
                // Lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file); // Thay thế hàm này với code của bạn để lưu file
                //lưu vào đối tượng product trong DB
                ProductImage productImage = productService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build()
                );
                productImages.add(productImage);
                if (existingProduct.getThumbnail() == null || existingProduct.getThumbnail().isEmpty()) {
                    existingProduct.setThumbnail(filename);
                    productService.updateProductThumbnail(existingProduct);
                }
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName){
        try{
            Path imagePath=Paths.get("uploads/"+imageName);
            UrlResource resource=new UrlResource(imagePath.toUri());

            if(resource.exists()){
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
            }
            else{
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
            }
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }

    }
    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file ) || file.getOriginalFilename() == null){
            throw new IOException("Invalid image file format");
        }
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads");
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    private boolean isImageFile(MultipartFile file){
        String contentType=file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword ,
            @RequestParam(defaultValue = "0",name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0")     int page,
            @RequestParam(defaultValue = "10")    int limit
    ) throws JsonProcessingException {int totalPages = 0;

        // tạo pageable t thông tin trang và giới hạn
        PageRequest pageRequest=PageRequest.of(
                page,limit,
                Sort.by("id").ascending());
        logger.info(String.format("keyword = %s, category_id = %d",keyword,categoryId,page,limit));
        List<ProductResponse> productResponses = productRedisService
                .getAllProducts(keyword, categoryId, pageRequest);
        if (productResponses!=null && !productResponses.isEmpty()) {
            System.out.println("redis");
            totalPages = productResponses.get(0).getTotalPages();
        }
        if(productResponses == null) {
            Page<ProductResponse> productPage = productService
                    .getAllProducts(keyword, categoryId, pageRequest);
            // Lấy tổng số trang
            totalPages = productPage.getTotalPages();
            productResponses = productPage.getContent();
            // Bổ sung totalPages vào các đối tượng ProductResponse
            for (ProductResponse product : productResponses) {
                product.setTotalPages(totalPages);
            }
            productRedisService.saveAllProducts(
                    productResponses,
                    keyword,
                    categoryId,
                    pageRequest
            );
        }

        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(productResponses)
                .totalPages(totalPages)
                .build());

    }
    //http://localhost:8088/api/v1/products/6
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            if (existingProduct == null) {
                return ResponseEntity.notFound().build(); // Trả về 404 nếu không tìm thấy sản phẩm
            }return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()); // Trả về 500 nếu có lỗi
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the product.");
        }
    }

        @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts(){

        Faker faker=new Faker();
        for(int i=0;i< 1_000_000;i++){
            String productName=faker.commerce().productName();
            if(productService.existsByName(productName)) continue;
            ProductDTO productDTO=ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10,90_00))
                    .description(faker.lorem().sentence())
                    .categoryId((long)faker.number().numberBetween(1,3))
                    .build();
            try {
                productService.createProduct(productDTO );
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }

        }
        return ResponseEntity.ok("");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable long id,
            @RequestParam("product") String productJson,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws Exception {
        try {
            // Chuyển đổi JSON sang đối tượng ProductDTO
            ObjectMapper objectMapper = new ObjectMapper();
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

            // Cập nhật sản phẩm và xử lý hình ảnh
            Product updatedProduct = productService.updateProduct(id, productDTO, images);
            return ResponseEntity.ok(updatedProduct);
        } catch (DataNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Lỗi khi chuyển đổi dữ liệu.");
        }
    }


}