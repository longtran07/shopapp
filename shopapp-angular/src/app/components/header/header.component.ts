import { Router,ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserResponse } from 'src/app/responses/user/user.response';
import { NgbPopoverConfig } from '@ng-bootstrap/ng-bootstrap';
import { TokenService } from 'src/app/services/token.service';
import { Category } from 'src/app/models/category';
import { CategoryService } from 'src/app/services/category.service';
import { Product } from 'src/app/models/product';
import { ProductService } from 'src/app/services/product.service';
import { environment } from 'src/app/environments/environment';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit{
  userResponse?:UserResponse | null;

  isPopoverOpen = false;
  activeNavItem: number = 0;

  notificationsVisible: boolean = false;
  notifications = [
    { message: 'Đơn hàng của bạn đã được xác nhận', time: '10 phút trước' },
    { message: 'Bạn có một tin nhắn mới', time: '1 giờ trước' },
    { message: 'Mã giảm giá của bạn sắp hết hạn', time: '1 ngày trước' }
  ];

  toggleNotifications(): void {
    this.notificationsVisible = !this.notificationsVisible;
  }

  clearNotifications(): void {
    this.notifications = [];
  }



  togglePopover(event: Event): void {
    event.preventDefault();
    this.isPopoverOpen = !this.isPopoverOpen;
  }
  handleItemClick(index: number): void {
    if (index === 0) {
      this.router.navigate(['/user-profile']);
    } else if (index === 1) {
      this.router.navigate(['/order-list']);
    } else if (index === 2) {
      this.userService.removeUserFromLocalStorage();
      this.tokenService.removeToken();
      this.userResponse = this.userService.getUserResponseFromLocalStorage();
      this.router.navigate(['/']);
    }
    this.isPopoverOpen = false;
  }
  setActiveNavItem(index: number) {    
    this.activeNavItem = index;
    //alert(this.activeNavItem);
  }
 
  generateVisiblePageArray(currentPage: number, totalPages: number): number[] {
    const maxVisiblePages = 5;
    const halfVisiblePages = Math.floor(maxVisiblePages / 2);

    let startPage = Math.max(currentPage - halfVisiblePages, 1);
    let endPage = Math.min(startPage + maxVisiblePages - 1, totalPages);

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(endPage - maxVisiblePages + 1, 1);
    }

    return new Array(endPage - startPage + 1).fill(0)
        .map((_, index) => startPage + index);
  }

  constructor(
    private userService: UserService,   
    private popoverConfig: NgbPopoverConfig,  
    private tokenService: TokenService,   
    private router:Router,

  ) {
    
   }
  ngOnInit() {
    this.userResponse = this.userService.getUserResponseFromLocalStorage();    
  }  
}
