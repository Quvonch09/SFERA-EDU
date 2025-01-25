package com.example.sfera_education.payload.res;

import com.example.sfera_education.payload.QuestionDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResQuiz {
    private Integer duration;
    private Integer countQuestion;
    private List<QuestionDto> questionDtoList;
}
