package com.ot.popIce.services;

import com.ot.popIce.dao.UserDao;
import com.ot.popIce.dto.ResponseStructure;
import com.ot.popIce.dto.User;
import com.ot.popIce.util.EmailSender;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private EmailSender mailSender;

    @Transactional
    public ResponseEntity<ResponseStructure<User>> save(User user) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        if (userDao.findByEmail(user.getEmail()) != null || userDao.findByPhone(user.getPhone()) != null) {
            responseStructure.setStatus(HttpStatus.CONFLICT.value());
            responseStructure.setMessage("User Already Exists So Cannot Be Saved ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.CONFLICT);
        } else {
            responseStructure.setStatus(HttpStatus.CREATED.value());
            responseStructure.setMessage("User Registered Successfully " + user.getName());
            user.setRole("Admin");
            user = userDao.save(user);
            responseStructure.setData(user);
            return new ResponseEntity<>(responseStructure, HttpStatus.CREATED);
        }
    }

    @Transactional
    public ResponseEntity<ResponseStructure<User>> delete(long id) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user = userDao.findById(id);
        if (Objects.isNull(user)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("User Does Not Exist To Delete " + id);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            userDao.delete(user);
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("User Deleted Successfully " + id);
            responseStructure.setData(user);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<User>> findById(long id) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user = userDao.findById(id);
        if (Objects.isNull(user)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("User Does Not Exist By ID " + id);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("User Found By ID Successfully " + id);
            responseStructure.setData(user);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<User>> findByPhone(String phone) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user = userDao.findByPhone(phone);
        if (user == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("User not found with phone number: " + phone);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("User found with phone number: " + phone);
            responseStructure.setData(user);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<List<User>>> findByNameContaining(String letter) {
        ResponseStructure<List<User>> responseStructure = new ResponseStructure<>();
        List<User> users = userDao.findByNameContaining(letter);
        if (users.isEmpty()) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No users found with name containing letter " + letter);
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Users found with name containing letter " + letter);
            responseStructure.setData(users);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<Page<User>>> findAll(int offset, int pageSize, String field) {
        ResponseStructure<Page<User>> responseStructure = new ResponseStructure<>();
        Page<User> users = userDao.findAll(offset, pageSize, field);
        if (Objects.isNull(users)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("No Users Exist To Find ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("All Users Found ");
            responseStructure.setData(users);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<ResponseStructure<User>> update(User user) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user1 = userDao.findByEmail(user.getEmail());
        if (user1 == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("User Not Found To Update ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("User Updated ");
            responseStructure.setData(userDao.save(user));
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<User>> login(String email, String password) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user = userDao.findByEmail(email);
        if (user == null) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("User not found with Email: " + email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseStructure);
        }
        if (password == null) {
            responseStructure.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseStructure.setMessage("Password not set for user: " + email);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseStructure);
        }
        if (!user.getPassword().equals(password)) {
            responseStructure.setStatus(HttpStatus.UNAUTHORIZED.value());
            responseStructure.setMessage("Incorrect password for user: " + email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseStructure);
        }
        responseStructure.setStatus(HttpStatus.OK.value());
        responseStructure.setMessage("Login successful for user: " + email);
        responseStructure.setData(user);
        return ResponseEntity.ok(responseStructure);
    }

    public ResponseEntity<ResponseStructure<String>> forgotPassword(String email) {
        ResponseStructure<String> responseStructure = new ResponseStructure<>();
        User user = userDao.findByEmail(email);
        if (Objects.isNull(user)) {
            responseStructure.setStatus((HttpStatus.NOT_FOUND.value()));
            responseStructure.setMessage("Email Does Not Exist " + email);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            int otp = (int) (Math.random() * (9999 - 1000) + 1000);
            user.setOtp(otp);
            userDao.save(user);
            mailSender.sendEmail(user.getEmail(), "This is Your OTP \n" +
                            " Don't Share OTP with Anyone\n " +
                            "Enter this OTP To Update Password \n" + " -> OTP " + otp,
                    "Your OTP To Update Password");
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("OTP Sent To Email Id: " + email);
            responseStructure.setData("OTP Sent To The Email Of User");
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }

    public ResponseEntity<ResponseStructure<User>> validateOtp(int otp) throws Exception {
        ResponseStructure<User> responseStructure = new ResponseStructure<User>();
        User user = userDao.findByOtp(otp);
        if (user != null) {
            user.setPassword(user.getPassword());
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setData(user);
            responseStructure.setMessage("Success ");
            return new ResponseEntity<ResponseStructure<User>>(responseStructure, HttpStatus.OK);
        } else {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setData(null);
            responseStructure.setMessage("OTP Invalid ");
            return new ResponseEntity<ResponseStructure<User>>(responseStructure, HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<ResponseStructure<User>> updateNewPassword(String password, int otp) {
        ResponseStructure<User> responseStructure = new ResponseStructure<>();
        User user = userDao.findByOtp(otp);
        if (Objects.isNull(user)) {
            responseStructure.setStatus(HttpStatus.NOT_FOUND.value());
            responseStructure.setMessage("Password Not Found To Update ");
            responseStructure.setData(null);
            return new ResponseEntity<>(responseStructure, HttpStatus.NOT_FOUND);
        } else {
            responseStructure.setStatus(HttpStatus.OK.value());
            responseStructure.setMessage("Password Updated " + user.getEmail());
            user.setPassword(password);
            int otp1 = (int) (Math.random() * (9999 - 1000) + 100000);
            user.setOtp(otp1);
            user = userDao.save(user);
            responseStructure.setData(user);
            return new ResponseEntity<>(responseStructure, HttpStatus.OK);
        }
    }
}