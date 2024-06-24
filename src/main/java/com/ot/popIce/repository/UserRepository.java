package com.ot.popIce.repository;

import com.ot.popIce.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByPhone(String phone);

    public List<User> findByNameContaining(String letter);

    public User findByEmail(String email);

    public User findByOtp(int otp);
}