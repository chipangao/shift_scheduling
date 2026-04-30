package com.example.shift_scheduling.service;

import com.example.shift_scheduling.entity.Shift;
import com.example.shift_scheduling.mapper.ShiftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShiftService {

    @Autowired
    private ShiftMapper shiftMapper;

    @Cacheable(value = "shift", key = "#id")
    public Shift getShiftById(Long id) {
        System.out.println("查資料庫: shift id=" + id);
        return shiftMapper.findById(id);
    }

    public List<Shift> getShiftsByEmployee(Long employeeId) {
        return shiftMapper.findByEmployeeId(employeeId);
    }

    public List<Shift> getShiftsByDate(LocalDate date) {
        return shiftMapper.findByDate(date);
    }

    public List<Shift> getAllShifts() {
        return shiftMapper.findAll();
    }

    @CacheEvict(value = "shift", key = "#result.id")
    public Shift createShift(Shift shift) {
        if (shiftMapper.existsByEmployeeAndDate(shift.getEmployeeId(), shift.getShiftDate())) {
            throw new IllegalArgumentException("員工當天已有班表，不可重複排班");
        }
        shiftMapper.insert(shift);
        return shift;
    }

    @CacheEvict(value = "shift", key = "#id")
    public void deleteShift(Long id) {
        shiftMapper.deleteById(id);
    }
}
