import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from 'src/app/services/product.service';
import { ProductDTO } from 'src/app/dots/product/product.dto'; 
import { Product } from 'src/app/models/product';
import { ProductImage } from 'src/app/models/product.image';
import { Pipe, PipeTransform } from '@angular/core';

@Component({
  selector: 'app-admin-add-product',
  templateUrl: './admin-add-product.component.html',
  styleUrls: ['./admin-add-product.component.scss']
})
export class AdminAddProductComponent implements OnInit {
  addProductForm: FormGroup; // FormGroup cho form thêm sản phẩm
  categories: any[] = []; // Danh sách các danh mục
  productId: number | null = null; // ID của sản phẩm vừa tạo
  selectedFiles: File[] = []; // Mảng chứa ảnh được chọn
  uploadSuccess: boolean = false; // Trạng thái tải lên ảnh thành công
  uploadError: boolean = false; // Trạng thái có lỗi khi tải lên ảnh
  productData: ProductDTO = { // Dữ liệu sản phẩm
    name: '',
    price: 1,
    description: '',
    category_id: 1
  };

  constructor(
    private productService: ProductService,
    private router: Router,
    private formBuilder: FormBuilder
  ) {
    // Khởi tạo form với các validator
    this.addProductForm = this.formBuilder.group({
      name: ['', Validators.required],
      price: ['', [Validators.required, Validators.min(1)]],
      description: ['', [Validators.required, Validators.minLength(6)]],
      category_id: ['', Validators.required] // Bắt buộc phải chọn một danh mục
    });
  }

  ngOnInit() {
    // Tải danh sách danh mục từ backend
    this.loadCategories();
  }

  // Tải danh sách danh mục từ backend
  loadCategories() {
    this.productService.getCategories(1, 100).subscribe({
      next: (response: any) => this.categories = response, // Cập nhật danh sách danh mục
      error: (error: any) => console.error('Lỗi khi tải danh mục:', error)
    });
  }

  // Hàm tạo sản phẩm
  onCreateProduct(): void {
    if (this.addProductForm.valid) {
      this.productData = { ...this.productData, ...this.addProductForm.value }; // Cập nhật `productData`

      this.productService.createProduct(this.productData).subscribe({
        next: (product: Product) => {
          this.productId = product.id; // Lưu ID sản phẩm vừa tạo
          this.uploadImages(); // Gọi hàm tải lên ảnh
        },
        error: (error: any) => {
          console.error('Lỗi khi tạo sản phẩm:', error);
          alert('Lỗi khi tạo sản phẩm');
          this.uploadError = true; // Cập nhật trạng thái lỗi
        }
      });
    } else {
      alert('Vui lòng điền đầy đủ thông tin');
    }
  }

  // Hàm tải lên ảnh
  uploadImages(): void {
    if (this.productId && this.selectedFiles.length > 0) {
      this.productService.uploadProductImages(this.productId, this.selectedFiles).subscribe({
        next: (images: ProductImage[]) => {
          console.log('Uploaded images:', images); // Hiển thị thông tin các ảnh đã tải lên
          this.uploadSuccess = true; // Cập nhật trạng thái thành công khi tải lên ảnh
          alert('Tạo sản phẩm thành công!');
          this.router.navigate(['/admin/products']); // Điều hướng về trang danh sách sản phẩm
        },
        error: (error: any) => {
          console.error('Lỗi khi tải lên ảnh:', error);
          alert('Lỗi khi tải lên ảnh');
          this.uploadError = true; // Cập nhật trạng thái lỗi khi tải lên ảnh
        }
      });
    } else {
      alert('Chọn ít nhất một ảnh và tạo sản phẩm trước');
    }
  }

  

  // Hàm xử lý sự kiện chọn file

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      // Chuyển đổi danh sách FileList thành mảng
      this.selectedFiles = Array.from(input.files);
    }
  }

  // Hàm xóa file đã chọn
  removeFile(index: number): void {
    const file = this.selectedFiles[index];
    if (file) {
      URL.revokeObjectURL(file.name); // Giải phóng URL
    }
    this.selectedFiles.splice(index, 1); // Xóa file tại vị trí index
  }
}
