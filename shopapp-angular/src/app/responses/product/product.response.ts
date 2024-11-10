import { ProductDTO } from "src/app/dots/product/product.dto";
import { ProductImage } from "src/app/models/product.image";

export interface ProductResponse extends ProductDTO { // Kế thừa từ ProductDTO
    product_images: ProductImage[]; // Danh sách các hình ảnh của sản phẩm
    createdAt?: string; // Thời gian tạo (nếu có)
    updatedAt?: string; // Thời gian cập nhật (nếu có)
  }