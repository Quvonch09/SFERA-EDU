package com.example.sfera_education.service;

import com.example.sfera_education.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.User;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.GroupStatistics;
import com.example.sfera_education.payload.res.ResPageable;
import com.example.sfera_education.repository.GroupRepository;
import com.example.sfera_education.repository.HomeWorkRepository;
import com.example.sfera_education.util.ResRateStudent;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateService {

    private final HomeWorkRepository homeWorkRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    public ApiResponse getGroupByYearlyStatistic()
    {
        List<GroupStatistics> groupStatistics = homeWorkRepository.findGroupStatistics();
        if(groupStatistics.isEmpty()){
            return new ApiResponse(ResponseError.NOTFOUND("Groups"));
        }
        return new ApiResponse(groupStatistics);
    }


    public ApiResponse getStudents(String keyword,Integer groupId,Integer categoryId, int page , int size)
    {

        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        Page<com.example.sfera_education.util.ResRateStudent> studentsWithRate =
                userRepository.findStudentsWithRate(keyword, groupId, categoryId,
                        startDate, endDate, PageRequest.of(page, size));

        ResPageable resPageable = pageable(studentsWithRate, page, size);

        return new ApiResponse(resPageable);
    }

    public ApiResponse getStudentsForTeacher(User teacher,Integer groupId,int page,int size)
    {

        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        Page<ResRateStudent> studentPage = userRepository.findStudentsForTeacher(teacher.getId(), groupId,
                startDate, endDate, PageRequest.of(page, size));

        return new ApiResponse(pageable(studentPage, page, size));

    }



    private ResPageable pageable(Page<com.example.sfera_education.util.ResRateStudent> students, int page, int size)
    {
        ResPageable resPageable = new ResPageable();
        resPageable.setPage(page);
        resPageable.setSize(size);
        resPageable.setTotalElements(students.getTotalElements());
        resPageable.setTotalPage(students.getTotalPages());
        resPageable.setBody(students.getContent());
        return resPageable;
    }
}
