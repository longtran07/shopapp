import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment'; 
import { Product } from '../models/product';
import { ProductDTO } from '../dots/product/product.dto';
import { Category } from '../models/category';
import { ProductResponse } from '../responses/product/product.response';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiProductBase = `${environment.apiBaseUrl}/products`;

  constructor(private http: HttpClient) { }

  getProducts(keyword:string, categoryId:number, 
              page: number, limit: number
    ): Observable<Product[]> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('category_id', categoryId)
      .set('page', page.toString())
      .set('limit', limit.toString());            
    return this.http.get<Product[]>(this.apiProductBase, { params });
  }
  getDetailProduct(productId: number) {
    return this.http.get(`${this.apiProductBase}/${productId}`);
  }
  getProductById(productId: number): Observable<ProductResponse> {
    return this.http.get<ProductResponse>(`${this.apiProductBase}/${productId}`);
  }

  updateProduct(productData: any, images: File[]): Observable<any> {
    const formData = new FormData();
    
    // Thêm thông tin sản phẩm vào formData
  // Chuyển đổi productData thành JSON và thêm vào FormData với tên "product"
  formData.append('product', JSON.stringify(productData));
    
    // Thêm các file vào formData
    for (const file of images) {
      formData.append('images', file);
    }
  
    return this.http.put(`${this.apiProductBase}/${productData.id}`, formData); // Gọi API PUT
  }
  
  getProductsByIds(productIds: number[]): Observable<Product[]> {
    // Chuyển danh sách ID thành một chuỗi và truyền vào params
    debugger
    const params = new HttpParams().set('ids', productIds.join(',')); 
    return this.http.get<Product[]>(`${this.apiProductBase}/by-ids`, { params });
  }
  deleteProduct(productId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiProductBase}/${productId}`);
  }
  addProduct(formData: FormData): Observable<Product> {
    return this.http.post<Product>(this.apiProductBase, formData);
  }

  private apiGetCategories  = `${environment.apiBaseUrl}/categories`;

  getCategories(page: number, limit: number):Observable<Category[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());     
      return this.http.get<Category[]>(this.apiGetCategories, { params });           
  }
  createProduct(productData: ProductDTO): Observable<any> {
    return this.http.post<any>(this.apiProductBase, productData);
  }

  // Tải lên ảnh cho sản phẩm
  uploadProductImages(productId: number, files: File[]): Observable<any> {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file)); // Append từng file vào FormData
    return this.http.post<any>(`${this.apiProductBase}/uploads/${productId}`, formData);
  }
}
