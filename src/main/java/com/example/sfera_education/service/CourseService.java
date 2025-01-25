package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.repository.ModuleRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final ModuleRepository moduleRepository;

}
