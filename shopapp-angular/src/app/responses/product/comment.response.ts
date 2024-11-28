import { UserResponse } from "../user/user.response";

export interface CommentResponse {
    content: string;
    user: UserResponse;
    updated_at: string;  // Hoặc có thể sử dụng `Date` tùy thuộc vào cách bạn xử lý thời gian
  }