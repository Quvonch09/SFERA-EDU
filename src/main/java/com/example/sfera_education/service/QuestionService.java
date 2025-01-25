package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Category;
import com.example.sfera_education.entity.Lesson;
import com.example.sfera_education.entity.Option;
import com.example.sfera_education.entity.Question;
import com.example.sfera_education.entity.enums.CategoryEnum;
import com.example.sfera_education.payload.*;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.repository.CategoryRepository;
import com.example.sfera_education.repository.LessonRepository;
import com.example.sfera_education.repository.OptionRepository;
import com.example.sfera_education.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final CategoryRepository categoryRepository;
    private final LessonRepository lessonRepository;


    public ApiResponse saveQuestions(Integer categoryId, Integer lessonId, QuestionDto questionDto) {

        Category category = null;
        if (categoryId != 0 && lessonId == 0) {
            category = categoryRepository.findById(categoryId).orElse(null);
            if (category == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Category"));
            }
            if (!category.getCategoryEnum().equals(CategoryEnum.QUIZ)) {
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Category type QUIZ emas!"));
            }
        }

        Lesson lesson = null;
        if (lessonId != 0 && categoryId == 0) {
            lesson = lessonRepository.findById(lessonId).orElse(null);
            if (lesson == null) {
                return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
            }
            if (!lesson.getModule().getCategory().getCategoryEnum().equals(CategoryEnum.ONLINE)) {
                return new ApiResponse(ResponseError.DEFAULT_ERROR("Lesson Online Categoryga tegishli emas!"));
            }
        }

        if (checkQuestion(questionDto)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Not saved"));
        }

        Question save = questionRepository.save(saveQuestion(questionDto, category, lesson));

        for (OptionDto optionDto : questionDto.getOptionDto()) {
            optionRepository.save(addOption(optionDto, save));
        }

        return new ApiResponse("Success");
    }


    public ApiResponse getOneQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }

        List<Option> allByQuestionId = optionRepository.findAllByQuestionId(questionId);
        if (allByQuestionId.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Options"));
        }

        List<Option> allByQuestionList = optionRepository.findAllByQuestionId(questionId);
        List<OptionDto> list = allByQuestionList.stream().map(option -> optionDto(option, questionId)).toList();
        return new ApiResponse(questionDto(question, list));
    }


    public ApiResponse searchQuestionByQuizCategory(
            String questionName, Integer categoryId, Integer lessonId, CategoryEnum categoryEnum, Integer page, Integer size) {
        PageRequest request = PageRequest.of(page, size);

        Page<Question> questions = questionRepository.searchNameAndCategoryId(questionName, categoryId, lessonId, categoryEnum.name(), request);

        List<QuestionDto> questionDtos = new ArrayList<>();
        List<OptionDto> optionDtos = new ArrayList<>();
        if (questions.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Questions"));
        }

        for (Question question : questions.getContent()) {
            for (Option option : question.getOptions()) {
                optionDtos.add(optionDto(option, option.getQuestion().getId()));
            }

            questionDtos.add(questionDto(question, optionDtos));
        }

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(questions.getTotalPages())
                .totalElements(questions.getTotalElements())
                .body(questionDtos)
                .build();
        return new ApiResponse(resPageable);

    }


    public ApiResponse getQuestionListByLesson(Integer lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Lesson"));
        }

        List<Question> questions = questionRepository.findAllByLessonId(lesson.getId());
        if (questions.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Question list"));
        }

        List<QuestionDto> questionList = new ArrayList<>();

        for (Question question : questions) {

            List<Option> allByQuestionId = optionRepository.findAllByQuestionId(question.getId());

            List<OptionDto> optionDtoList = new ArrayList<>();

            if (allByQuestionId.isEmpty()) {
                optionDtoList = null;
            } else {
                for (Option option : allByQuestionId) {
                    optionDtoList.add(optionDto(option, option.getQuestion().getId()));
                }
            }

            questionList.add(questionDto(question, optionDtoList));
        }
        return new ApiResponse(questionList);
    }


    public ApiResponse updateQuestion(Integer questionId, QuestionDto questionDto) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }

        if (checkQuestion(questionDto)) {
            return new ApiResponse(ResponseError.DEFAULT_ERROR("Not saved"));
        }

        optionRepository.deleteAll(optionRepository.findAllByQuestionId(question.getId()));

        question.setName(questionDto.getName());
        Question save = questionRepository.save(question);

        for (OptionDto optionDto : questionDto.getOptionDto()) {
            optionRepository.save(addOption(optionDto, save));
        }
        return new ApiResponse("Success");
    }


    public ApiResponse deleteQuestion(Integer questionId) {
        Question question = questionRepository.findById(questionId).orElse(null);
        if (question == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Question"));
        }
        questionRepository.delete(question);
        return new ApiResponse("Success");
    }


    private Question saveQuestion(QuestionDto questionDto, Category category, Lesson lesson) {
        return Question.builder()
                .name(questionDto.getName())
                .category(category)
                .lesson(lesson)
                .build();
    }


    private Option addOption(OptionDto optionDto, Question save) {
        return Option.builder()
                .answer(optionDto.getAnswer())
                .question(save)
                .correct(optionDto.getIsCorrect())
                .build();
    }


    private boolean checkQuestion(QuestionDto questionDto) {
        boolean chekquest = false;
        QuestionHelp result = forOption(questionDto);

        if (result.getCountTrue() == 1 && result.getCountOption() >= 2) {
            chekquest = true;
        }
        return !chekquest;
    }


    private QuestionHelp forOption(QuestionDto questionDto) {
        int countTrue = 0, countOption = 0;
        for (OptionDto optionDto : questionDto.getOptionDto()) {
            if (optionDto.getIsCorrect()) {
                countTrue++;
            }
            countOption++;
        }
        return new QuestionHelp(countTrue, countOption);
    }


    public QuestionDto questionDto(Question question, List<OptionDto> optionDto) {
        return QuestionDto.builder()
                .id(question.getId())
                .name(question.getName())
                .categoryId(question.getCategory() != null ? question.getCategory().getId() : null)
                .lessonId(question.getLesson() != null ? question.getLesson().getId() : null)
                .categoryName(question.getCategory() != null ? question.getCategory().getName() : null)
                .lessonName(question.getLesson() != null ? question.getLesson().getName() : null)
                .optionDto(optionDto)
                .build();
    }


    public OptionDto optionDto(Option option, Integer questionId) {
        return OptionDto.builder()
                .id(option.getId())
                .answer(option.getAnswer())
                .questionId(questionId)
                .isCorrect(option.isCorrect())
                .build();
    }
}
