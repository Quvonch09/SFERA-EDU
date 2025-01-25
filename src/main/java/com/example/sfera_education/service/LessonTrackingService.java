package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Group;
import com.example.sfera_education.entity.Lesson;
import com.example.sfera_education.entity.LessonTracking;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.LessonTrackingDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.LessonRepository;
import com.example.sfera_education.repository.LessonTrackingRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonTrackingService {

    private final LessonTrackingRepository lessonTrackingRepository;
    private final GroupRepository groupRepository;
    private final LessonRepository lessonRepository;
    private final NotificationService notificationService;


    public ApiResponse saveLessonTracking(LessonTrackingDTO reqLessonTracking, User user) {
        boolean exists = lessonTrackingRepository.existsByGroupIdAndLessonIdAndActiveTrue(
                reqLessonTracking.getGroupId(), reqLessonTracking.getLessonId()
        );
        if (!exists) {
            Group group = groupRepository.findById(reqLessonTracking.getGroupId()).orElse(null);
            if (group == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Group"));
            }

            if (group.getTeacher().getId().equals(user.getId())) {
                Lesson lesson = lessonRepository.findById(reqLessonTracking.getLessonId()).orElse(null);
                if (lesson == null) {
                    return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
                }
                LessonTracking lessonTracking = LessonTracking.builder()
                        .group(group)
                        .lesson(lesson)
                        .active(reqLessonTracking.isActive())
                        .creatDate(LocalDate.now())
                        .build();
                lessonTrackingRepository.save(lessonTracking);

                for (User student : group.getStudents()) {
                    notificationService.saveNotification(
                            student,
                            "Eslatma!",
                            lesson.getName() + " darsi guruhingiz uchun ruxsat berildi.",
                            0L,
                            false
                    );
                }
                return new ApiResponse("Success");
            }
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Guruh teacherga tegishli emas"));
        }
        return new ApiResponse(ResponseError.ALREADY_EXIST("LessonTracking"));
    }


    public ApiResponse getLessonTracking(Integer id, User user) {
        LessonTracking lessonTracking = lessonTrackingRepository.findById(id).orElse(null);
        if (lessonTracking == null) {
            return new ApiResponse(ResponseError.NOTFOUND("LessonTracking"));
        }

        if (lessonTracking.getGroup().getTeacher().equals(user)) {
            LessonTrackingDTO lessonTrackingDTO = lessonTrackingDTO(lessonTracking);
            return new ApiResponse(lessonTrackingDTO);
        }
        return new ApiResponse(ResponseError.DEFAULT_ERROR("LessonTracking does not belong to the teacher!"));
    }


    public ApiResponse getTeacherByLessonTrackingList(User user) {
        List<LessonTracking> all = lessonTrackingRepository.findAll();
        if (all.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("LessonTracking"));
        }

        List<LessonTrackingDTO> list = all.stream().filter(lessonTracking ->
                lessonTracking.getGroup().getTeacher().getId().equals(user.getId())).map(this::lessonTrackingDTO).toList();
        return new ApiResponse(list);
    }


    public ApiResponse updateLessonTracking(Integer id, LessonTrackingDTO reqLessonTracking, User user) {
        LessonTracking lessonTracking = lessonTrackingRepository.findById(id).orElse(null);
        if (lessonTracking != null) {
            Group group = groupRepository.findById(reqLessonTracking.getGroupId()).orElse(null);
            if (group == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Group"));
            }
            Lesson lesson = lessonRepository.findById(reqLessonTracking.getLessonId()).orElse(null);
            if (lesson == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
            }

            if (user.getId().equals(group.getTeacher().getId())) {
                lessonTracking.setGroup(group);
                lessonTracking.setLesson(lesson);
                lessonTracking.setActive(reqLessonTracking.isActive());
                return new ApiResponse("Success");
            }
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }
        return new ApiResponse(ResponseError.NOTFOUND("LessonTracking"));
    }


    public ApiResponse deleteLessonTracking(Integer id) {
        LessonTracking lessonTracking = lessonTrackingRepository.findById(id).orElse(null);

        if (lessonTracking == null) {
            return new ApiResponse(ResponseError.NOTFOUND("LessonTracking"));
        }

        lessonTrackingRepository.deleteById(id);
        return new ApiResponse("Successfully deleted");
    }


    public LessonTrackingDTO lessonTrackingDTO(LessonTracking lessonTracking) {
        return LessonTrackingDTO.builder()
                .id(lessonTracking.getId())
                .lessonId(lessonTracking.getLesson().getId())
                .groupId(lessonTracking.getGroup().getId())
                .active(lessonTracking.isActive())
                .groupName(lessonTracking.getGroup().getName())
                .lessonName(lessonTracking.getLesson().getName())
                .build();
    }
}
