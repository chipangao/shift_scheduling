package com.example.shift_scheduling.service;

import com.example.shift_scheduling.entity.User;
import com.example.shift_scheduling.mapper.UserMapper;
import com.example.shift_scheduling.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void register(User user) {
        // 檢查帳號是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("帳號已存在");
        }
        if (userMapper.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("Email 已被註冊");
        }
        // 密碼加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userMapper.insert(user);
    }

    public String login(String username, String rawPassword) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("帳號或密碼錯誤");
        }
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("帳號或密碼錯誤");
        }
        return jwtUtil.generateToken(username);
    }

    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
