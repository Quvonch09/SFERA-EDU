package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.GroupStatistics;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.payload.res.ResRateStudent;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.HomeWorkRepository;
import com.example.sfera_education.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RateService {

    private final HomeWorkRepository homeWorkRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ApiResponse getGroupByYearlyStatistic() {
        List<GroupStatistics> groupStatistics = homeWorkRepository.findGroupStatistics();
        if (groupStatistics.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Groups"));
        }
        return new ApiResponse(groupStatistics);
    }


    public ApiResponse getStudents(String keyword, Integer groupId, Integer categoryId, int page, int size) {
        PageRequest request = PageRequest.of(page, size);
        Page<User> students = userRepository.findAlLByStudentSearchAndGroupIdAndCategoryId(keyword, groupId, categoryId, request);
        ResPageable pageable = pageable(students, page, size);
        return new ApiResponse(pageable);
    }

    public ApiResponse getStudentsForTeacher(User teacher, Integer groupId, int page, int size) {
        PageRequest request = PageRequest.of(page, size);
        Page<User> students = userRepository.findAllByGroupId(groupId, teacher.getId(), request);
        ResPageable pageable = pageable(students, page, size);
        return new ApiResponse(pageable);

    }


    private List<ResRateStudent> getStudentRateList(Page<User> students) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1); // Bu oyning birinchi kuni
        LocalDate endDate = LocalDate.now();
        Map<ResRateStudent, Integer> rateStudentMap = new HashMap<>();
        for (User user : students.getContent()) {
            Integer score = homeWorkRepository.findTotalScoreByStudentsAndPeriod(user, startDate, endDate);
            Integer ratingStudent = userRepository.getRatingStudent(user.getGroupId(), user.getId());

            // Null qiymatlar uchun standart qiymatlar qo'llash
            if (score == null) {
                score = 0; // Yoki mos qiymat
            }

            if (ratingStudent == null) {
                ratingStudent = 0; // Yoki mos qiymat
            }

            Group group = groupRepository.findById(user.getGroupId()).orElseThrow();
            ResRateStudent resRateStudent = ResRateStudent.builder()
                    .fullName(user.getFirstname() + " " + user.getLastname())
                    .categoryName(group.getCategory().getName())
                    .groupName(group.getName())
                    .rate(ratingStudent)
                    .score(score).build();

            rateStudentMap.put(resRateStudent, score);
        }
        return rateStudentMap.entrySet().stream()
                .sorted(Map.Entry.<ResRateStudent, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private ResPageable pageable(Page<User> students, int page, int size) {
        List<ResRateStudent> studentRateList = getStudentRateList(students);
        ResPageable resPageable = new ResPageable();
        resPageable.setPage(page);
        resPageable.setSize(size);
        resPageable.setTotalElements(students.getTotalElements());
        resPageable.setTotalPage(students.getTotalPages());
        resPageable.setBody(studentRateList);
        return resPageable;
    }
}
