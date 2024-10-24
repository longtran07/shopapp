import { UserProfileComponent } from './components/user-profile/user.profile.component';
import { NgModule } from '@angular/core';
import { HttpClientModule,HTTP_INTERCEPTORS } from '@angular/common/http';
import { BrowserModule } from '@angular/platform-browser';
import { HomeComponent } from './components/home/home.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { DetailProductComponent } from './components/detail-product/detail-product.component';
import { OrderComponent } from './components/order/order.component';
import { OrderDetailComponent } from './components/order-detail/order.detail.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TokenInterceptor } from './interceptor/token.interceptor';
import { AppComponent } from './app/app.component';
import { AppRoutingModule } from './app-routing.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AdminComponent } from './admin/admin.component';




@NgModule({
  declarations: [   
    HomeComponent, 
    HeaderComponent,
    FooterComponent, 
    DetailProductComponent, 
    OrderComponent, 
    OrderDetailComponent, 
    LoginComponent, 
    RegisterComponent,
    AppComponent,
    UserProfileComponent,
    AdminComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    ReactiveFormsModule,
    AppRoutingModule,
    NgbModule

  ],
  providers: [
    {
      provide : HTTP_INTERCEPTORS,
      useClass : TokenInterceptor,
      multi: true,
    },

  ],
  bootstrap: [
    AppComponent
    // HomeComponent,
    // DetailProductComponent,
    // /OrderComponent,
    // OrderDetailComponent,
    // LoginComponent,
    // RegisterComponent
  ]
})
export class AppModule { }
