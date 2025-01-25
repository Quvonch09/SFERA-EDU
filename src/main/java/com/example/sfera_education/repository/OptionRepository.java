package com.example.sfera_education.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.Option;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Integer> {

    List<Option> findAllByQuestionId(Integer id);

    Option findByQuestionIdAndCorrectTrue(Integer id);
}
