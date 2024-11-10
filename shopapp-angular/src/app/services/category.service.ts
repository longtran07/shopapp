import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { environment } from '../environments/environment'; 
import { Category } from '../models/category';
@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiGetCategories  = `${environment.apiBaseUrl}/categories`;

  constructor(private http: HttpClient) { }

  getCategories(page: number, limit: number):Observable<Category[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('limit', limit.toString());     
      return this.http.get<Category[]>(this.apiGetCategories, { params });           
  }

  createCategory(categoryData: { name: string }): Observable<any> {
    return this.http.post<any>(`${this.apiGetCategories}`, categoryData); // Sử dụng endpoint POST '/'
  }
   
  updateCategory(categoryData:{name : string}):Observable<any>{
    return this.http.put<any>(`${this.apiGetCategories}`, categoryData);
  }

  deleteCategory(categoryId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiGetCategories}/${categoryId}`).pipe(
      catchError((error) => {
        if (error.status === 404) {
          console.error('Category not found');
          // Hiển thị thông báo lỗi nếu không tìm thấy category
        }
        return throwError(error);
      })
    );
  }
  

}
