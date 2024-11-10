package com.project.shopapp.services;

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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;

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
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if(existingProduct != null){
            // copy các thuộc tính từ DTO
            // Có thể sử dụng ModelMaping
            Category existingCategory =categoryRepository
                    .findById(productDTO.getCategoryId()).orElseThrow(()->
                            new DataNotFoundException(
                                    "Can not find category with id :" + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;

    }

    @Override
    @Transactional
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()){
            productRepository.delete(optionalProduct.get());
        }

    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
    @Override
//    public ProductImage createProductImage(Long productId, ProductImageDTO productImageDTO) throws Exception {
//        Product existingProduct = productRepository
//                .findById(productId)
//                .orElseThrow(() -> new DataNotFoundException("Không tìm thấy sản phẩm với id = " + productId));
//
//        int currentImageCount = productImageRepository.findByProductId(productId).size();
//        if (currentImageCount >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
//            throw new InvalidParamException("Số lượng ảnh phải <= " + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
//        }
//
//        ProductImage newProductImage = ProductImage.builder()
//                .product(existingProduct)
//                .imageUrl(productImageDTO.getImageUrl())
//                .build();
//
//        return productImageRepository.save(newProductImage);
//    }
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
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }


}
