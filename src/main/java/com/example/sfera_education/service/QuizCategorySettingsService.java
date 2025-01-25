package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.QuizCategorySettings;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.QuizCategorySettingsDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.repository.CategoryRepository;
import com.example.sfera_education.repository.QuizCategorySettingsRepository;

@Service
@RequiredArgsConstructor
public class QuizCategorySettingsService {

    private final QuizCategorySettingsRepository quizCategorySettingsRepository;
    private final CategoryRepository categoryRepository;


    public ApiResponse saveQuizCategorySettings(Integer categoryId, QuizCategorySettingsDTO quizCategorySettingsDTO) {
        Category category = categoryRepository.findById(categoryId).orElse(null);

        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        QuizCategorySettings byCategoryId = quizCategorySettingsRepository.findByCategoryId(category.getId());

        if (byCategoryId != null) {
            return new ApiResponse(ResponseError.ALREADY_EXIST("Category setting"));
        }

        QuizCategorySettings quizCategorySettings = QuizCategorySettings.builder()
                .category(category)
                .countQuiz(quizCategorySettingsDTO.getCountQuiz())
                .durationTime(quizCategorySettingsDTO.getDurationTime())
                .build();

        quizCategorySettingsRepository.save(quizCategorySettings);

        return new ApiResponse("Category setting saved");
    }


    public ApiResponse updateQuizCategorySettings(Integer categoryId, QuizCategorySettingsDTO quizCategorySettingsDTO) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(categoryId);
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category setting"));
        }

        settings.setCategory(category);
        settings.setCountQuiz(quizCategorySettingsDTO.getCountQuiz());
        settings.setDurationTime(quizCategorySettingsDTO.getDurationTime());
        quizCategorySettingsRepository.save(settings);

        return new ApiResponse("Category setting updated");
    }


    public ApiResponse getQuizCategorySettings(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(category.getId());
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category setting"));
        }

        QuizCategorySettingsDTO settingsDTO = QuizCategorySettingsDTO.builder()
                .id(settings.getId())
                .countQuiz(settings.getCountQuiz())
                .durationTime(settings.getDurationTime())
                .build();

        return new ApiResponse(settingsDTO);
    }
}
