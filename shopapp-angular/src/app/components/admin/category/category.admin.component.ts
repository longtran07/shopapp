import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { environment } from './../../../environments/environment';
import { Component, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModalRef,NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Category } from 'src/app/models/category';
import { CategoryService } from 'src/app/services/category.service';

@Component({
  selector: 'app-category-admin',
  templateUrl: './category.admin.component.html',
  styleUrls: [
    './category.admin.component.scss',        
  ]
})
export class CategoryAdminComponent implements OnInit {

  isNotificationVisible = false;
  notificationMessage = '';

  categories: Category[] = [];
  page:number=1;
  limit:number=10;

  addCategoryForm: FormGroup;
  updateCategoryForm: FormGroup;
  private modalRef: NgbModalRef | undefined;

  categoryToDelete: number | null = null;

  constructor(
    private categoryService: CategoryService,
    private router: Router,
    private formBuilder:FormBuilder,
    private modalService: NgbModal,

  ) {
    this.addCategoryForm = this.formBuilder.group({
      name: ['', Validators.required]
    });

    this.updateCategoryForm = this.formBuilder.group({
      name: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadCategories(); // Tải danh sách sản phẩm khi component khởi tạo
  }

  showNotification(message: string) {
    this.notificationMessage = message;
    this.isNotificationVisible = true;
    setTimeout(() => {
      this.isNotificationVisible = false;
    }, 3000); // Thời gian hiển thị thông báo (3 giây)
  }


  loadCategories(): void {
    this.categoryService.getCategories(this.page, this.limit).subscribe({
      next: (response: any) => {
        this.categories = response;
      },
      error: (error: any) => {
        console.error('Error fetching categories:', error);
      }
    });
  }

  openAddCategoryModal(content: TemplateRef<any>): void {
    this.modalService.open(content).result.then(
      () => this.onAddCategory(),
      () => {} // Xử lý khi modal bị đóng mà không có hành động gì
    );
  }

  openUpdateCategoryModal(content: TemplateRef<any>): void {
    this.modalService.open(content).result.then(
      () => this.onUpdateCategory(),
      () => {} // Xử lý khi modal bị đóng mà không có hành động gì
    );
  }

  openDeleteConfirmModal(content : TemplateRef<any>, categoryId: number): void{
    this.categoryToDelete=categoryId;
    this.modalService.open(content).result.catch(()=>{
      this.categoryToDelete=null;
    }
  );
    
  }

  confirmDelete():void{
    if(this.categoryToDelete !== null){
      this.categoryService.deleteCategory(this.categoryToDelete).subscribe({
        next:(response) =>{
          console.log(response.message);
          this.loadCategories();
          this.showNotification(response.message);
          this.categoryToDelete=null;
          this.modalService.dismissAll();

        },
        error:(error)=>{
          console.error('Error deleting category:', error);
          this.showNotification('Error deleting category!');
        }
      });
    }
  }

  onAddCategory(): void {
    if (this.addCategoryForm.valid) {
      const categoryData = this.addCategoryForm.value;

      this.categoryService.createCategory(categoryData).subscribe({
        next: () => {
          this.loadCategories();
          this.addCategoryForm.reset();
          this.modalService.dismissAll();
        },
        error: (error: any) => {
          console.error('Error adding category:', error);
        }
      });
    }
  }

  onUpdateCategory():void{
    if (this.updateCategoryForm.valid) {
      const categoryData = this.updateCategoryForm.value;

      this.categoryService.updateCategory(categoryData).subscribe({
        next: () => {
          this.loadCategories();
          this.updateCategoryForm.reset();
          this.modalService.dismissAll();
        },
        error: (error: any) => {
          console.error('Error adding category:', error);
        }
      });
    }
  }
  
  

  updateCategory(categoryId: number): void {
    this.router.navigate([`/admin/category/${categoryId}`]); // Chuyển đến trang chi tiết sản phẩm
  }


  deleteCategory(categoryId: number): void {
    this.categoryService.deleteCategory(categoryId).subscribe({
      next: (response) => {
        console.log(response.message); // Hiển thị thông báo thành công từ backend
        this.loadCategories(); // Tải lại danh sách categories
        this.showNotification(response.message); // Hiển thị thông báo cho người dùng
      },
      error: (error) => {
        console.error('Error deleting category:', error);
        this.showNotification('Error deleting category!'); // Hiển thị thông báo lỗi nếu có
      }
    });
  }
  
}

