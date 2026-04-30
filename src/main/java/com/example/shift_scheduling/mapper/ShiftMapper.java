package com.example.shift_scheduling.mapper;

import com.example.shift_scheduling.entity.Shift;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ShiftMapper {

    @Select("SELECT * FROM shift WHERE id = #{id}")
    Shift findById(Long id);

    @Select("SELECT * FROM shift WHERE employee_id = #{employeeId} ORDER BY shift_date")
    List<Shift> findByEmployeeId(Long employeeId);

    @Select("SELECT * FROM shift WHERE shift_date = #{date} ORDER BY employee_id")
    List<Shift> findByDate(LocalDate date);

    @Select("SELECT * FROM shift ORDER BY shift_date DESC, employee_id")
    List<Shift> findAll();

    @Insert("INSERT INTO shift(employee_id, shift_date, shift_type) VALUES(#{employeeId}, #{shiftDate}, #{shiftType})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Shift shift);

    @Delete("DELETE FROM shift WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT COUNT(*) > 0 FROM shift WHERE employee_id = #{employeeId} AND shift_date = #{shiftDate}")
    boolean existsByEmployeeAndDate(@Param("employeeId") Long employeeId, @Param("shiftDate") LocalDate shiftDate);
}
