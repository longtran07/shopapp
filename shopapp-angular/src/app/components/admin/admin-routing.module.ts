import { AdminComponent } from "./admin.component";
import { OrderAdminComponent } from "./order/order.admin.component";
import { DetailOrderAdminComponent } from "./detail-order/detail.order.admin.component";
import { Route, Router,Routes } from "@angular/router";
import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CategoryAdminComponent } from "./category/category.admin.component";
import { AdminAddProductComponent } from "./admin-product/admin-add-product/admin-add-product.component";
import { AdminDetailProductComponent } from "./admin-product/admin-detail-product/admin-detail-product.component";
import { ProductAdminComponent } from "./admin-product/admin-view-product/product.admin.component";


const routes: Routes = [
    {
        path: 'admin',
        component: AdminComponent,
        children: [
            {
                path: 'orders',
                component: OrderAdminComponent
            },
            {
                path: 'orders/:id',
                component: DetailOrderAdminComponent
            },
            {
                path: 'products',
                component: ProductAdminComponent
            },
            {
                path: 'categories',
                component: CategoryAdminComponent
            },
            { path: 'products', component: ProductAdminComponent },
            { path: 'products/add', component: AdminAddProductComponent }, // Component thêm sản phẩm
            { path: 'products/:id', component: AdminDetailProductComponent }, // 
        
        ]
    }
];
@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
    exports: [RouterModule]
})
export class AdminRoutingModule { }
