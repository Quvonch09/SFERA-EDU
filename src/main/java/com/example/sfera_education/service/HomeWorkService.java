package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.HomeWorkDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.UserDTO;
import com.example.sfera_education.payload.res.ResHomeWork;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HomeWorkService {

    private final HomeWorkRepository homeWorkRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final GroupRepository groupRepository;
    private final NotificationService notificationService;


    public ApiResponse saveHomeWork(HomeWorkDTO homeWorkDTO, User user) {
        if (!user.getRole().equals(ERole.ROLE_STUDENT)) {
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }

        Task task = taskRepository.findById(homeWorkDTO.getTaskId()).orElse(null);
        if (task == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }

        HomeWork byTaskId = homeWorkRepository.findByTaskIdAndStudentId(homeWorkDTO.getTaskId(), user.getId());
        if (byTaskId != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Homework"));
        }

        File file = fileRepository.findById(homeWorkDTO.getFileId()).orElse(null);

        Group group = groupRepository.findById(user.getGroupId()).orElse(null);
        if (group == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Group"));
        }

        User teacher = group.getTeacher();


        HomeWork homeWork = HomeWork.builder()
                .student(user)
                .task(task)
                .file(file)
                .solution(homeWorkDTO.getSolution())
                .build();
        homeWorkRepository.save(homeWork);

        notificationService.saveNotification(
                teacher,
                "Hurmatli " + teacher.getFirstName() + " " + teacher.getLastName() + "!",
                "Sizga " + group.getName() + " guruh o'quvchisi " + user.getFirstName() +
                        " " + user.getLastName() + "\n" +
                        " topshiriq jo'natdi.",
                0L,
                false
        );

        return new ApiResponse("Success");
    }


    public ApiResponse getHomeWork(User user) {
        if (user.getRole().equals(ERole.ROLE_STUDENT)) {
            List<HomeWorkDTO> homeWorkDTOList = new ArrayList<>();
            for (HomeWork homeWork : homeWorkRepository.findByStudentId(user.getId())) {
                homeWorkDTOList.add(homeWorkDTO(homeWork));
            }
            return new ApiResponse(homeWorkDTOList);
        } else if (user.getRole().equals(ERole.ROLE_TEACHER)) {
            List<User> userList = userRepository.findAllByTeacherIdAndHomeworkIsCheckedFalse(user.getId());
            if (userList.isEmpty()) {
                return new ApiResponse(ResponseError.NOTFOUND("Tekshirilmagan homeworklar"));
            }
            List<UserDTO> userDTOList = new ArrayList<>();
            for (User userByHome : userList) {
                Group group = groupRepository.findById(userByHome.getGroupId()).orElse(null);
                UserDTO userDTO = UserDTO.builder()
                        .userId(userByHome.getId())
                        .firstName(userByHome.getFirstName())
                        .lastName(userByHome.getLastName())
                        .groupId(userByHome.getGroupId())
                        .groupName(group != null ? group.getName() : null)
                        .build();
                userDTOList.add(userDTO);
            }
            return new ApiResponse(userDTOList);
        }
        return new ApiResponse(ResponseError.ACCESS_DENIED());
    }


    public ApiResponse getAllHomeworkByStudent(Long studentId) {
        User user = userRepository.findById(studentId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        List<HomeWorkDTO> homeWorkDTOList = new ArrayList<>();
        for (HomeWork homeWork : homeWorkRepository.findByStudentIdAndCheckedFalse(studentId)) {
            homeWorkDTOList.add(homeWorkDTO(homeWork));
        }
        return new ApiResponse(homeWorkDTOList);
    }


    public ApiResponse getOneHomeWork(Integer homeWorkId) {
        HomeWork homeWork = homeWorkRepository.findById(homeWorkId).orElse(null);
        if (homeWork == null) {
            return new ApiResponse(ResponseError.NOTFOUND("HomeWork"));
        }
        return new ApiResponse(homeWorkDTO(homeWork));
    }


    public ApiResponse updateScore(User user, Integer homeWorkId, Integer score) {
        HomeWork homeWork = homeWorkRepository.findById(homeWorkId).orElse(null);
        if (homeWork == null) {
            return new ApiResponse(ResponseError.NOTFOUND("HomeWork"));
        }

        Group group = groupRepository.findById(homeWork.getStudent().getGroupId()).orElse(null);
        if (group != null && group.getTeacher().getId().equals(user.getId()) && user.getRole().equals(ERole.ROLE_TEACHER)) {
            if (score > 5) {
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Eng yuqori natija 5 ball"));
            }
            homeWork.setScore(score);
            homeWork.setDueDate(LocalDate.now());
            homeWork.setChecked(true);
            homeWork.setDueDate(LocalDate.now());
            homeWorkRepository.save(homeWork);

            notificationService.saveNotification(
                    homeWork.getStudent(),
                    "Tekshirildi!",
                    "Sizning " + homeWork.getTask().getName() + " topshirig'iga bergan javobingiz" +
                            homeWork.getScore() + " ball bilan baholandi!",
                    0L,
                    false
            );

            return new ApiResponse("Success");
        }
        return new ApiResponse(ResponseError.ACCESS_DENIED());
    }


    private HomeWorkDTO homeWorkDTO(HomeWork homeWork) {
        return HomeWorkDTO.builder()
                .id(homeWork.getId())
                .fileId(homeWork.getFile() != null ? homeWork.getFile().getId() : null)
                .taskId(homeWork.getTask().getId())
                .solution(homeWork.getSolution())
                .studentId(homeWork.getStudent().getId())
                .score(homeWork.getScore())
                .checked(homeWork.isChecked())
                .build();
    }

    public ApiResponse getAllHomeWork(User user, String keyword, Integer groupId, int page, int size) {
        User user2 = userRepository.findById(user.getId()).orElse(null);
        if (user2 == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }
        Page<HomeWork> allByFilter = homeWorkRepository.getAllByFilter(user2.getId(), keyword, groupId,
                PageRequest.of(page, size));
        if (allByFilter.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("No homeWork"));
        }
        List<ResHomeWork> resHomeWorkList = new ArrayList<>();
        for (HomeWork homeWork : allByFilter.getContent()) {
            ResHomeWork resHomeWork = ResHomeWork.builder()
                    .studentId(homeWork.getStudent().getId())
                    .firstName(homeWork.getStudent().getFirstName())
                    .lastName(homeWork.getStudent().getLastName())
                    .groupName(Objects.requireNonNull(groupRepository.findById(homeWork.getStudent().getGroupId()).orElse(null)).getName())
                    .homeworkId(homeWork.getId())
                    .build();
            resHomeWorkList.add(resHomeWork);
        }

        ResPageable resPageable = ResPageable.builder()
                .totalPage(allByFilter.getTotalPages())
                .totalElements(allByFilter.getTotalElements())
                .page(page)
                .size(size)
                .body(resHomeWorkList).build();
        return new ApiResponse(resPageable);
    }
}
