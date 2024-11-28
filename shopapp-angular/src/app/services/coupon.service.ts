import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/app/environments/environment';
import { CouponResponse } from '../responses/order/coupon.response';

@Injectable({
  providedIn: 'root'
})
export class CouponService {
  private apiUrl = `${environment.apiBaseUrl}/coupons/calculate`;

  constructor(private http: HttpClient) {}

  calculateCouponValue(couponCode: string, totalAmount: number): Observable<CouponResponse> {
    const params = new HttpParams()
      .set('couponCode', couponCode)
      .set('totalAmount', totalAmount.toString());
      return this.http.get<CouponResponse>(this.apiUrl, { params });
  }
}