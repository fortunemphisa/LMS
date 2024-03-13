package com.geeks.AttendanceSpringBootBackend.service;


import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceRequestDto;
import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceResponseDto;


import java.time.LocalDate;
import java.util.List;


public interface AttendanceInterface {

     List<AttendanceResponseDto> attendanceList();
     AttendanceResponseDto newAttendance (AttendanceRequestDto attendance);
     AttendanceResponseDto getAttendanceRecordById(long id);
     AttendanceResponseDto updateAttendanceRecord(long id, String status);
     List<AttendanceResponseDto> getAllAttendanceRecords();
     void deleteAttendanceRecord(long id);
     AttendanceResponseDto[] deadlineChecker(LocalDate date);

}
