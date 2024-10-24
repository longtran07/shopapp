import { Injectable } from "@angular/core";
import { environment } from "../environments/environment";
import { HttpClient } from "@angular/common/http";
import { OrderDTO } from "../dots/order/order.dto";
import { Observable } from "rxjs";

@Injectable({
    providedIn:'root',
})

export class OrderService{
    private apiUrl=`${environment.apiBaseUrl}/orders`;
    private apiGetAllOrdes=`${environment.apiBaseUrl}/orders/get-orders-by-keyword`;

    constructor(private http: HttpClient){}

    placeOrder(orderData : OrderDTO): Observable<any>{
        return this.http.post(this.apiUrl,orderData);
    }

    getOrderById(orderId: number): Observable<any> {
        const url = `${environment.apiBaseUrl}/orders/${orderId}`;
        return this.http.get(url);
      }
}