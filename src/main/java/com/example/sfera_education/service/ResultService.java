package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Result;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.ResultDTO;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.payload.res.ResResult;
import com.example.sfera_education.payload.res.ResResultP;
import com.example.sfera_education.repository.CategoryRepository;
import com.example.sfera_education.repository.ResultRepository;
import com.example.sfera_education.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ApiResponse getAllResults(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ApiResponse(ResponseError.NOTFOUND("User"));
        }

        List<Result> results = resultRepository.findAllByUserId(user.getId());

        if (results.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Result list"));
        }

        List<ResultDTO> list = results.stream().map(this::resultDTO).toList();
        return new ApiResponse(list);
    }


    public ApiResponse getResult(Long resultId) {
        Result result = resultRepository.findById(resultId).orElse(null);
        if (result == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }

        return new ApiResponse(resultDTO(result));
    }


    public ApiResponse countAllResults(User user) {
        Integer countResult = resultRepository.countAllByUserId(user.getId());
        Integer passedResultCount = resultRepository.countAllByResult(user);
        Integer resultCategoryCount = categoryRepository.countAllByResultAndUserId(user.getId(), "QUIZ");
        ResResult resResult = ResResult.builder()
                .countResult(countResult)
                .passedResultCount(passedResultCount)
                .resultCategoryCount(resultCategoryCount)
                .build();
        return new ApiResponse(resResult);
    }


    public ApiResponse getResultByPercentage(User user) {
        List<Result> allByUserId = resultRepository.findAllByUserId(user.getId());
        if (allByUserId.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }
        Integer countAll = resultRepository.countAllByUserId(user.getId());
        Integer x = resultRepository.countAllByUserAndStatusCode(user, 1);
        Integer y = resultRepository.countAllByUserAndStatusCode(user, 2);
        Integer z = resultRepository.countAllByUserAndStatusCode(user, 3);

        // Handle case where countAll is 0 or null
        if (countAll == null || countAll == 0) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }

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


    public ApiResponse searchResults(String name, String categoryName, Integer statusCode, int page, int size) {
        Page<Result> results = resultRepository.searchResults(name, categoryName, statusCode, PageRequest.of(page, size));
        if (results.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Result"));
        }
        List<ResultDTO> list = results.stream().map(this::resultDTO).toList();

        ResPageable resPageable = ResPageable.builder()
                .page(page)
                .size(size)
                .totalPage(results.getTotalPages())
                .totalElements(results.getTotalElements())
                .body(list)
                .build();

        return new ApiResponse(resPageable);
    }


    public ResultDTO resultDTO(Result result) {
        String status;
        if (result.getStatusCode() == 1) {
            status = "Yomon";
        } else if (result.getStatusCode() == 2) {
            status = "Yaxshi";
        } else {
            status = "A'lo";
        }
        return ResultDTO.builder()
                .id(result.getId())
                .userId(result.getUser().getId())
                .userName(result.getUser().getFirstname() + " " + result.getUser().getLastname())
                .categoryName(result.getCategoryName())
                .correctAnswer(result.getCorrectAnswer())
                .countAnswer(result.getCountAnswer())
                .duration(result.getDuration())
                .status(status)
                .createdAt(result.getCreatedAt())
                .build();
    }


}
