package com.example.sfera_education.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.sfera_education.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

}
