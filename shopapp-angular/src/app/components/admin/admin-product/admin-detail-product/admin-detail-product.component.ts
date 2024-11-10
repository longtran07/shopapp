import { ProductResponse } from 'src/app/responses/product/product.response'; 
import { ProductService } from 'src/app/services/product.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Product } from 'src/app/models/product';
import { ProductImage } from 'src/app/models/product.image';
import { environment } from 'src/app/environments/environment';

@Component({
  selector: 'app-admin-detail-product',
  templateUrl: './admin-detail-product.component.html',
  styleUrls: ['./admin-detail-product.component.scss']
})
export class AdminDetailProductComponent implements OnInit {
  updateProductForm: FormGroup;
  selectedFiles: File[] = [];
  uploadError = false;
  uploadSuccess = false;
  productId: number =0 ;
  categories: any[] = []; // Danh sách các danh mục
  productImages: ProductImage[] = []; // Thêm biến để lưu danh sách ảnh sản phẩm


  constructor(
    private fb: FormBuilder,
    private productService: ProductService,
    private route: ActivatedRoute
  ) {
    this.updateProductForm = this.fb.group({
      name: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(1)]],
      description: ['', Validators.required],
      category_id: ['', Validators.required],
      id: [''] // Thêm trường id để gửi lên server
    });
  }

      
  

  // Tải danh sách danh mục từ backend
  loadCategories() {
    this.productService.getCategories(1, 100).subscribe({
      next: (response: any) => this.categories = response, // Cập nhật danh sách danh mục
      error: (error: any) => console.error('Lỗi khi tải danh mục:', error)
    });
  }


  ngOnInit(): void {
    this.loadCategories();
    this.productId = Number(this.route.snapshot.paramMap.get('id'));
    this.productService.getProductById(this.productId).subscribe(productResponse => {
      this.updateProductForm.patchValue(productResponse); // Load product data into the form
      if (productResponse.product_images && productResponse.product_images.length > 0) {
        this.productImages = productResponse.product_images.map((product_image: ProductImage) => ({
          product_id: product_image.product_id,
          image_url: `${environment.apiBaseUrl}/products/images/${product_image.image_url}` // Thêm URL đầy đủ
        }));
      } else {
        this.productImages = []; // Nếu không có ảnh, gán mảng rỗng
      }
    });
  }


  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      for (const file of Array.from(input.files)) {
        this.selectedFiles.push(file);
      }
    }
  }

  removeFile(index: number): void {
    this.selectedFiles.splice(index, 1);
  }

  onUpdateProduct(): void {
    debugger
    if (this.updateProductForm.valid) {
      debugger
      const productData = this.updateProductForm.value;
      this.productService.updateProduct(productData, this.selectedFiles).
      subscribe({
        
        next: (product: Product) => {
          debugger
          this.uploadSuccess = true;
          this.uploadError = false;
        },
        error: (error: any) => {
          debugger
          this.uploadError = true;
          this.uploadSuccess = false;
          // Handle error response
        }
      });
    }
  }

}
