import { CommentResponse } from './../responses/product/comment.response';
import { environment } from './../environments/environment';
// src/app/services/comment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private baseUrl = `${environment.apiBaseUrl}/comments`;

  constructor(private http: HttpClient) {}

  // Lấy bình luận theo sản phẩm, trả về mảng CommentResponse
  getCommentsByProduct(productId: number): Observable<CommentResponse[]> {
    return this.http.get<CommentResponse[]>(`${this.baseUrl}?product_id=${productId}`);
  }

  // Thêm bình luận
  addComment(comment: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, comment);
  }

  // Cập nhật bình luận
  updateComment(commentId: number, comment: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${commentId}`, comment);
  }
}
