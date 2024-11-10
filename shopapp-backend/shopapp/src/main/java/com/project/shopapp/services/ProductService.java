package com.project.shopapp.services;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.utils.MessageKeys;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory =categoryRepository
                .findById(productDTO.getCategoryId()).orElseThrow(()->
                new DataNotFoundException(
                        "Can not find category with id :" + productDTO.getCategoryId()));
        Product newProduct=Product
                .builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        Optional<Product> optionalProduct = productRepository.getDetailProduct(productId);
        if(optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException("Cannot find product with id =" + productId);
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId,
                                                    PageRequest pageRequest) {
        // lấy dsach spham theo page và limit
        Page<Product> productPage;
        productPage=productRepository.searchProducts(categoryId,keyword,pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO, MultipartFile[] images) throws Exception {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Product does not exist with id: " + id));

        // Cập nhật thông tin sản phẩm
        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setCategory(categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Cannot find category with id: " + productDTO.getCategoryId())));

        // Xử lý hình ảnh nếu có
        if (images != null && images.length > 0) {
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : images) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra và lưu file
                String filename = storeFile(file); // Lưu hình ảnh và lấy tên file
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(filename)
                        .product(existingProduct)
                        .build();
                productImages.add(productImage);
            }
            // Lưu tất cả hình ảnh vào DB
            productImageRepository.saveAll(productImages);
            // Cập nhật thumbnail nếu chưa có
            if (existingProduct.getThumbnail() == null || existingProduct.getThumbnail().isEmpty()) {
                existingProduct.setThumbnail(productImages.get(0).getImageUrl()); // Sử dụng hình ảnh đầu tiên làm thumbnail
            }
        }

        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            productRepository.delete(optionalProduct.get());
        } else {
            throw new EntityNotFoundException("Product not found with id: " + id);
        }
    }   

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
    @Transactional
    public ProductImage createProductImage(
            Long product_id,
            ProductImageDTO productImageDTO) throws DataNotFoundException, InvalidParamException {
        Product existingProduct =productRepository
                .findById(product_id).orElseThrow(()->
                        new DataNotFoundException(
                                "Can not find product with id :" + productImageDTO.getProductId()));
        ProductImage newProductImage=ProductImage
                .builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // không cho insert quuas 5 ảnh cho 1 sản phẩm
        int size = productImageRepository.findByProductId(product_id).size();
        if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT ) {
            throw new InvalidParamException("Number if images must be <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }
    @Override
    public void updateProductThumbnail(Product product) {
        productRepository.save(product);
    }


    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
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
}
