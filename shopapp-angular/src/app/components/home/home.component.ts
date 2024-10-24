
import { CategoryService } from './../../services/category.service';
import { ProductService } from './../../services/product.service';
import { Component } from '@angular/core';
import { Product } from 'src/app/models/product';
import { Category } from 'src/app/models/category';
import { environment } from 'src/app/environments/environment';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  products: Product[] = [];
  categories: Category[]=[];
  selectedCategoryId: number = 0;
  currentPage : number =1;
  itemsPerPage : number =12;
  pages : number[]=[];
  totalPages  : number = 0;
  visiblePages: number[] = [];
  keyword : string="";

  constructor(
    private productService : ProductService,
    private categoryService: CategoryService,
    private router: Router

  ){}
  ngOnInit(){
    this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage);
    this.getCategories(1,100);
  }
  getCategories(page: number,limit : number){
    this.categoryService.getCategories(page,limit).subscribe({
      next: (categories: Category[])=> {
          debugger
          this.categories=categories;
      },
      complete:() => {
          debugger
      },
      error:(error: any)=> {
          console.error('Error fetching categories:', error)
      },
    })
  }
  onPageChange(page: number) {
    debugger;
    this.currentPage = page;
    this.getProducts( this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage);
  }

  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1).fill(0).map((_, index) => startPage + index);
  }
  // onProductClick(productId: number) {
  //   debugger
  //   // Điều hướng đến trang detail-product với productId là tham số
  //   this.router.navigate(['/detail-product', productId]);
  // }
    // Hàm xử lý sự kiện khi sản phẩm được bấm vào
    onProductClick(productId: number) {
      debugger
      // Điều hướng đến trang detail-product với productId là tham số
      this.router.navigate(['/products', productId]);
    }  

  searchProducts() {
    this.currentPage = 1;
    this.itemsPerPage = 12;
    debugger
    this.getProducts(this.keyword, this.selectedCategoryId, this.currentPage, this.itemsPerPage);
  }

  getProducts( keyword: string, selectedCategoryId: number, page: number, limit: number){
    debugger
    this.productService.getProducts(keyword, selectedCategoryId, page, limit).subscribe({
      next:(response: any)=>{
        debugger
        response.products.forEach((product: Product) => {
            product.url =`${environment.apiBaseUrl}/products/images/${product.thumbnail}`;
        });
        this.products=response.products;
        this.totalPages=response.totalPages;
        this.visiblePages=response.visiblePages;
      },
      complete() {
          debugger;
      },
      error(error: any) {
          debugger;
          console.error('Error fetching products:', error)
      },
    })

  }
}
