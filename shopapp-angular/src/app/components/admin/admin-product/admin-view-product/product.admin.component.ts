import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { Product } from 'src/app/models/product';
import { Router } from '@angular/router';
import { environment } from 'src/app/environments/environment'; 
@Component({
  selector: 'app-product-admin',
  templateUrl: './product.admin.component.html',
  styleUrls: ['./product.admin.component.scss'],
})
export class ProductAdminComponent implements OnInit {
  products: Product[] = [];
  selectedCategoryId: number  = 0; // Giá trị category được chọn
  currentPage: number = 0;
  itemsPerPage: number = 10;
  pages: number[] = [];
  totalPages:number = 0;
  visiblePages: number[] = [];
  keyword:string = "";

  constructor(
    private productService: ProductService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts(); // Tải danh sách sản phẩm khi component khởi tạo
  }

  loadProducts(): void {
    this.productService.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage).subscribe({
      next: (response: any) => {
        debugger
        response.products.forEach((product: Product) => {          
          product.url = `${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
        });
        this.products = response.products;
        this.totalPages = response.totalPages;
        this.pages = Array.from({ length: this.totalPages }, (_, i) => i);
      this.visiblePages = this.generateVisiblePageArray(this.currentPage, this.totalPages);
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        console.error('Error fetching products:', error);
      }
    });    
  }

  addProduct(): void {
    this.router.navigate(['/admin/products/add']); // Chuyển đến trang thêm sản phẩm
  }

  viewProduct(productId: number): void {
    this.router.navigate([`/admin/products/${productId}`]); // Chuyển đến trang chi tiết sản phẩm
  }

  deleteProduct(productId: number): void {
    // Thực hiện xóa sản phẩm
    this.productService.deleteProduct(productId).subscribe({
      next: () => {
        this.loadProducts(); // Tải lại danh sách sản phẩm
      },
      error: (error: any) => {
        console.error('Error deleting product:', error);
      },
    });
  }

  onPageChange(page: number) {
    debugger;
    this.currentPage = page;
    this.loadProducts();
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    debugger
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 0);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages-1);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1).fill(0)
        .map((_, index) => startPage + index);
  }

  
}
