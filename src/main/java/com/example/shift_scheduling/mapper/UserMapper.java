package com.example.shift_scheduling.mapper;

import com.example.shift_scheduling.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM app_user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM app_user WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM app_user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM app_user ORDER BY id")
    List<User> findAll();

    @Insert("INSERT INTO app_user(username, password, email, role) VALUES(#{username}, #{password}, #{email}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE app_user SET password = #{password}, email = #{email}, role = #{role} WHERE id = #{id}")
    int update(User user);

    @Delete("DELETE FROM app_user WHERE id = #{id}")
    int deleteById(Long id);
}
