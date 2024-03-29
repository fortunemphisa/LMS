package com.geeks.AttendanceSpringBootBackend.service.impl;

import com.geeks.AttendanceSpringBootBackend.entity.AttendanceRecord;
import com.geeks.AttendanceSpringBootBackend.entity.User;
import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceRequestDto;
import com.geeks.AttendanceSpringBootBackend.entity.dto.AttendanceResponseDto;
import com.geeks.AttendanceSpringBootBackend.enums.Status;
import com.geeks.AttendanceSpringBootBackend.exceptions.AttendanceExceptions;
import com.geeks.AttendanceSpringBootBackend.repository.AttendanceRepository;
import com.geeks.AttendanceSpringBootBackend.repository.UserRepository;
import com.geeks.AttendanceSpringBootBackend.service.AttendanceInterface;
import com.geeks.AttendanceSpringBootBackend.service.IpAdressInterface;
import com.sun.tools.javac.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImplementation implements AttendanceInterface {
    private static final Logger logger = LogManager.getLogger(Main.class);

    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AttendanceMapperServiceImpl attendanceDtoMapper;
    @Autowired
    private LoginTimeChecker loginTimeChecker;
    @Autowired
    private IpAdressInterface ipAdressInterface;


    @Override
    public List<AttendanceResponseDto> attendanceList() {

        List<AttendanceResponseDto> attendanceRecords = attendanceRepository.findAll()
                .stream()
                .map(attendanceDtoMapper::mapToDto)
                .collect(Collectors.toList());
        return attendanceRecords;
    }

    @Override
    public AttendanceResponseDto newAttendance(AttendanceRequestDto requestDto) {

        LocalTime expectedLogOutTime;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime currentTime = LocalTime.now();
        String formattedTime = currentTime.format(formatter);
        // Mapping my attendanceRequest to attendance entity
        AttendanceRecord attendanceRecord = attendanceDtoMapper.mapTOEntity(requestDto);
        AttendanceRecord newAttendanceRecord;
        String logInIp = ipAdressInterface.getLocation();
        logger.info(logInIp);
        // check if user exists

        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(() -> new AttendanceExceptions("User with id " + requestDto.getUserId() + " not found"));
        attendanceRecord.setUserId(user);

        if (logInIp.equals("Office")) {

            attendanceRecord.setLogInTime(LocalTime.parse(formattedTime, formatter));
            attendanceRecord.setDate(LocalDate.now());
            attendanceRecord.setLogInLocation(logInIp);
            expectedLogOutTime = attendanceRecord.getLogInTime().plusHours(9);
            attendanceRecord.setLogOutTime(expectedLogOutTime);

            if (loginTimeChecker.isPresent(attendanceRecord.getLogInTime())) {
                attendanceRecord.setStatus(Status.PRESENT);
            } else {
                attendanceRecord.setStatus(Status.LATE);
            }

            newAttendanceRecord = attendanceRepository.save(attendanceRecord);

        } else {
            throw new AttendanceExceptions("User not attended from the office");
        }

        return attendanceDtoMapper.mapToDto(newAttendanceRecord);
    }

    // search record
    @Override
    public AttendanceResponseDto getAttendanceRecordById(long id) {
        Optional<AttendanceRecord> attendanceRecordOptional = attendanceRepository.findById(id);
        if (attendanceRecordOptional.isPresent()) {

            return attendanceDtoMapper.mapToDto(attendanceRecordOptional.get());
        } else {
            // handle not found scenario
            throw new AttendanceExceptions("Attendance not found for ID: " + id);
        }
    }

    //Update method
    @Override
    public AttendanceResponseDto updateAttendanceRecord(long id, String status) {
        Optional<AttendanceRecord> attendanceRecordOptional = attendanceRepository.findById(id);
        if (attendanceRecordOptional.isPresent()) {
            AttendanceRecord attendanceRecord = attendanceRecordOptional.get();
            attendanceRecord.setStatus(Status.valueOf(status));
            attendanceRepository.save(attendanceRecord);
            AttendanceRecord updatedAttendanceRecord = attendanceRepository.save(attendanceRecord);
            return attendanceDtoMapper.mapToDto(updatedAttendanceRecord);
        } else {
            // handle not found scenario
            throw new AttendanceExceptions("Attendance not found");
        }
    }

    @Override
    public List<AttendanceResponseDto> getAllAttendanceRecords() {
        return null;
    }

    // Delete operation
    public void deleteAttendanceRecord(long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public AttendanceResponseDto[] deadlineChecker(LocalDate systemDate){
        List<AttendanceResponseDto> allAttendancesForToday = attendanceRepository.findAttendanceByDate(systemDate);
        AttendanceResponseDto[] arr = new AttendanceResponseDto[allAttendancesForToday.size()];
        return allAttendancesForToday.toArray(arr);
    }
}

