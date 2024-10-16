package com.project.shopapp.controllers;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.LoginResponse;
import com.project.shopapp.responses.RegisterResponse;
import com.project.shopapp.services.IUserService;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("/register")
    public ResponseEntity <RegisterResponse> createUser(
            @Valid @RequestBody UserDTO userDTO,
            BindingResult result
    ){
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        RegisterResponse
                        .builder()
                        .message(localizationUtils.getLocalizedMessage(String.valueOf(errorMessages)))
                        .build());
            }
            if(!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(RegisterResponse
                        .builder()
                        .message(localizationUtils.getLocalizedMessage(MessageKeys.PASSWORD_NOT_MATCH))
                        .build());
            }
            User user=userService.createUser(userDTO);
            // Trả về JSON thay vì chuỗi văn bản
            return ResponseEntity.ok(
                    RegisterResponse
                    .builder()
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody UserLoginDTO userLoginDTO
    ) {
        try {
            // Sinh token
            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());

            // Trả về token trong response
            return ResponseEntity.ok().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESSFULLY))
                            .token(token) // Thêm token vào phản hồi
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED,e.getMessage()))
                            .build()
            );
        }
    }
}
