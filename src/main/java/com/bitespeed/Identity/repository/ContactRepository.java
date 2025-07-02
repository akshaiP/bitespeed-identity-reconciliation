package com.bitespeed.Identity.repository;

import com.bitespeed.Identity.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByEmail(String email);
    List<Contact> findByPhoneNumber(String phoneNumber);
    List<Contact> findByEmailOrPhoneNumber(String email, String phoneNumber);
}