package com.project.shopapp.services;

import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.Role;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRoleService {
    List<Role> getAllRole();

}
