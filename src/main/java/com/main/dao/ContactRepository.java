package com.main.dao;

import com.main.entities.Contact;
import com.main.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact,Integer> {
    @Query("from Contact as c where c.user.id=:userId order by id desc")
    public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
    public List<Contact> findByFirstNameContainingAndUser(String firstName, User user);
}
