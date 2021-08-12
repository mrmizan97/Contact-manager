package com.main.controller;

import com.main.dao.ContactRepository;
import com.main.dao.UserRepository;
import com.main.entities.Contact;
import com.main.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class SearchController {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private ContactRepository contactRepository;
    @GetMapping("/search/{query}")
    public ResponseEntity<?> searchContact(Principal principal, @PathVariable("query") String query){

        System.out.println(query);
        User user = this.userRepository.getUserByUserName(principal.getName());
        List<Contact> contacts = this.contactRepository.findByFirstNameContainingAndUser(query, user);
        return ResponseEntity.ok(contacts);
    }



}
