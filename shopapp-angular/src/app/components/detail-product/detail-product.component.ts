import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/app/services/product.service';
import { CartService } from 'src/app/services/cart.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Product } from '../../models/product';
import { ProductImage } from 'src/app/models/product.image';
import { environment } from 'src/app/environments/environment';
import { CommentService } from 'src/app/services/comment.service';
import { UserService } from 'src/app/services/user.service';
import { CommentResponse } from 'src/app/responses/product/comment.response';

@Component({
  selector: 'app-detail-product',
  templateUrl: './detail-product.component.html',
  styleUrls: ['./detail-product.component.scss']
})

export class DetailProductComponent implements OnInit {
  product?: Product;
  productId: number = 0;

  comments: CommentResponse[] = [];
  
  newComment: string = '';
  currentImageIndex: number = 0;
  quantity: number = 1;
  constructor(
    private commentService: CommentService,
    private productService: ProductService,
    private cartService: CartService,
    private userService: UserService,
    // private categoryService: CategoryService,
    // private router: Router,
      private activatedRoute: ActivatedRoute,
      private router: Router,
    ) {
      
    }
    ngOnInit() {
      // Lấy productId từ URL      
      const idParam = this.activatedRoute.snapshot.paramMap.get('id');
      debugger
      //this.cartService.clearCart();
      //const idParam = 9 //fake tạm 1 giá trị
      if (idParam !== null) {
        this.productId = +idParam;
      }
      if (!isNaN(this.productId)) {
        this.productService.getDetailProduct(this.productId).subscribe({
          next: (response: any) => {            
            // Lấy danh sách ảnh sản phẩm và thay đổi URL
            debugger
            if (response.product_images && response.product_images.length > 0) {
              response.product_images.forEach((product_image:ProductImage) => {
                product_image.image_url = `${environment.apiBaseUrl}/products/images/${product_image.image_url}`;
              });
            }            
            debugger
            this.product = response 
            // Bắt đầu với ảnh đầu tiên
            this.showImage(0);
          },
          complete: () => {
            this.loadComments();
            debugger;
          },
          error: (error: any) => {
            debugger;
            console.error('Error fetching detail:', error);
          }
        });    
      } else {
        console.error('Invalid productId:', idParam);
      }  
      this.loadComments();    
    }
    showImage(index: number): void {
      debugger
      if (this.product && this.product.product_images && 
          this.product.product_images.length > 0) {
        // Đảm bảo index nằm trong khoảng hợp lệ        
        if (index < 0) {
          index = 0;
        } else if (index >= this.product.product_images.length) {
          index = this.product.product_images.length - 1;
        }        
        // Gán index hiện tại và cập nhật ảnh hiển thị
        this.currentImageIndex = index;
      }
    }


    loadComments() : void{
      this.commentService.getCommentsByProduct(this.productId).subscribe({
        next: (comments: CommentResponse[]) => {
          this.comments = comments;
        },
        error: (error: any) => {
          console.error('Error fetching categories:', error);
        }
      });
    }



    addComment(): void {
      debugger
      const userResponse = this.userService.getUserResponseFromLocalStorage();  // Lấy thông tin người dùng từ localStorage
  
      if (!userResponse) {
        alert('You must be logged in to add a comment');
        return;  // Nếu chưa đăng nhập, thông báo lỗi
      }
  
      if (this.newComment.trim()) {
        debugger
        const comment = {
          user_id: userResponse.id,  // Sử dụng userId từ userResponse
          product_id: this.productId,
          content: this.newComment
        };

        this.commentService.addComment(comment).subscribe({
          next: (comments: any) => {
            this.loadComments();
            this.newComment = '';
          },
          error: (error: any) => {
            console.error('Error fetching categories:', error);
          }
        });
  
      }
    }
  
    updateComment(comment: CommentResponse): void {
      const userResponse = this.userService.getUserResponseFromLocalStorage();  
  
      if (comment.user.id !== userResponse?.id) {
        alert('You cannot edit another user\'s comment');
        return;
      }
      this.loadComments();
  
      // this.commentService.updateComment(comment.id, comment).subscribe(() => {
      //   this.loadComments();
      // });
    }

    thumbnailClick(index: number) {
      debugger
      // Gọi khi một thumbnail được bấm
      this.currentImageIndex = index; // Cập nhật currentImageIndex
    }  
    nextImage(): void {
      debugger
      this.showImage(this.currentImageIndex + 1);
    }
  
    previousImage(): void {
      debugger
      this.showImage(this.currentImageIndex - 1);
    }      
    addToCart(): void {
      debugger
      if (this.product) {
        this.cartService.addToCart(this.product.id, this.quantity);
      } else {
        // Xử lý khi product là null
        console.error('Không thể thêm sản phẩm vào giỏ hàng vì product là null.');
      }
    }    
        
    increaseQuantity(): void {
      this.quantity++;
    }
    
    decreaseQuantity(): void {
      if (this.quantity > 1) {
        this.quantity--;
      }
    }
    
    buyNow(): void {      
      this.router.navigate(['/orders']);
    }    
}
