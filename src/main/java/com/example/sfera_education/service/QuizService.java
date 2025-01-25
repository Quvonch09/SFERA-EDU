package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.*;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.OptionDto;
import com.example.sfera_education.payload.QuestionDto;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.AnswerDTO;
import com.example.sfera_education.payload.res.ResQuiz;
import com.example.sfera_education.repository.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final QuizCategorySettingsRepository quizCategorySettingsRepository;
    private final OptionRepository optionRepository;
    private final QuestionService questionService;
    private final ResultRepository resultRepository;
    private final ResultService resultService;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;
    private final NotificationService notificationService;


    public ApiResponse startQuiz(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(categoryId);
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category settings"));
        }

        List<Question> questions = questionRepository
                .findByCategoryIdAndRandom(category.getId(), settings.getCountQuiz());

        if (questions.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }

        ResQuiz resQuiz = ResQuiz.builder()
                .countQuestion(settings.getCountQuiz())
                .duration(settings.getDurationTime())
                .questionDtoList(questionDtoList(questions))
                .build();

        return new ApiResponse(resQuiz);
    }


    public ApiResponse passQuizForLesson(Integer lessonId, Integer nextLessonId, User user,
                                         List<AnswerDTO> answers) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        int correctAnswers = 0;
        for (AnswerDTO answerDTO : answers) {
            Option option = optionRepository.findByQuestionIdAndCorrectTrue(answerDTO.getQuestionId());
            if (option != null) {
                if (answerDTO.getOptionId().equals(option.getId()))
                    correctAnswers++;
            }
        }


        int check = (correctAnswers / questionRepository.countAllByLessonId(lesson.getId())) * 100;

        if (check >= 60) {

            Lesson nextLesson = lessonRepository.findById(nextLessonId).orElse(null);
            if (nextLesson == null) {
                return new ApiResponse(ResponseError.NOTFOUND(nextLessonId));
            }

            boolean exists = userProgressRepository.existsByUserIdAndLessonId(user.getId(), nextLesson.getId());
            if (!exists) {
                UserProgress userProgress = UserProgress.builder()
                        .lesson(nextLesson)
                        .user(user)
                        .completed(true)
                        .build();
                userProgressRepository.save(userProgress);
            }

            return new ApiResponse("Successfully passed quiz");
        }
        return new ApiResponse(ResponseError.DEFAULT_ERROR("O'tolmadingiz!"));
    }

    public ApiResponse passQuiz(Integer categoryId, Integer durationTime, User user, List<AnswerDTO> answers, Integer countQuiz) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category"));
        }

        QuizCategorySettings settings = quizCategorySettingsRepository.findByCategoryId(categoryId);
        if (settings == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Category settings"));
        }

        Integer correctAnswers = 0;

        for (AnswerDTO answer : answers) {
            Option option = optionRepository.findByQuestionIdAndCorrectTrue(answer.getQuestionId());
            if (option.getId().equals(answer.getOptionId())) {
                correctAnswers++;
            }
        }

        double score = ((double) correctAnswers / countQuiz) * 100;

        int code;

        if (score <= 60) {
            code = 1;
        } else if (score >= 65 && score <= 86) {
            code = 2;
        } else {
            code = 3;
        }

        Result result = Result.builder()
                .categoryName(category.getName())
                .user(user)
                .correctAnswer(correctAnswers)
                .createdAt(LocalDate.now())
                .duration(durationTime)
                .countAnswer(settings.getCountQuiz())
                .statusCode(code)
                .build();
        Result save = resultRepository.save(result);

        notificationService.saveNotification(
                user,
                "Tabriklaymiz!",
                "Siz " + category.getName() + " yo'nalishi boyicha " + settings.getCountQuiz() + " ta testdan " +
                        correctAnswers + " ta to'g'ri javob bilan " + score + "% natija ko'rsatdingiz!",
                0L,
                false
        );

        return new ApiResponse(resultService.resultDTO(save));

    }


    private List<QuestionDto> questionDtoList(List<Question> questions) {
        List<QuestionDto> questionDtoList = new ArrayList<>();
        for (Question question : questions) {
            List<OptionDto> optionDtoList = new ArrayList<>();
            for (Option option : optionRepository.findAllByQuestionId(question.getId())) {
                optionDtoList.add(questionService.optionDto(option, question.getId()));
            }
            questionDtoList.add(questionService.questionDto(question, optionDtoList));
        }
        return questionDtoList;
    }
}
