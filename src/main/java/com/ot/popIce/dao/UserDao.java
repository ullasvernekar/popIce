package com.ot.popIce.dao;

import com.ot.popIce.dto.User;
import com.ot.popIce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDao {

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        return userRepository.save(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findById(long id) {
        Optional<User> optional = userRepository.findById(id);
        return optional.orElse(null);
    }

    public Page<User> findAll(int offset, int pageSize, String field) {
        return userRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field).descending()));
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public List<User> findByNameContaining(String letter) {
        return userRepository.findByNameContaining(letter);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByOtp(int otp) {
        return userRepository.findByOtp(otp);
    }
}