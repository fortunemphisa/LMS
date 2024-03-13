package com.geeks.AttendanceSpringBootBackend.controller;

import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceRequestDto;
import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceResponseDto;
import com.geeks.AttendanceSpringBootBackend.service.AttendanceInterface;
import com.geeks.AttendanceSpringBootBackend.service.IpAdressInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceInterface attendanceInterface;
    @Autowired
    private IpAdressInterface ipAdressInterface;

    @PostMapping("/create")
    public ResponseEntity<AttendanceResponseDto> addNewAttendance(@RequestBody AttendanceRequestDto attendanceRecord) {
            System.out.println(attendanceRecord.toString());
            AttendanceResponseDto attendanceResponseDto = attendanceInterface.newAttendance(attendanceRecord);
            return ResponseEntity.status(HttpStatus.CREATED).body(attendanceResponseDto);
    }

    @GetMapping("/view-all")
    public List<AttendanceResponseDto> attendanceRecord(){
        return attendanceInterface.attendanceList();
    }
    @DeleteMapping("/delete/{id}")
    public void deleteAttendanceRecord(@PathVariable long id) {
        attendanceInterface.deleteAttendanceRecord(id);
    }
    @GetMapping("/view/{id}")
    public AttendanceResponseDto getAttendanceRecordById (@PathVariable Long id){
        return attendanceInterface.getAttendanceRecordById(id);
    }
    @PutMapping("/update/{id}/{status}")
    public void UpdateAttendance(@PathVariable long id,@PathVariable String status){
        attendanceInterface.updateAttendanceRecord(id, status );
    }
    @GetMapping("/today/{date}")
    public AttendanceResponseDto[] attendancesForToday (@PathVariable LocalDate date){
        return attendanceInterface.deadlineChecker(date);
    }
    @GetMapping("/test-ip")
    public String getIp(){
        return ipAdressInterface.getSystemIp();
    }
}
