package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.*;
import com.example.sfera_education.payload.res.StudentDashboardDto;
import com.example.sfera_education.repository.*;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class StudentService {

    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;
    private final HomeWorkRepository homeWorkRepository;
    private final LessonTrackingRepository lessonTrackingRepository;
    private final GroupRepository groupRepository;
    private final CategoryService categoryService;


    public ApiResponse getCountAllAndAvailableLessonsAndScoreAndRate(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        Group group = groupRepository.findByUserId(id);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        Integer countAllLessons = lessonRepository.countAllByCategoryId(group.getCategory().getId());

        Integer availableLessons = lessonTrackingRepository.countAllByGroupIdAndActiveTrue(group.getId());

        Integer allScoreByStudent = homeWorkRepository.findTotalScoreByStudent(user.getId());

        Integer countRatingStudents = group.getStudents().size();

        Integer ratingStudent = userRepository.getRatingStudent(group.getId(), user.getId());

        StudentStatisticDTO studentStatisticDTO = StudentStatisticDTO.builder()
                .availableLessons(availableLessons)
                .countAllLessons(countAllLessons)
                .score(allScoreByStudent != null ? allScoreByStudent : 0)
                .ratingStudent(ratingStudent != null ? ratingStudent : 0)
                .countRatingStudents(countRatingStudents)
                .build();
        return new ApiResponse(studentStatisticDTO);
    }


    public ApiResponse getRatingStudents(User user) {
        Group group = groupRepository.findByUserId(user.getId());
        if (group != null) {
            List<StudentRatingDTO> ratingStudents = homeWorkRepository.getRatingStudents(group.getId());
            return new ApiResponse(ratingStudents);
        }
        return new ApiResponse(ResponseError.NOTFOUND("group"));
    }


    public ApiResponse getCategoryStudent(User user) {
        User student = userRepository.findById(user.getId()).orElse(null);
        if (student == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        if (!student.getRole().equals(ERole.ROLE_STUDENT)) {
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }

        Group group = groupRepository.findById(user.getGroupId()).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        Category category = group.getCategory();
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        CategoryDTO categoryDTO = categoryService.parseCategoryDTO(category, null, null);
        return new ApiResponse(categoryDTO);
    }


    public ApiResponse getCountUserDashboard(User user) {
        Integer categoryCount = userRepository.countDistinctCategoriesByUserId(user.getId());
        Integer lessonCount = userRepository.countByLesson(user.getId());
        Integer countLesson = lessonRepository.countLesson();
        StudentDashboardDto studentDashboardDto = StudentDashboardDto.builder()
                .categoryCount(categoryCount)
                .wonLessonCount(lessonCount)
                .lessonCount(countLesson)
                .build();
        return new ApiResponse(studentDashboardDto);
    }
}
