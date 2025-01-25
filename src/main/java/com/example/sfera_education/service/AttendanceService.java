package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Attendance;
import com.example.sfera_education.entity.DayOfWeek;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.exception.NotFoundException;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.AttendDto;
import com.example.sfera_education.payload.AttendanceDto;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResAttend;
import com.example.sfera_education.repository.AttendanceRepository;
import com.example.sfera_education.repository.GroupRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;


    public ApiResponse create(List<AttendanceDto> attendanceDtos, User teacher) {
        for (AttendanceDto attendanceDto : attendanceDtos) {
            User student = userRepository.findById(attendanceDto.getStudentId())
                    .orElseThrow(() -> new NotFoundException(new ApiResponse(
                            ResponseError.NOTFOUND("user not found"))));
            if (attendanceRepository.findByStudentAndDate(student, attendanceDto.getDate()) == null) {
                Attendance attendance = Attendance.builder()
                        .student(student)
                        .date(attendanceDto.getDate())
                        .groupId(student.getGroupId())
                        .isAttendance(attendanceDto.isAttendance())
                        .teacher(teacher)
                        .createdAt(LocalDateTime.now())
                        .build();
                attendanceRepository.save(attendance);
            }
        }


        return new ApiResponse("Attendance successfully saved");
    }

    public ApiResponse getAttendanceByGroupId(Integer groupId, int year, int month) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(
                        ResponseError.NOTFOUND("group not found"))));

        LocalDate startOfMonth = LocalDate.of(year, Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        LocalDate groupMonthYear = group.getStartDate().withDayOfMonth(1);

        if (startOfMonth.isBefore(groupMonthYear)) {
            return new ApiResponse(ResponseError.NOTFOUND("Attendance"));
        }

        List<LocalDate> groupDays = getGroupDays(groupId, year, month);

        List<ResAttend> resAttends = new ArrayList<>();

        List<User> users = userRepository.findAllByGroupId(group.getId());

        for (User user : users) {
            List<AttendDto> attendDtoList = new ArrayList<>();

            for (LocalDate groupDay : groupDays) {
                Attendance attendance = attendanceRepository.findByStudentAndDate(user, groupDay);

                AttendDto attendDto;
                if (attendance != null) {
                    attendDto = AttendDto.builder()
                            .id(attendance.getId())
                            .attendance(attendance.isAttendance())
                            .date(attendance.getDate())
                            .build();
                } else {
                    attendDto = AttendDto.builder()
                            .id(null)
                            .attendance(null)
                            .date(groupDay)
                            .build();
                }

                attendDtoList.add(attendDto);
            }

            ResAttend resAttend = ResAttend.builder()
                    .studentId(user.getId())
                    .studentName(user.getFirstname())
                    .studentLastName(user.getLastname())
                    .attendDtoList(attendDtoList)
                    .build();

            resAttends.add(resAttend);
        }

        return new ApiResponse(resAttends);
    }

    public ApiResponse getAttendanceByStudent(User user, int month) {
        LocalDate startOfMonth = LocalDate.of(LocalDate.now().getYear(), Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
        User user1 = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException(new ApiResponse(
                        ResponseError.NOTFOUND("user not found"))));
        List<Attendance> attendance = attendanceRepository
                .getAttendanceByStudentIdAndDateBetween(user1.getId(), startOfMonth, endOfMonth);
        List<AttendanceDto> attendanceDtos = attendanceDtoList(attendance);
        return new ApiResponse(attendanceDtos);
    }


    public ApiResponse updateAttendance(AttendanceDto attendanceDto, Long attendanceId, User user) {
        Optional<Attendance> byId = attendanceRepository.findById(attendanceId);
        if (byId.isEmpty()) return new ApiResponse(ResponseError.NOTFOUND("attendance"));
        Attendance attendance = byId.get();
        attendance.setAttendance(attendanceDto.isAttendance());
        attendance.setUpdatedAt(LocalDateTime.now());
        attendance.setUpdatedBy(user);
        return new ApiResponse("Attendance updated");
    }


    private List<AttendanceDto> attendanceDtoList(List<Attendance> attendances) {
        return attendances.stream().map(attendance1 ->
                AttendanceDto.builder()
                        .id(attendance1.getId())
                        .studentName(attendance1.getStudent().getFirstname())
                        .studentLastName(attendance1.getStudent().getLastname())
                        .attendance(attendance1.isAttendance())
                        .date(attendance1.getDate())
                        .build()).toList();
    }

    public List<LocalDate> getGroupDays(int groupId, int year, int month) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException(new ApiResponse(
                        ResponseError.NOTFOUND("group not found"))));

        List<DayOfWeek> daysOfWeek = group.getDays();

        LocalDate startOfMonth = LocalDate.of(year, Month.of(month), 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<LocalDate> classDates = new ArrayList<>();

        for (LocalDate date = startOfMonth; !date.isAfter(endOfMonth); date = date.plusDays(1)) {
            for (DayOfWeek dayOfWeek : daysOfWeek) {
                if (date.getDayOfWeek().name().equalsIgnoreCase(dayOfWeek.getDayOfWeek().name())) {
                    classDates.add(date);
                    break;
                }
            }
        }

        return classDates;
    }
}
