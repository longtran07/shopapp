import { NgModule } from '@angular/core';
import { AdminComponent } from './admin.component';
import { OrderAdminComponent } from './order/order.admin.component';
import { DetailOrderAdminComponent } from './detail-order/detail.order.admin.component';

import { CategoryAdminComponent } from './category/category.admin.component';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AdminRoutingModule } from './admin-routing.module';

import { SafeUrlPipe } from './pipes/safe-url.pipe';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminAddProductComponent } from './admin-product/admin-add-product/admin-add-product.component';
import { AdminDetailProductComponent } from './admin-product/admin-detail-product/admin-detail-product.component';
import { ProductAdminComponent } from './admin-product/admin-view-product/product.admin.component';
// import { AdminUpdateProductComponent } from './admin-update-product/admin-update-product.component';

@NgModule({
  declarations: [
    AdminComponent,
    OrderAdminComponent,
    DetailOrderAdminComponent,
    ProductAdminComponent,
    CategoryAdminComponent,
    AdminAddProductComponent,
    AdminDetailProductComponent,
    SafeUrlPipe,
    // AdminUpdateProductComponent
  ],
  imports: [
    AdminRoutingModule, // import routes,
    CommonModule,
    FormsModule,
    NgbModule,   
    ReactiveFormsModule
    
  ]
})
export class AdminModule {}

