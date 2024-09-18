package com.project.shopapp.services;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User creaetUser(UserDTO userDTO) throws DataNotFoundException;

    String login(String phoneNumber, String password) throws DataNotFoundException;
}
