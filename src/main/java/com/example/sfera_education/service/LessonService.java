package com.example.sfera_education.service;

import com.example.sfera_education.entity.Module;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.*;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.repository.*;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final FileRepository fileRepository;
    private final LessonTrackingRepository lessonTrackingRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final CategoryRepository categoryRepository;


    public ApiResponse saveLesson(LessonDTO lessonDTO) {
        Module module = moduleRepository.findById(lessonDTO.getModuleId()).orElse(null);
        if (module == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Module"));
        }

        File file = fileRepository.findById(lessonDTO.getFileId()).orElse(null);

        Lesson lesson = Lesson.builder()
                .name(lessonDTO.getName())
                .description(lessonDTO.getDescription())
                .module(module)
                .videoLink(lessonDTO.getVideoLink())
                .videoTime(lessonDTO.getVideoTime())
                .file(file)
                .deleted(false)
                .build();

        lessonRepository.save(lesson);

        return new ApiResponse("Success");
    }


    public ApiResponse getAllLessonsEdu(Integer moduleId, User user) {
        User student = userRepository.findById(user.getId()).orElse(null);
        if (student == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }


        List<Lesson> lessons = lessonRepository.findByModuleIdAndDeletedFalse(moduleId);
        if (lessons.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        List<LessonDTO> lessonDtoList = new ArrayList<>();

        for (Lesson lesson : lessons) {
            boolean active;
            if (user.getRole().equals(ERole.ROLE_STUDENT)) {
                LessonTracking lessonTracking = lessonTrackingRepository
                        .findByGroupIdAndLessonId(user.getGroupId(), lesson.getId());

                if (lessonTracking == null) {
                    active = false;
                } else {
                    active = lessonTracking.isActive();
                }
            } else {
                active = true;
            }

            LessonDTO lessonDto = LessonDTO.builder()
                    .id(lesson.getId())
                    .name(lesson.getName())
                    .description(lesson.getDescription())
                    .moduleId(lesson.getModule().getId())
                    .videoLink(lesson.getVideoLink())
                    .videoTime(lesson.getVideoTime())
                    .fileId(lesson.getFile() != null ? lesson.getFile().getId() : null)
                    .userActive(active)
                    .deleted(lesson.isDeleted())
                    .videoTime(lesson.getVideoTime())
                    .build();
            lessonDtoList.add(lessonDto);
        }
        return new ApiResponse(lessonDtoList);
    }


    public ApiResponse getOneLesson(Integer id, User user) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        if (user.getRole().equals(ERole.ROLE_STUDENT)) {
            boolean exists = lessonTrackingRepository.existsByGroupIdAndLessonIdAndActiveTrue(user.getGroupId(), id);
            if (!exists) {
                return new ApiResponse(ResponseError.ACCESS_DENIED());
            }
        }

        LessonDTO lessonDto = getLessonDto(lesson);

        return new ApiResponse(lessonDto);
    }


    public LessonDTO getLessonDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .name(lesson.getName())
                .description(lesson.getDescription())
                .moduleId(lesson.getModule().getId())
                .videoLink(lesson.getVideoLink())
                .videoTime(lesson.getVideoTime())
                .build();
    }


    public ApiResponse updateLesson(Integer id, LessonDTO lessonDto) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        File file = fileRepository.findById(lessonDto.getFileId()).orElse(null);

        lesson.setName(lessonDto.getName());
        lesson.setDescription(lessonDto.getDescription());
        lesson.setVideoLink(lessonDto.getVideoLink());
        lesson.setVideoTime(lessonDto.getVideoTime());
        lesson.setFile(file);
        lessonRepository.save(lesson);
        return new ApiResponse("Success");
    }


    @CacheEvict("lesson")
    public ApiResponse deleteLesson(Integer id) {
        Lesson lesson = lessonRepository.findById(id).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }
        lesson.setDeleted(true);
        lessonRepository.save(lesson);
        return new ApiResponse("Success");
    }


    @Cacheable("lesson")
    public ApiResponse getLesson(Integer lessonId) {
        Lesson lesson1;
        try {
            Thread.sleep(2000);
            lesson1 = lessonRepository.findByIdAndDeletedFalse(lessonId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (lesson1 == null) {
            return new ApiResponse(ResponseError.NOTFOUND(lessonId));
        }
        LessonDTO lessonDto = getLessonDto(lesson1);
        return new ApiResponse(lessonDto);
    }


    @Transactional
    public ApiResponse startCourse(Integer categoryId, User user) {
        log.info("start course started");
        Optional<Category> byId = categoryRepository.findById(categoryId);
        if (byId.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        Category category = byId.get();
        userRepository.addCategoryToUser(user.getId(), category.getId());
        List<Lesson> byCategory = lessonRepository.findByCategory(categoryId);
        Lesson firstLesson = lessonRepository.findFirstLessonByCategoryId(categoryId);
        for (Lesson l : byCategory) {
            boolean exist = userProgressRepository.existsByUserIdAndLessonId(user.getId(), l.getId());
            if (!exist) {
                UserProgress userProgress = UserProgress.builder()
                        .user(user)
                        .lesson(l)
                        .completed(firstLesson.getId().equals(l.getId()))
                        .build();
                userProgressRepository.save(userProgress);
            }
        }

        log.info("user progress saved");
        List<UserLessonDTO> userLessons = lessonRepository.findUserLessons(categoryId, user.getId());
        return new ApiResponse(userLessons);
    }


    public ApiResponse getUserCourses(User user) {
        List<Category> categories = categoryRepository.findAllByUserId(user.getId());
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category c : categories) {
            CategoryDTO categoryDTO = CategoryDTO.builder()
                    .id(c.getId())
                    .name(c.getName())
                    .description(c.getDescription())
                    .fileId(c.getFile() != null ? c.getFile().getId() : null)
                    .build();
            categoryDTOS.add(categoryDTO);
        }
        return new ApiResponse(categoryDTOS);
    }


    public ApiResponse getLessonByCategoryId(Integer categoryId, User user) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        if (!category.getCategoryEnum().equals(CategoryEnum.ONLINE)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Category type online emas!"));
        }

        List<Lesson> lessonList = lessonRepository.findAllByCategoryId(categoryId);
        List<LessonDTO> lessonDTOS = new ArrayList<>();

        Set<Integer> processedModules = new HashSet<>();

        for (Lesson l : lessonList) {
            boolean active = false;

            if (!processedModules.contains(l.getModule().getId())) {
                processedModules.add(l.getModule().getId());
                active = true;
            } else {
                UserProgress userProgress = userProgressRepository.findByUserIdAndLessonId(user.getId(), l.getId());
                if (userProgress != null) {
                    active = true;
                }
            }

            LessonDTO lessonDTO = lessonDTO(l, active);
            lessonDTOS.add(lessonDTO);
        }
        return new ApiResponse(lessonDTOS);
    }


    public ApiResponse searchLesson(String name, Long teacherId, Integer moduleId, Integer categoryId, CategoryEnum categoryEnum, int page, int size) {

        Page<Lesson> lessons = lessonRepository.searchLessons(name, teacherId, moduleId, categoryId, categoryEnum.name(), PageRequest.of(page, size));

        if (lessons.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        List<LessonDTO> lessonDTOS = new ArrayList<>();

        for (Lesson lesson : lessons) {
            lessonDTOS.add(lessonDTO(lesson, true));
        }

        ResPageable resPageable = new ResPageable();
        resPageable.setPage(page);
        resPageable.setSize(size);
        resPageable.setTotalPage(lessons.getTotalPages());
        resPageable.setTotalElements(lessons.getTotalElements());
        resPageable.setBody(lessonDTOS);

        return new ApiResponse(resPageable);
    }


    private LessonDTO lessonDTO(Lesson l, boolean active) {
        return LessonDTO.builder()
                .id(l.getId())
                .name(l.getName())
                .description(l.getDescription())
                .videoLink(l.getVideoLink())
                .videoTime(l.getVideoTime())
                .moduleId(l.getModule().getId())
                .moduleName(l.getModule().getName())
                .categoryName(l.getModule().getCategory().getName())
                .categoryId(l.getModule().getCategory().getId())
                .userActive(active)
                .fileId(l.getFile() != null ? l.getFile().getId() : null)
                .deleted(l.isDeleted())
                .build();
    }


}



