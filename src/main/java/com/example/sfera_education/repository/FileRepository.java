package com.example.sfera_education.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.File;

public interface FileRepository extends JpaRepository<File, Long> {

}
