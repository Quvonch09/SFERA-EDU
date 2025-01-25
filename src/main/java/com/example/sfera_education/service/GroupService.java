package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.DayOfWeek;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.GroupDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResGroup;
import com.example.sfera_education.payload.res.ResGroupStudents;
import com.example.sfera_education.repository.CategoryRepository;
import com.example.sfera_education.repository.DayOfWeekRepository;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final DayOfWeekRepository dayOfWeekRepository;
    private final CategoryRepository categoryRepository;
    private final NotificationService notificationService;


    public ApiResponse saveGroup(ResGroup resGroup) {
        boolean b = groupRepository.existsByName(resGroup.getName());
        if (b) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Group name"));
        }

        User teacher = userRepository.findByIdAndRoleAndEnabledTrue(resGroup.getTeacherId(), ERole.ROLE_TEACHER);
        if (teacher == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        for (Integer daysWeekId : resGroup.getDaysWeekIds()) {
            DayOfWeek dayOfWeek = dayOfWeekRepository.findById(daysWeekId).orElse(null);
            if (dayOfWeek == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Days Week"));
            }
            dayOfWeeks.add(dayOfWeek);
        }

        Category category = categoryRepository.findById(resGroup.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        Group group = Group.builder()
                .name(resGroup.getName())
                .category(category)
                .days(dayOfWeeks)
                .teacher(teacher)
                .active(true)
                .startDate(resGroup.getStartDate())
                .startTime(resGroup.getStartTime())
                .endTime(resGroup.getEndTime())
                .students(null)
                .build();
        groupRepository.save(group);

        notificationService.saveNotification(
                teacher,
                "Guruh qo'shildi!",
                "Siz " + group.getName() + " guruhiga o'qituvchi etib tayinlandingiz!",
                0L,
                false
        );
        return new ApiResponse("Group Saved");
    }


    public ApiResponse getTeacherGroups(User user) {
        if (user.getRole() != ERole.ROLE_TEACHER) {
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }
        List<Group> allByTeacherIdAndActiveTrue = groupRepository.findAllByTeacherIdAndActiveTrue(user.getId());
        if (allByTeacherIdAndActiveTrue.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Group list"));
        }

        List<GroupDTO> dtos = new ArrayList<>();
        for (Group group : allByTeacherIdAndActiveTrue) {
            dtos.add(
                    GroupDTO.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .build()
            );
        }
        return new ApiResponse(dtos);
    }


    public ApiResponse getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        if (groups.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }
        List<GroupDTO> groupDTOList = new ArrayList<>();
        for (Group group : groups) {
            groupDTOList.add(parseGroupDTO(group));
        }
        return new ApiResponse(groupDTOList);
    }


    public ApiResponse getGroupById(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        List<ResGroupStudents> resGroupStudents = new ArrayList<>();

        for (User student : group.getStudents()) {
            ResGroupStudents resGroupStudents1 = ResGroupStudents.builder()
                    .studentId(student.getId())
                    .fullName(student.getFirstname() + " " + student.getLastname())
                    .active(true)
                    .build();
            resGroupStudents.add(resGroupStudents1);
        }

        for (User deleteStudent : group.getDeleteStudents()) {
            ResGroupStudents resDelStudent = ResGroupStudents.builder()
                    .studentId(deleteStudent.getId())
                    .fullName(deleteStudent.getFirstname() + " " + deleteStudent.getLastname())
                    .active(false)
                    .build();
            resGroupStudents.add(resDelStudent);
        }

        List<String> days = new ArrayList<>();
        for (DayOfWeek day : group.getDays()) {
            days.add(day.getDayOfWeek().toString());
        }
        GroupDTO groupDto = parseGroupDTO(group);
        groupDto.setStudents(resGroupStudents);
        groupDto.setDaysName(days);

        return new ApiResponse(groupDto);
    }


    public ApiResponse updateGroup(Integer groupId, ResGroup resGroup) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        User teacher = userRepository.findByIdAndRoleAndEnabledTrue(resGroup.getTeacherId(), ERole.ROLE_TEACHER);
        if (teacher == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();
        for (Integer daysWeekId : resGroup.getDaysWeekIds()) {
            DayOfWeek dayOfWeek = dayOfWeekRepository.findById(daysWeekId).orElse(null);
            if (dayOfWeek == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Days Week"));
            }
            dayOfWeeks.add(dayOfWeek);
        }

        Category category = categoryRepository.findById(resGroup.getCategoryId()).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }
        group.setId(groupId);
        group.setName(category.getName());
        group.setTeacher(teacher);
        group.setCategory(category);
        group.setDays(dayOfWeeks);
        group.setStartDate(resGroup.getStartDate());
        group.setStartTime(resGroup.getStartTime());
        group.setEndTime(resGroup.getEndTime());
        groupRepository.save(group);
        return new ApiResponse("Group Updated");
    }


    @Transactional
    public ApiResponse deleteGroup(Integer groupId) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        groupRepository.deleteByGroupId(group.getId());
        for (User student : group.getStudents()) {
            student.setGroupId(null);
            student.setRole(ERole.ROLE_USER);
        }
        group.setStudents(null);
        group.setActive(false);
        groupRepository.save(group);
        return new ApiResponse("Group Deleted");
    }


    private GroupDTO parseGroupDTO(Group group) {
        return GroupDTO.builder()
                .id(group.getId())
                .categoryId(group.getCategory().getId())
                .name(group.getName())
                .teacherId(group.getTeacher().getId())
                .teacherName(group.getTeacher().getFirstname() + " " + group.getTeacher().getLastname())
                .startDate(group.getStartDate())
                .active(group.isActive())
                .startTime(group.getStartTime())
                .endTime(group.getEndTime())
                .build();
    }

    private ResGroupStudents resGroupStudents(User user) {
        return ResGroupStudents.builder()
                .studentId(user.getId())
                .fullName(user.getFirstname() + " " + user.getLastname())
                .build();
    }
}
