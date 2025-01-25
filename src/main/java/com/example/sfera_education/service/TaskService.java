package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.TaskDto;
import com.example.sfera_education.repository.*;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final FileRepository fileRepository;
    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;
    private final HomeWorkRepository homeWorkRepository;
    private final UserRepository userRepository;

    public ApiResponse saveTask(TaskDto taskDto, Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);

        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        Task task = Task.builder()
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .file(fileRepository.findById(taskDto.getFileId()).orElse(null))
                .lesson(lesson)
                .build();
        taskRepository.save(task);

        return new ApiResponse("Success");
    }


    public ApiResponse getAllTasks(User user, Integer lessonId) {
        if (user.getRole().name().equals("ROLE_STUDENT")) {
            Group group = groupRepository.findById(user.getGroupId()).orElse(null);
            if (group == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Group"));
            }
        }

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        List<TaskDto> taskList = getTaskList(lesson.getId(), user);
        return new ApiResponse(taskList);
    }


    public ApiResponse getOneTask(Integer taskId, User user) {
        Task task = taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }

        User student = userRepository.findById(user.getId()).orElse(null);

        if (student == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        boolean exists = true;

        if (student.getRole().name().equals("ROLE_STUDENT")) {
            exists = homeWorkRepository.existsByTaskIdAndStudentId(taskId, user.getId());
        }

        TaskDto taskDto = parsTaskDto(task, exists);

        return new ApiResponse(taskDto);
    }


    public ApiResponse updateTask(TaskDto taskDto, Integer taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        if (task == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }

        task.setName(taskDto.getName());
        task.setDescription(taskDto.getDescription());
        task.setFile(fileRepository.findById(taskDto.getFileId()).orElse(null));
        taskRepository.save(task);

        return new ApiResponse("Task updated");
    }


    public ApiResponse deleteTask(Integer taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Task"));
        }

        taskRepository.delete(task);
        return new ApiResponse("Task deleted");
    }


    private List<TaskDto> getTaskList(Integer lessonId, User user) {
        List<Task> tasks = taskRepository.findAllByLessonId(lessonId);
        if (tasks.isEmpty()) {
            return null;
        }

        List<TaskDto> taskDtoList = new ArrayList<>();
        for (Task task : tasks) {
            boolean exists = homeWorkRepository.existsByTaskIdAndStudentId(task.getId(), user.getId());
            taskDtoList.add(parsTaskDto(task, exists));
        }

        return taskDtoList;
    }


    private TaskDto parsTaskDto(Task task, boolean send) {
        return TaskDto.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .fileId(task.getFile() != null ? task.getFile().getId() : null)
                .lessonId(task.getLesson().getId())
                .send(send)
                .build();
    }
}
