package com.example.shift_scheduling.service;

import com.example.shift_scheduling.entity.Shift;
import com.example.shift_scheduling.mapper.ShiftMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private ShiftMapper shiftMapper;

    @InjectMocks
    private ShiftService shiftService;

    private Shift mockShift;

    @BeforeEach
    void setUp() {
        mockShift = new Shift();
        mockShift.setId(1L);
        mockShift.setEmployeeId(1L);
        mockShift.setShiftDate(LocalDate.of(2026, 5, 1));
        mockShift.setShiftType("morning");
    }

    @Test
    void testGetShiftById() {
        when(shiftMapper.findById(1L)).thenReturn(mockShift);

        Shift result = shiftService.getShiftById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getEmployeeId());
        verify(shiftMapper, times(1)).findById(1L);
    }

    @Test
    void testGetShiftsByEmployee() {
        List<Shift> shifts = Arrays.asList(mockShift);
        when(shiftMapper.findByEmployeeId(1L)).thenReturn(shifts);

        List<Shift> result = shiftService.getShiftsByEmployee(1L);

        assertEquals(1, result.size());
        verify(shiftMapper, times(1)).findByEmployeeId(1L);
    }

    @Test
    void testCreateShift_Success() {
        when(shiftMapper.existsByEmployeeAndDate(anyLong(), any(LocalDate.class))).thenReturn(false);
        when(shiftMapper.insert(any(Shift.class))).thenReturn(1);

        Shift result = shiftService.createShift(mockShift);

        assertNotNull(result);
        verify(shiftMapper, times(1)).insert(mockShift);
    }

    @Test
    void testCreateShift_Duplicate_ThrowsException() {
        when(shiftMapper.existsByEmployeeAndDate(1L, LocalDate.of(2026, 5, 1))).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            shiftService.createShift(mockShift);
        });

        verify(shiftMapper, never()).insert(any(Shift.class));
    }

    @Test
    void testDeleteShift() {
        when(shiftMapper.deleteById(1L)).thenReturn(1);

        shiftService.deleteShift(1L);

        verify(shiftMapper, times(1)).deleteById(1L);
    }
}
