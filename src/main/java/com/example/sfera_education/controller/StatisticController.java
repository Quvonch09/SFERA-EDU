package com.example.sfera_education.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.security.CurrentUser;
import com.example.sfera_education.service.StatisticService;

@CrossOrigin
@RestController
@RequestMapping("/statistic")
@RequiredArgsConstructor
public class StatisticController {
    private final StatisticService statisticService;


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Edu Admin barcha narsaning sonini kurish")
    @GetMapping
    public ResponseEntity<ApiResponse> getStatistic() {
        ApiResponse allCount = statisticService.getAllCount();
        return ResponseEntity.ok(allCount);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Edu uchun AMDIN top teacherlarni kurish")
    @GetMapping("/top/Teacher")
    public ResponseEntity<ApiResponse> getTopTeacher() {
        ApiResponse topTeacher = statisticService.getTopTeacher();
        return ResponseEntity.ok(topTeacher);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Edu uchun ADMIN top studentlarni kurish")
    @GetMapping("/top/Student")
    public ResponseEntity<ApiResponse> getTopStudent() {
        ApiResponse topStudent = statisticService.getTopStudent();
        return ResponseEntity.ok(topStudent);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Edu uchun ADMIN top grouplarni kurish")
    @GetMapping("/top/group")
    public ResponseEntity<ApiResponse> getTopGroup() {
        ApiResponse topGroup = statisticService.getTopGroup();
        return ResponseEntity.ok(topGroup);
    }

    @Operation(summary = "Education uchun statistika  ADMIN ga category foizlardagi statistika")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/categoryPercentage")
    public ResponseEntity<ApiResponse> getCategoryStatistic() {
        ApiResponse categoryStatistic = statisticService.getCategoryStatistic();
        return ResponseEntity.ok(categoryStatistic);
    }

    @Operation(summary = "Education Admin uchun categorylar bo'yicha yillik statistika")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/categoryYearly")
    public ResponseEntity<ApiResponse> getCategoryStatisticYearly() {
        ApiResponse apiResponse = statisticService.getCategoryByYearlyStatistic();
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Teacher uchun guruhlarining yillik statistikasi")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @GetMapping("/teacher-dashboard")
    public ResponseEntity<ApiResponse> getStatisticForTeacher(@CurrentUser User user) {
        ApiResponse apiResponse = statisticService.getStatisticForTeacher(user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Education studentning yillik statistikasi")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/student-yearly-statistic")
    public ResponseEntity<ApiResponse> getStatisticStudent(@CurrentUser User user) {
        ApiResponse apiResponse = statisticService.getStatisticStudent(user);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Quiz uchun Admin barcha satatistikalarni sonlarda")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/countAll-quiz")
    public ResponseEntity<ApiResponse> getAllCountQuiz() {
        ApiResponse apiResponse = statisticService.getCountAllQuiz();
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Quiz uchun Admin haftalik statistika resultlarniki")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/weekly-statistic")
    public ResponseEntity<ApiResponse> getWeeklyStatistic() {
        ApiResponse apiResponse = statisticService.countResultsByDayOfWeek();
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Quiz uchun Admin resultlarni statusi boyicha foizlik statistika")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/percentage-resultStatus")
    public ResponseEntity<ApiResponse> getPercentage() {
        ApiResponse apiResponse = statisticService.resultByPercentage();
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Admin login qilganidan keyin ko'radigan userlar statistikalari")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/site-dashboard")
    public ResponseEntity<ApiResponse> dashboardAdmin() {
        ApiResponse apiResponse = statisticService.countUserRoleAndPiece();
        return ResponseEntity.ok(apiResponse);
    }


    @Operation(summary = "Edu student uchun haftalik uyga vazifalar soni")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    @GetMapping("/student-statistic")
    public ResponseEntity<ApiResponse> getStatisticForStudent(@CurrentUser User user) {
        ApiResponse statisticWeekly = statisticService.getStatisticWeekly(user);
        return ResponseEntity.ok(statisticWeekly);
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_STUDENT')")
    @Operation(summary = "Onlinedagi barcha narsaning soni")
    @GetMapping("/getOnlineCount")
    public ResponseEntity<ApiResponse> getOnlineCount() {
        ApiResponse countOnline = statisticService.getCountOnline();
        return ResponseEntity.ok(countOnline);
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "edu Teacher dashborti uchun student guruh va jami oqituvchilar soni")
    @GetMapping("/count-dashboard")
    public ResponseEntity<ApiResponse> getCountByTeacher(@CurrentUser User user) {
        return ResponseEntity.ok(statisticService.getTeacherCountStatistic(user));
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Edu uchun Teacher top grouplarini kurish")
    @GetMapping("/top/group-teacher")
    public ResponseEntity<ApiResponse> getTopGroupByTeacher(@CurrentUser User user) {
        ApiResponse topGroup = statisticService.topGroupByTeacher(user);
        return ResponseEntity.ok(topGroup);
    }


    @PreAuthorize("hasRole('ROLE_TEACHER')")
    @Operation(summary = "Edu uchun teacher top studentlarini kurish")
    @GetMapping("/top/student-teacher")
    public ResponseEntity<ApiResponse> getTopStudentByTeacher(@CurrentUser User user) {
        ApiResponse topStudent = statisticService.topStudentByTeacher(user);
        return ResponseEntity.ok(topStudent);
    }

}
