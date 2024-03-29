package com.geeks.AttendanceSpringBootBackend.controller;


import com.geeks.AttendanceSpringBootBackend.entity.User;
import com.geeks.AttendanceSpringBootBackend.exceptions.ErrorResponse;
import com.geeks.AttendanceSpringBootBackend.exceptions.UserException;
import com.geeks.AttendanceSpringBootBackend.service.UserInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserInterface userInterface;

    @PostMapping
    public User addNewUser(@RequestBody User user){
         System.out.println(user);
        return userInterface.addNewUser(user);
    }

    @GetMapping
    public List<User> allUsers(){
        return userInterface.viewUsers();
    }
}
