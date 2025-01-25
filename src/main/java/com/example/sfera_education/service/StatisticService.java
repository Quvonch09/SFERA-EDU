package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.StatisticDTO;
import com.example.sfera_education.payload.res.*;
import com.example.sfera_education.payload.top.TopGroup;
import com.example.sfera_education.payload.top.TopStudent;
import com.example.sfera_education.payload.top.TopTeacher;
import com.example.sfera_education.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final GroupRepository groupRepository;
    private final LessonTrackingRepository lessonTrackingRepository;
    private final TaskRepository taskRepository;
    private final HomeWorkRepository homeWorkRepository;
    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final UserProgressRepository userProgressRepository;

    LocalDate startDate = LocalDate.now().withDayOfMonth(1);
    LocalDate endDate = LocalDate.now();

    public ApiResponse getAllCount() {
        int teacherCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_TEACHER);
        int studentCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_STUDENT);
        Integer categoryCount = categoryRepository.countByActiveTrue();
        Integer groupCount = groupRepository.countByActiveTrue();
        StatisticDTO statisticDto = StatisticDTO.builder()
                .teacherCount(teacherCount)
                .studentCount(studentCount)
                .categoryCount(categoryCount)
                .groupCount(groupCount)
                .build();
        return new ApiResponse(statisticDto);
    }


    public ApiResponse getTopStudent() {
        // Fetch active students with the role of STUDENT
        List<User> activeStudents = userRepository.findByRole(ERole.ROLE_STUDENT);
        if (activeStudents.isEmpty()) {
            return new ApiResponse(List.of());
        }

        Map<TopStudent, Integer> topStudentMap = new HashMap<>();
        for (User user : activeStudents) {
            if (user.isEnabled()) {
                Integer score = getTotalScoreByStudentsAndCurrentMonth(user);

                // Skip users with no score
                if (score == null) {
                    continue;
                }

                // Retrieve group data and skip users without a valid group
                Group group = groupRepository.findById(user.getGroupId()).orElse(null);
                if (group == null) {
                    continue;
                }

                // Build TopStudent object and add it to the map
                TopStudent topStudentDTO = TopStudent.builder()
                        .id(user.getId())
                        .fullName(user.getFirstName() + " " + user.getLastName())
                        .groupId(group.getId())
                        .groupName(group.getName())
                        .scoreMonth(score)
                        .build();
                topStudentMap.put(topStudentDTO, score);
            }
        }

        // Sort by score, limit to top 5 students, and convert to a list
        List<TopStudent> topStudents = topStudentMap.entrySet().stream()
                .sorted(Map.Entry.<TopStudent, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // Check if the result is empty before returning
        if (topStudents.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Top students"));
        }

        return new ApiResponse(topStudents);
    }


    public ApiResponse getTopGroup() {
        List<Group> groups = groupRepository.findAllByActiveTrue();
        if (groups.isEmpty()) {
            return new ApiResponse(List.of());
        }

        Map<TopGroup, Integer> topGroupMap = new HashMap<>();
        for (Group group : groups) {
            Integer score = getTotalScoreByGroupAndCurrentMonth(group.getId());
            score = (score != null) ? score : 0; // Handle null score by setting it to 0

            TopGroup topGroupDTO = TopGroup.builder()
                    .id(group.getId())
                    .groupName(group.getName())
                    .scoreMonth(score != null ? score : 0)
                    .studentCount(group.getStudents().size())
                    .build();
            topGroupMap.put(topGroupDTO, score);
        }

        // Sort and limit to top 5 groups
        List<TopGroup> topGroups = topGroupMap.entrySet().stream()
                .sorted(Map.Entry.<TopGroup, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        return new ApiResponse(topGroups);
    }


    public ApiResponse getTopTeacher() {
        // Fetch all active teachers
        List<User> teachers = userRepository.findByRole(ERole.ROLE_TEACHER);
        if (teachers.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("User list"));
        }

        List<TopTeacher> topTeacherList = new ArrayList<>();

        // Loop through each teacher
        for (User teacher : teachers) {
            if (teacher.isEnabled()) {
                // Fetch all active groups for the teacher
                List<Group> groups = groupRepository.findAllByTeacherIdAndActiveTrue(teacher.getId());

                // If teacher has no groups, skip to the next teacher
                if (groups.isEmpty()) {
                    continue;
                }

                // Calculate the total score for the teacher's groups
                double totalScore = 0.0;
                for (Group group : groups) {
                    Integer groupScore = getTotalScoreByGroupAndCurrentMonth(group.getId());

                    // Only add non-null scores to the total
                    if (groupScore != null) {
                        totalScore += groupScore;
                    }
                }

                // Build the TopTeacher object and add to the list
                TopTeacher topTeacherDTO = TopTeacher.builder()
                        .id(teacher.getId())
                        .fullName(teacher.getFirstName() + " " + teacher.getLastName())
                        .phoneNumber(teacher.getPhoneNumber())
                        .scoreMonth(totalScore)
                        .build();

                topTeacherList.add(topTeacherDTO);
            }
        }

        // Sort the teachers by score and limit to top 5
        List<TopTeacher> sortedTopTeachers = topTeacherList.stream()
                .sorted(Comparator.comparing(TopTeacher::getScoreMonth).reversed())
                .limit(5)
                .toList();

        // Return the sorted top 5 teachers
        if (sortedTopTeachers.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Top teachers"));
        }

        return new ApiResponse(sortedTopTeachers);
    }


    public ApiResponse getTeacherCountStatistic(User user) {

        User teacher = userRepository.findById(user.getId()).orElse(null);
        if (teacher == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Teacher"));
        }

        int groupCount = groupRepository.countAllByTeacherIdAndActiveTrue(teacher.getId());

        int studentCount = userRepository.countAllByStudentByTeacher(teacher.getId());

        int teacherCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_TEACHER);

        return new ApiResponse(new ResTeacherCount(groupCount, studentCount, teacherCount));
    }


    public ApiResponse topStudentByTeacher(User teacher) {

        // Fetch all active students by teacher ID
        List<User> activeStudents = userRepository.findAllByStudentByTeacher(teacher.getId());
        if (activeStudents.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Student"));
        }

        // Map to store top students and their scores
        Map<TopStudent, Integer> topStudentMap = new HashMap<>();

        // Iterate over each active student
        for (User student : activeStudents) {
            if (student.isEnabled()) {
                // Fetch the student's score for the current month
                Integer score = getTotalScoreByStudentsAndCurrentMonth(student);

                // Find the student's group
                Group group = groupRepository.findById(student.getGroupId()).orElse(null);
                if (group == null) {
                    // Skip this student if their group is not found
                    continue;
                }

                // Build the TopStudent DTO
                TopStudent topStudentDTO = TopStudent.builder()
                        .id(student.getId())
                        .fullName(student.getFirstName() + " " + student.getLastName())
                        .groupId(group.getId())
                        .groupName(group.getName())
                        .scoreMonth(score != null ? score : 0)  // Default to 0 if score is null
                        .build();

                // Map the student with their score
                topStudentMap.put(topStudentDTO, score != null ? score : 0);
            }
        }

        // Collect and sort the top 5 students by score
        List<TopStudent> topStudents = topStudentMap.entrySet().stream()
                .sorted(Map.Entry.<TopStudent, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // Return a response with the top students or an empty result message
        if (topStudents.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Top student"));
        }

        return new ApiResponse(topStudents);
    }


    public ApiResponse topGroupByTeacher(User teacher) {

        // Fetch all active groups by the teacher ID
        List<Group> groups = groupRepository.findAllByTeacherIdAndActiveTrue(teacher.getId());
        if (groups.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        // Map to store TopGroup objects and their scores
        Map<TopGroup, Integer> topGroupMap = new HashMap<>();

        // Iterate over each group
        for (Group group : groups) {
            // Get the total score for the current month for this group
            Integer score = getTotalScoreByGroupAndCurrentMonth(group.getId());

            // Calculate the number of students in the group
            int studentCount = (group.getStudents() != null) ? group.getStudents().size() : 0;

            // Build the TopGroup DTO
            TopGroup topGroupDTO = TopGroup.builder()
                    .id(group.getId())
                    .groupName(group.getName())
                    .scoreMonth(score != null ? score : 0)  // Default score to 0 if null
                    .studentCount(studentCount)
                    .build();

            // Map the group with its score
            topGroupMap.put(topGroupDTO, score != null ? score : 0);
        }

        // Collect and sort the top 5 groups by score in descending order
        List<TopGroup> topGroups = topGroupMap.entrySet().stream()
                .sorted(Map.Entry.<TopGroup, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        // Return a response with the top groups or an empty result message
        if (topGroups.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Top group"));
        }

        return new ApiResponse(topGroups);
    }


    public ApiResponse getCategoryStatistic() {
        List<Category> categories = categoryRepository.findAllByCategoryEnumAndActiveTrue(CategoryEnum.EDUCATION);
        if (categories.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category list"));
        }

        List<ResCategory> resCategoryList = new ArrayList<>();
        for (Category category : categories) {
            int lessonScore = 0;
            double percentage;
            int sum = 0;
            List<Group> groups = groupRepository.findAllByCategoryIdAndActiveTrue(category.getId());
            for (Group group : groups) {
                Integer score = getTotalScoreByGroupAndCurrentMonthByCategory(group.getId());
                Integer lessonsScores = getLessonsScore(group.getId());
                sum += (score != null ? score : 0);
                lessonScore += (lessonsScores != null ? lessonsScores : 0);
            }

            if (sum != 0 && lessonScore != 0) {
                percentage = (double) sum / lessonScore * 100;
            } else {
                percentage = 0.0;
            }
            ResCategory resCategory = ResCategory.builder()
                    .categoryName(category.getName())
                    .percentage(percentage)
                    .build();
            resCategoryList.add(resCategory);
        }

        return new ApiResponse(resCategoryList);
    }


    public ApiResponse getCategoryByYearlyStatistic() {
        List<CategoryStatistics> categoryStatistics = homeWorkRepository.findCategoryStatistics();
        if (categoryStatistics.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category "));
        }
        return new ApiResponse(categoryStatistics);
    }


    public ApiResponse getStatisticForTeacher(User teacher) {
        List<GroupStatistics> groupStatisticsByTeacher = homeWorkRepository.findGroupStatisticsByTeacher(teacher.getId());
        return new ApiResponse(groupStatisticsByTeacher);
    }


    public ApiResponse getStatisticStudent(User user) {
        List<StudentStatisticY> studentStatisticYearly = homeWorkRepository.findStudentStatisticYearly(user.getId());
        if (studentStatisticYearly.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Statistic "));
        }

        return new ApiResponse(studentStatisticYearly);
    }

    public ApiResponse getStatisticWeekly(User user) {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusWeeks(1);
        List<WeekStatistic> weekStatistics = homeWorkRepository.countHomeWorkByDayOfWeek(startTime, endTime, user.getId());
        return new ApiResponse(weekStatistics);
    }


    public Integer getTotalScoreByGroupAndCurrentMonth(Integer group_id) {

        return homeWorkRepository.findTotalScoreByGroupAndPeriod(group_id, startDate, endDate);
    }

    public Integer getTotalScoreByGroupAndCurrentMonthByCategory(Integer group_id) {
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1); // Oldingi oyning birinchi kuni
        LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1); // Joriy oyning birinchi kuni oldin
        return homeWorkRepository.findTotalScoreByGroupAndPeriod(group_id, startDate, endDate);
    }


    public Integer getTotalScoreByStudentsAndCurrentMonth(User student) {
        return homeWorkRepository.findTotalScoreByStudentsAndPeriod(student, startDate, endDate);
    }


    private Integer getLessonsScore(Integer groupId) {
        int lessonsScore = 0;
        LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1); // Oldingi oyning birinchi kuni
        LocalDate endDate = LocalDate.now().withDayOfMonth(1).minusDays(1);
        List<LessonTracking> list = lessonTrackingRepository.findByGroupIdAndActiveTrueAndCreatDateBetween(groupId, startDate, endDate);
        for (LessonTracking lessonTracking : list) {
            Lesson lesson = lessonTracking.getLesson();
            Integer i = taskRepository.countAllByLessonId(lesson.getId());
            lessonsScore += i * 5;
        }
        return lessonsScore;
    }


    public ApiResponse getCountAllQuiz() {
        Integer categoryCount = categoryRepository.countByCategoryEnum(CategoryEnum.QUIZ);
        int resultCount = (int) resultRepository.count();
        int userCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_USER);
        int questionCount = (int) questionRepository.count();
        Integer yomonCount = resultRepository.countAllByStatusCode(1);
        Integer yaxshiCount = resultRepository.countAllByStatusCode(2);
        Integer aloCount = resultRepository.countAllByStatusCode(3);
        Integer nowCount = resultRepository.countAllByCreatedAt(LocalDate.now());

        ResCountQuiz resCountQuiz = ResCountQuiz.builder()
                .categoryCount(categoryCount != null ? categoryCount : 0)
                .resultCount(resultCount)
                .userCount(userCount)
                .questionCount(questionCount)
                .badResultsCount(yomonCount != null ? yomonCount : 0)
                .goodResultsCount(yaxshiCount != null ? yaxshiCount : 0)
                .superResultsCount(aloCount != null ? aloCount : 0)
                .todayResultsCount(nowCount != null ? nowCount : 0)
                .build();

        return new ApiResponse(resCountQuiz);
    }


    public ApiResponse countResultsByDayOfWeek() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusWeeks(1);
        List<WeekStatistic> objects = resultRepository.countResultsByDayOfWeek(startTime, endTime);
        return new ApiResponse(objects);
    }


    public ApiResponse resultByPercentage() {
        List<Result> all = resultRepository.findAll();
//        if (all.isEmpty()) {
//            return new ApiResponse(ResponseError.NOTFOUND("Result"));
//        }

        Integer countAll = Math.toIntExact(resultRepository.count());
        Integer x = resultRepository.countAllByStatusCode(1);
        Integer y = resultRepository.countAllByStatusCode(2);
        Integer z = resultRepository.countAllByStatusCode(3);

//        // Handle case where countAll is 0 or null
//        if (countAll == null || countAll == 0) {
//            return new ApiResponse(ResponseError.NOTFOUND("Result"));
//        }

        List<ResResultP> resultPList = new ArrayList<>();

        // Calculate percentages safely with proper casting and null checks
        ResResultP result1 = ResResultP.builder()
                .status("Yomon")
                .percentage(x != null ? ((double) x / countAll) * 100 : 0)
                .build();
        resultPList.add(result1);

        ResResultP result2 = ResResultP.builder()
                .status("Yaxshi")
                .percentage(y != null ? ((double) y / countAll) * 100 : 0)
                .build();
        resultPList.add(result2);

        ResResultP result3 = ResResultP.builder()
                .status("A'lo")
                .percentage(z != null ? ((double) z / countAll) * 100 : 0)
                .build();
        resultPList.add(result3);

        return new ApiResponse(resultPList);
    }


    public ApiResponse countUserRoleAndPiece() {
        int userCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_USER);
        int teacherCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_TEACHER);
        int studentCount = userRepository.countByRoleAndEnabledTrue(ERole.ROLE_STUDENT);

        int countAllUser = userCount + teacherCount + studentCount + 1;

        List<ResDashboardUserP> userPList = new ArrayList<>();

        ResDashboardUserP userP = ResDashboardUserP.builder()
                .role(ERole.ROLE_USER.name())
                .piece(((double) userCount / countAllUser) * 100)
                .build();

        userPList.add(userP);

        ResDashboardUserP studentP = ResDashboardUserP.builder()
                .role(ERole.ROLE_STUDENT.name())
                .piece(((double) studentCount / countAllUser) * 100)
                .build();

        userPList.add(studentP);

        ResDashboardUserP teacherP = ResDashboardUserP.builder()
                .role(ERole.ROLE_TEACHER.name())
                .piece(((double) teacherCount / countAllUser) * 100)
                .build();

        userPList.add(teacherP);

        ResSiteAdminDashboard dashboard = ResSiteAdminDashboard.builder()
                .countUser(userCount)
                .countStudent(studentCount)
                .countTeacher(teacherCount)
                .countAll(countAllUser)
                .rolePiece(userPList)
                .build();

        return new ApiResponse(dashboard);
    }


    public ApiResponse getCountOnline() {
        Integer categoryCount = categoryRepository.countByCategoryEnum(CategoryEnum.ONLINE);
        Integer lessonCount = lessonRepository.countLesson(CategoryEnum.ONLINE.name());
        Integer moduleCount = moduleRepository.countAllByModule(CategoryEnum.ONLINE.name());
        Long studentCount = userProgressRepository.count();

        OnlineStatisticDto onlineStatisticDto = OnlineStatisticDto.builder()
                .categoryCount(categoryCount)
                .lessonCount(lessonCount)
                .moduleCount(moduleCount)
                .studentCount(studentCount)
                .build();
        return new ApiResponse(onlineStatisticDto);
    }
}
