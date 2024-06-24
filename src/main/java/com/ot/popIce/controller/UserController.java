package com.ot.popIce.controller;

import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.dto.User;
import com.ot.popIce.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping(value = "/save")
    public ResponseEntity<ResponseStructure<User>> save(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping(value = "/delete{id}")
    public ResponseEntity<ResponseStructure<User>> delete(@PathVariable long id) {
        return userService.delete(id);
    }

    @GetMapping(value = "/findById/{id}")
    public ResponseEntity<ResponseStructure<User>> findById(@PathVariable long id) {
        return userService.findById(id);
    }

    @GetMapping(value = "/findAll")
    public ResponseEntity<ResponseStructure<Page<User>>> getAll(@RequestParam(defaultValue = "0") int offset,
                                                                @RequestParam(defaultValue = "5") int pageSize,
                                                                @RequestParam(defaultValue = "id") String field) {
        return userService.findAll(offset, pageSize, field);
    }

    @GetMapping(value = "/findByNameContaining/{letter}")
    public ResponseEntity<ResponseStructure<List<User>>> findByNameContaining(@PathVariable String letter) {
        return userService.findByNameContaining(letter);
    }

    @GetMapping(value = "/findByPhone/{phone}")
    public ResponseEntity<ResponseStructure<User>> findByPhone(@PathVariable String phone) {
        return userService.findByPhone(phone);
    }

    @PutMapping(value = "/update")
    public ResponseEntity<ResponseStructure<User>> update(@RequestBody User user) {
        return userService.update(user);
    }

    @PostMapping(value = "/updateNewPassword/{otp}/{password}")
    public ResponseEntity<ResponseStructure<User>> updateNewPassword(@PathVariable String password, @PathVariable int otp) {
        return userService.updateNewPassword(password, otp);
    }

    @GetMapping(value = "/validateOTP/{otp}")
    public ResponseEntity<ResponseStructure<User>> validateOTP(@PathVariable int otp) throws Exception {
        return userService.validateOtp(otp);
    }

    @PostMapping(value = "/login/{email}/{password}")
    public ResponseEntity<ResponseStructure<User>> login(@PathVariable String email, @PathVariable String password) {
        return userService.login(email, password);
    }

    @PostMapping(value = "/forgotpassword")
    public ResponseEntity<ResponseStructure<String>> forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);
    }
}