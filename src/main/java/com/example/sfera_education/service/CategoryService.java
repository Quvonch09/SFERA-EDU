package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.File;
import com.example.sfera_education.entity.QuizCategorySettings;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.entity.enums.ERole;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.CategoryDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.repository.*;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final QuizCategorySettingsRepository quizCategorySettingsRepository;
    private final FileRepository fileRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    public ApiResponse saveCategory(CategoryDTO categoryDTO, CategoryEnum categoryEnum) {
        if (categoryRepository.existsByNameIgnoreCaseAndCategoryEnum(categoryDTO.getName(), categoryEnum)) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category"));
        }

        File file = fileRepository.findById(categoryDTO.getFileId()).orElse(null);


        Category category = addCategory(categoryDTO, categoryEnum, file);
        if (category.getCategoryEnum().equals(CategoryEnum.QUIZ)) {
            saveQuizCategory(category);
        }
        return new ApiResponse("Category saved");
    }

    private void saveQuizCategory(Category category) {
        QuizCategorySettings categorySettings = QuizCategorySettings.builder()
                .durationTime(0)
                .countQuiz(0)
                .category(category)
                .build();
        quizCategorySettingsRepository.save(categorySettings);
    }


    public ApiResponse getOneCategory(Integer id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(category.getId());

                    CategoryDTO categoryDTO = parseCategoryDTO(
                            category,
                            settings != null ? settings.getCountQuiz() : null,
                            settings != null ? settings.getDurationTime() : null
                    );
                    return new ApiResponse(categoryDTO);
                })
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Category")));

    }

    public ApiResponse getAllCategories(CategoryEnum categoryEnum, User user) {
        List<Category> categoryList = categoryRepository.findAllByCategoryEnumAndActiveTrue(categoryEnum);
        if (categoryList.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categoryList) {
            if (category.getCategoryEnum().equals(CategoryEnum.QUIZ)) {
                QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(category.getId());
                if (user.getRole().equals(ERole.ROLE_TEACHER)) {
                    return new ApiResponse(ResponseError.ACCESS_DENIED());
                }

                if (user.getRole().equals(ERole.ROLE_STUDENT) || user.getRole().equals(ERole.ROLE_USER)) {
                    if (settings == null) {
                        continue;
                    }
                }

                categoryDTOList.add(parseCategoryDTO(category, settings != null ? settings.getCountQuiz() : null,
                        settings != null ? settings.getDurationTime() : null)
                );
            } else {
                categoryDTOList.add(parseCategoryDTO(category, null, null));
            }
        }
        return new ApiResponse(categoryDTOList);
    }


    public ApiResponse updateCategory(Integer id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        File file = null;
        if (categoryDTO.getFileId() != 0) {
            file = fileRepository.findById(categoryDTO.getFileId()).orElse(null);
            if (file == null) {
                return new ApiResponse(ResponseError.NOTFOUND("File"));
            }
        }
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setFile(file);
        category.setActive(true);
        categoryRepository.save(category);
        return new ApiResponse("Category updated");
    }


    public ApiResponse getCategoryByTeacher(User user) {
        User teacher = userRepository.findById(user.getId()).orElse(null);

        if (teacher == null || !teacher.getRole().equals(ERole.ROLE_TEACHER)) {
            return new ApiResponse(ResponseError.ACCESS_DENIED());
        }

        List<Category> categories = categoryRepository.findAllByTeacherId(teacher.getId());
        if (categories.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Category list"));
        }

        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categories) {
            categoryDTOList.add(parseCategoryDTO(category, null, null));
        }

        return new ApiResponse(categoryDTOList);
    }


    public ApiResponse deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        category.setActive(false);
        categoryRepository.save(category);
        return new ApiResponse("Category deleted");
    }


    private Category addCategory(CategoryDTO categoryDTO, CategoryEnum categoryEnum, File file) {
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .active(true)
                .categoryEnum(categoryEnum)
                .file(file)
                .build();
        return categoryRepository.save(category);
    }


    public CategoryDTO parseCategoryDTO(Category category, Integer countQuiz, Integer duration) {
        Integer modules = moduleRepository.countAllByCategory_IdAndDeletedFalse(category.getId());
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.isActive())
                .categoryEnum(category.getCategoryEnum().name())
                .fileId(category.getFile() != null ? category.getFile().getId() : 0)
                .moduleCount(modules)
                .countQuiz(countQuiz)
                .durationTime(duration)
                .build();
    }


}
